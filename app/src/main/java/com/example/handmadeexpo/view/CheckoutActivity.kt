package com.example.handmadeexpo.view

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.handmadeexpo.R
import com.example.handmadeexpo.model.OrderItem
import com.example.handmadeexpo.model.OrderModel
import com.example.handmadeexpo.ui.theme.MainColor
import com.example.handmadeexpo.viewmodel.CheckoutViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class CheckoutActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val cartItems = intent.getSerializableExtra("cartItems") as? ArrayList<OrderItem>
        val isFromCart = intent.getBooleanExtra("isFromCart", false)

        val finalItems = if (cartItems != null && cartItems.isNotEmpty()) {
            cartItems.toList()
        } else {
            val pId = intent.getStringExtra("productId") ?: ""
            val name = intent.getStringExtra("name") ?: "Unknown"
            val price = intent.getDoubleExtra("price", 0.0)
            val img = intent.getStringExtra("image") ?: ""
            if (pId.isNotEmpty()) listOf(OrderItem(pId, name, price, 1, img)) else emptyList()
        }

        setContent {
            CheckoutUI(
                initialItems = finalItems,
                isFromCart = isFromCart,
                onBackClick = { finish() }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutUI(
    initialItems: List<OrderItem>,
    isFromCart: Boolean,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val viewModel: CheckoutViewModel = viewModel()

    LaunchedEffect(Unit) { viewModel.fetchUserInfo() }

    val currentDate = remember {
        SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())
    }

    var orderItems by remember { mutableStateOf(initialItems) }
    var selectedPaymentMethod by remember { mutableStateOf("COD") }
    var isPlacingOrder by remember { mutableStateOf(false) }

    var customerName by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var deliveryDate by remember { mutableStateOf("") }

    var showAddressDialog by remember { mutableStateOf(false) }
    var tempAddress by remember { mutableStateOf("") }
    var tempPhone by remember { mutableStateOf("") }
    var tempDeliveryDate by remember { mutableStateOf("") }

    var showStockError by remember { mutableStateOf(false) }
    var stockErrorMessage by remember { mutableStateOf("") }

    val fetchedName by viewModel.currentUserName
    LaunchedEffect(fetchedName) { if (fetchedName.isNotEmpty()) customerName = fetchedName }

    val subtotal = orderItems.sumOf { it.price * it.quantity }
    val deliveryFee = if (orderItems.isNotEmpty()) 200.0 else 0.0
    val total = subtotal + deliveryFee

    val calendar = Calendar.getInstance()
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            calendar.set(year, month, dayOfMonth)
            val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            tempDeliveryDate = sdf.format(calendar.time)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    ).apply {
        datePicker.minDate = Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, 1) }.timeInMillis
    }

    // Modern Stock Error Dialog
    if (showStockError) {
        AlertDialog(
            onDismissRequest = { showStockError = false },
            icon = {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(Color(0xFFFF9800).copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Warning,
                        contentDescription = null,
                        tint = Color(0xFFFF9800),
                        modifier = Modifier.size(28.dp)
                    )
                }
            },
            title = {
                Text(
                    "Stock Unavailable",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color(0xFF212121)
                )
            },
            text = {
                Text(
                    stockErrorMessage,
                    fontSize = 14.sp,
                    color = Color(0xFF616161),
                    textAlign = TextAlign.Center
                )
            },
            confirmButton = {
                Button(
                    onClick = { showStockError = false; onBackClick() },
                    colors = ButtonDefaults.buttonColors(containerColor = MainColor),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("OK", fontWeight = FontWeight.Bold)
                }
            },
            shape = RoundedCornerShape(20.dp),
            containerColor = Color.White
        )
    }

    // Modern Address/Shipping Dialog
    if (showAddressDialog) {
        AlertDialog(
            onDismissRequest = { showAddressDialog = false },
            icon = {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(MainColor.copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.LocalShipping,
                        contentDescription = null,
                        tint = MainColor,
                        modifier = Modifier.size(28.dp)
                    )
                }
            },
            title = {
                Text(
                    "Shipping Details",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color(0xFF212121)
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = tempAddress,
                        onValueChange = { tempAddress = it },
                        label = { Text("Delivery Address") },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("House no., Street, Area") },
                        shape = RoundedCornerShape(12.dp),
                        leadingIcon = {
                            Icon(Icons.Default.LocationOn, null, tint = MainColor)
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MainColor,
                            unfocusedBorderColor = Color(0xFFE0E0E0)
                        )
                    )

                    OutlinedTextField(
                        value = tempPhone,
                        onValueChange = { if (it.all { c -> c.isDigit() } && it.length <= 10) tempPhone = it },
                        label = { Text("Phone Number") },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("10-digit mobile") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(12.dp),
                        leadingIcon = {
                            Icon(Icons.Default.Phone, null, tint = MainColor)
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MainColor,
                            unfocusedBorderColor = Color(0xFFE0E0E0)
                        )
                    )

                    OutlinedTextField(
                        value = tempDeliveryDate,
                        onValueChange = { },
                        label = { Text("Delivery Date") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { datePickerDialog.show() },
                        readOnly = true,
                        enabled = false,
                        placeholder = { Text("Select date") },
                        shape = RoundedCornerShape(12.dp),
                        leadingIcon = {
                            Icon(Icons.Default.CalendarToday, null, tint = MainColor)
                        },
                        trailingIcon = {
                            IconButton(onClick = { datePickerDialog.show() }) {
                                Icon(Icons.Default.EditCalendar, null, tint = MainColor)
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledTextColor = Color(0xFF212121),
                            disabledContainerColor = Color.Transparent,
                            disabledBorderColor = Color(0xFFE0E0E0),
                            disabledLeadingIconColor = MainColor,
                            disabledTrailingIconColor = MainColor
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (tempAddress.isBlank() || tempPhone.length != 10 || tempDeliveryDate.isBlank()) {
                            Toast.makeText(context, "Please fill all details correctly", Toast.LENGTH_SHORT).show()
                        } else {
                            address = tempAddress
                            phoneNumber = tempPhone
                            deliveryDate = tempDeliveryDate
                            showAddressDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(MainColor),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Save Details", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showAddressDialog = false },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFF757575)
                    )
                ) {
                    Text("Cancel", fontWeight = FontWeight.Medium)
                }
            },
            shape = RoundedCornerShape(20.dp),
            containerColor = Color.White
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F7FA))
    ) {
        // Modern Header
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(4.dp, RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color(0xFFF5F5F5), CircleShape)
                    ) {
                        Icon(
                            painterResource(id = R.drawable.outline_arrow_back_ios_24),
                            contentDescription = "Back",
                            tint = Color(0xFF212121)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        "Back",
                        fontSize = 14.sp,
                        color = Color(0xFF757575)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(MainColor.copy(alpha = 0.15f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.ShoppingCart,
                            contentDescription = null,
                            tint = MainColor,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            "Checkout",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF212121)
                        )
                        Text(
                            "${orderItems.size} ${if (orderItems.size == 1) "item" else "items"}",
                            fontSize = 13.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
        }

        // Content
        Box(modifier = Modifier.weight(1f)) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Shipping Details Section
                item {
                    SectionHeader(
                        icon = Icons.Default.LocalShipping,
                        title = "Shipping Details",
                        iconColor = MainColor
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    ModernShippingCard(
                        customerName = customerName,
                        address = address,
                        phoneNumber = phoneNumber,
                        deliveryDate = deliveryDate,
                        onClick = {
                            tempAddress = address
                            tempPhone = phoneNumber
                            tempDeliveryDate = deliveryDate
                            showAddressDialog = true
                        }
                    )
                }

                // Items Section
                item {
                    SectionHeader(
                        icon = Icons.Default.Inventory,
                        title = "Your Items",
                        iconColor = Color(0xFF4CAF50)
                    )
                }

                items(orderItems) { item ->
                    ModernOrderItemCard(
                        item = item,
                        onDeleteClick = {
                            if (orderItems.size > 1) {
                                orderItems = orderItems.toMutableList().apply { remove(item) }
                            } else {
                                onBackClick()
                            }
                        }
                    )
                }

                // Payment Method Section
                item {
                    SectionHeader(
                        icon = Icons.Default.Payment,
                        title = "Payment Method",
                        iconColor = Color(0xFF9C27B0)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    ModernPaymentCard(
                        selectedMethod = selectedPaymentMethod,
                        onMethodSelected = { selectedPaymentMethod = it }
                    )
                }

                // Order Summary Section
                item {
                    SectionHeader(
                        icon = Icons.Default.Receipt,
                        title = "Order Summary",
                        iconColor = Color(0xFF1E88E5)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    ModernSummaryCard(
                        subtotal = subtotal,
                        deliveryFee = deliveryFee,
                        total = total
                    )
                }

                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }

        // Bottom Bar
        if (orderItems.isNotEmpty()) {
            ModernCheckoutBottomBar(
                total = total,
                isPlacingOrder = isPlacingOrder,
                onPlaceOrder = {
                    if (address.isBlank() || phoneNumber.length != 10 || deliveryDate.isBlank()) {
                        tempAddress = address
                        tempPhone = phoneNumber
                        tempDeliveryDate = deliveryDate
                        showAddressDialog = true
                        return@ModernCheckoutBottomBar
                    }

                    isPlacingOrder = true
                    val newOrder = OrderModel(
                        customerName = customerName,
                        address = address,
                        phone = phoneNumber,
                        items = orderItems,
                        totalPrice = total,
                        paymentMethod = selectedPaymentMethod,
                        status = "Pending",
                        orderDate = currentDate,
                        deliveryDate = deliveryDate
                    )

                    viewModel.placeOrder(newOrder) { success, msg ->
                        isPlacingOrder = false
                        if (success) {
                            val userId = FirebaseAuth.getInstance().currentUser?.uid
                            if (userId != null) {
                                val db = FirebaseDatabase.getInstance().reference
                                if (isFromCart) db.child("carts").child(userId).removeValue()
                                orderItems.forEach { item ->
                                    db.child("Bargains").child(userId).child(item.productId).removeValue()
                                }
                            }
                            Toast.makeText(context, "Order Placed Successfully!", Toast.LENGTH_LONG).show()
                            onBackClick()
                        } else {
                            stockErrorMessage = msg
                            showStockError = true
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun SectionHeader(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, iconColor: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            icon,
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF212121)
        )
    }
}

@Composable
fun ModernShippingCard(
    customerName: String,
    address: String,
    phoneNumber: String,
    deliveryDate: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .shadow(2.dp, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(
            containerColor = if (address.isBlank()) Color(0xFFFFF3E0) else Color.White
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        if (address.isBlank()) Color(0xFFFF9800).copy(alpha = 0.2f)
                        else MainColor.copy(alpha = 0.15f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    if (address.isBlank()) Icons.Default.Add else Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = if (address.isBlank()) Color(0xFFFF9800) else MainColor,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    customerName.ifEmpty { "Customer" },
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFF212121)
                )
                if (address.isBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Tap to add shipping details",
                        color = Color(0xFFE65100),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                } else {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        address,
                        color = Color(0xFF616161),
                        fontSize = 14.sp,
                        maxLines = 2
                    )
                    Text(
                        phoneNumber,
                        color = Color(0xFF9E9E9E),
                        fontSize = 13.sp
                    )
                    if (deliveryDate.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Surface(
                            color = MainColor.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                "Delivery: $deliveryDate",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MainColor
                            )
                        }
                    }
                }
            }
            Icon(
                Icons.Default.Edit,
                contentDescription = "Edit",
                tint = Color(0xFF9E9E9E),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun ModernOrderItemCard(item: OrderItem, onDeleteClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Card(
                modifier = Modifier.size(70.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                AsyncImage(
                    model = item.imageUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    error = painterResource(R.drawable.img_1)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    item.productName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color(0xFF212121),
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "NRP ${item.price.toInt()}",
                        color = MainColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        " × ${item.quantity}",
                        color = Color(0xFF9E9E9E),
                        fontSize = 14.sp
                    )
                }
            }
            IconButton(
                onClick = onDeleteClick,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF44336).copy(alpha = 0.1f))
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = Color(0xFFF44336),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun ModernPaymentCard(selectedMethod: String, onMethodSelected: (String) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            ModernPaymentOption(
                label = "Cash on Delivery",
                value = "COD",
                icon = Icons.Default.LocalAtm,
                selectedValue = selectedMethod,
                onSelect = onMethodSelected
            )
            Divider(color = Color(0xFFEEEEEE))
            ModernPaymentOption(
                label = "eSewa Wallet",
                value = "eSewa",
                icon = Icons.Default.AccountBalanceWallet,
                selectedValue = selectedMethod,
                onSelect = onMethodSelected
            )
        }
    }
}

@Composable
fun ModernPaymentOption(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selectedValue: String,
    onSelect: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect(value) }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = (value == selectedValue),
            onClick = { onSelect(value) },
            colors = RadioButtonDefaults.colors(selectedColor = MainColor)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Icon(
            icon,
            contentDescription = null,
            tint = if (value == selectedValue) MainColor else Color(0xFF9E9E9E),
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            label,
            fontSize = 15.sp,
            fontWeight = if (value == selectedValue) FontWeight.Bold else FontWeight.Normal,
            color = if (value == selectedValue) Color(0xFF212121) else Color(0xFF616161)
        )
    }
}

@Composable
fun ModernSummaryCard(subtotal: Double, deliveryFee: Double, total: Double) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            SummaryRow("Subtotal", "NRP ${subtotal.toInt()}", false)
            Spacer(modifier = Modifier.height(8.dp))
            SummaryRow("Delivery Fee", "NRP ${deliveryFee.toInt()}", false)
            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = Color(0xFFEEEEEE), thickness = 1.dp)
            Spacer(modifier = Modifier.height(12.dp))
            SummaryRow("Total Amount", "NRP ${total.toInt()}", true)
        }
    }
}

@Composable
fun SummaryRow(label: String, value: String, isTotal: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            label,
            fontSize = if (isTotal) 17.sp else 15.sp,
            fontWeight = if (isTotal) FontWeight.Bold else FontWeight.Normal,
            color = if (isTotal) Color(0xFF212121) else Color(0xFF616161)
        )
        Text(
            value,
            color = if (isTotal) MainColor else Color(0xFF212121),
            fontSize = if (isTotal) 20.sp else 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun ModernCheckoutBottomBar(total: Double, isPlacingOrder: Boolean, onPlaceOrder: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 12.dp,
        color = Color.White
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Button(
                onClick = onPlaceOrder,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MainColor),
                enabled = !isPlacingOrder
            ) {
                if (isPlacingOrder) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        Icons.Default.ShoppingCartCheckout,
                        contentDescription = null,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Place Order • NRP ${total.toInt()}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )
                }
            }
        }
    }
}
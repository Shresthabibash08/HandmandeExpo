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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.example.handmadeexpo.ui.theme.Gray
import com.example.handmadeexpo.ui.theme.MainColor
import com.example.handmadeexpo.ui.theme.TextBlack
import com.example.handmadeexpo.ui.theme.cream
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
        val isFromCart = intent.getBooleanExtra("isFromCart", false) // NEW: Flag to know if from cart

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
                isFromCart = isFromCart, // NEW: Pass the flag
                onBackClick = { finish() }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutUI(
    initialItems: List<OrderItem>,
    isFromCart: Boolean, // NEW: Flag to know if from cart
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val viewModel: CheckoutViewModel = viewModel()

    LaunchedEffect(Unit) { viewModel.fetchUserInfo() }

    val currentDate = remember {
        val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        sdf.format(Date())
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
    )

    datePickerDialog.datePicker.minDate = Calendar.getInstance().apply {
        add(Calendar.DAY_OF_MONTH, 1)
    }.timeInMillis

    if (showStockError) {
        AlertDialog(
            onDismissRequest = { showStockError = false },
            icon = {
                Icon(
                    Icons.Default.Warning,
                    contentDescription = null,
                    tint = Color(0xFFFF9800),
                    modifier = Modifier.size(48.dp)
                )
            },
            title = {
                Text(
                    "Stock Unavailable",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFF9800)
                )
            },
            text = {
                Text(
                    stockErrorMessage,
                    textAlign = TextAlign.Center
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showStockError = false
                        onBackClick()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MainColor)
                ) {
                    Text("OK")
                }
            }
        )
    }

    if (showAddressDialog) {
        AlertDialog(
            onDismissRequest = { showAddressDialog = false },
            title = { Text("Shipping Details") },
            text = {
                Column {
                    OutlinedTextField(
                        value = tempAddress,
                        onValueChange = { tempAddress = it },
                        label = { Text("Address") },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Enter your complete address") }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = tempPhone,
                        onValueChange = {
                            if (it.all { char -> char.isDigit() } && it.length <= 10) {
                                tempPhone = it
                            }
                        },
                        label = { Text("Phone Number") },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("10 digit mobile number") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = tempDeliveryDate,
                        onValueChange = { },
                        label = { Text("Preferred Delivery Date") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { datePickerDialog.show() },
                        readOnly = true,
                        enabled = false,
                        placeholder = { Text("Tap to select date") },
                        trailingIcon = {
                            IconButton(onClick = { datePickerDialog.show() }) {
                                Icon(
                                    imageVector = Icons.Default.CalendarToday,
                                    contentDescription = "Select Date",
                                    tint = MainColor
                                )
                            }
                        },
                        colors = TextFieldDefaults.colors(
                            disabledTextColor = TextBlack,
                            disabledContainerColor = Color.Transparent,
                            disabledLabelColor = Gray
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (tempAddress.isBlank()) {
                            Toast.makeText(context, "Please enter an address", Toast.LENGTH_SHORT).show()
                        } else if (tempPhone.length != 10) {
                            Toast.makeText(context, "Phone number must be exactly 10 digits", Toast.LENGTH_SHORT).show()
                        } else if (tempDeliveryDate.isBlank()) {
                            Toast.makeText(context, "Please select a delivery date", Toast.LENGTH_SHORT).show()
                        } else {
                            address = tempAddress
                            phoneNumber = tempPhone
                            deliveryDate = tempDeliveryDate
                            showAddressDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MainColor)
                ) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { showAddressDialog = false }) { Text("Cancel") }
            }
        )
    }

    Scaffold(
        containerColor = cream,
        topBar = {
            TopAppBar(
                title = { Text("Checkout (${orderItems.size})", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(painterResource(id = R.drawable.outline_arrow_back_ios_24), "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MainColor)
            )
        },
        bottomBar = {
            if (orderItems.isNotEmpty()) {
                Box(modifier = Modifier.background(Color.White).padding(16.dp)) {
                    Button(
                        onClick = {
                            if (address.isBlank()) {
                                Toast.makeText(context, "Please enter shipping address!", Toast.LENGTH_SHORT).show()
                                tempAddress = address; tempPhone = phoneNumber; tempDeliveryDate = deliveryDate
                                showAddressDialog = true
                                return@Button
                            }

                            if (phoneNumber.length != 10 || !phoneNumber.all { it.isDigit() }) {
                                Toast.makeText(context, "Phone must be valid 10 digits!", Toast.LENGTH_SHORT).show()
                                tempAddress = address; tempPhone = phoneNumber; tempDeliveryDate = deliveryDate
                                showAddressDialog = true
                                return@Button
                            }

                            if (deliveryDate.isBlank()) {
                                Toast.makeText(context, "Please select delivery date!", Toast.LENGTH_SHORT).show()
                                tempAddress = address; tempPhone = phoneNumber; tempDeliveryDate = deliveryDate
                                showAddressDialog = true
                                return@Button
                            }

                            isPlacingOrder = true
                            Toast.makeText(context, "Validating stock...", Toast.LENGTH_SHORT).show()

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
                                    Toast.makeText(context, msg, Toast.LENGTH_LONG).show()

                                    // NEW: Clear cart if order came from cart
                                    if (isFromCart) {
                                        val userId = FirebaseAuth.getInstance().currentUser?.uid
                                        if (userId != null) {
                                            // Clear the entire cart
                                            FirebaseDatabase.getInstance()
                                                .getReference("carts")
                                                .child(userId)
                                                .removeValue()
                                                .addOnSuccessListener {
                                                    Toast.makeText(
                                                        context,
                                                        "Cart cleared",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                        }
                                    }

                                    onBackClick()
                                } else {
                                    stockErrorMessage = msg
                                    showStockError = true
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MainColor),
                        enabled = !isPlacingOrder
                    ) {
                        if (isPlacingOrder) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Processing...", fontSize = 16.sp)
                        } else {
                            Text("Place Order • NRP ${total.toInt()}", fontSize = 18.sp, color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        if (orderItems.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Your checkout list is empty", color = Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                item {
                    Text("Shipping Details", fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.clickable {
                            tempAddress = address; tempPhone = phoneNumber; tempDeliveryDate = deliveryDate
                            showAddressDialog = true
                        }
                    ) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(painterResource(id = R.drawable.baseline_add_location_24), null, tint = MainColor)
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(if (customerName.isEmpty()) "Name not set" else customerName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                if (address.isBlank()) {
                                    Text("Tap to add shipping details...", color = Color.Red, fontSize = 14.sp)
                                } else {
                                    Text(address, color = Gray, fontSize = 14.sp)
                                    Text(phoneNumber, color = Gray, fontSize = 14.sp)
                                    if (deliveryDate.isNotBlank()) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.CalendarToday, contentDescription = null, modifier = Modifier.size(14.dp), tint = MainColor)
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Delivery: $deliveryDate", color = MainColor, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                                        }
                                    }
                                }
                            }
                            Icon(painterResource(id = R.drawable.baseline_edit_24), "Edit", tint = Gray)
                        }
                    }
                }

                item { Text("Order Items", fontSize = 18.sp, fontWeight = FontWeight.Bold) }
                items(orderItems) { item ->
                    OrderItemRow(item = item, onDeleteClick = { orderItems = orderItems.toMutableList().apply { remove(item) } })
                    Spacer(modifier = Modifier.height(10.dp))
                }

                item {
                    Text("Payment Method", fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
                    Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(12.dp)) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            PaymentOptionRow("Cash on Delivery", "COD", selectedPaymentMethod) { selectedPaymentMethod = it }
                            PaymentOptionRow("eSewa Wallet", "eSewa", selectedPaymentMethod) { selectedPaymentMethod = it }
                        }
                    }
                }

                item {
                    Text("Order Summary", fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
                    Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(12.dp)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            PriceRow("Order Date", currentDate)
                            if (deliveryDate.isNotBlank()) {
                                PriceRow("Delivery Date", deliveryDate)
                            }
                            PriceRow("Subtotal", "NRP ${subtotal.toInt()}")
                            PriceRow("Delivery Fee", "NRP ${deliveryFee.toInt()}")
                            Divider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFEEEEEE))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Total", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                Text("NRP ${total.toInt()}", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MainColor)
                            }
                        }
                    }
                }
                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
        }
    }
}

@Composable
fun OrderItemRow(item: OrderItem, onDeleteClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(12.dp)).padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(model = item.imageUrl, contentDescription = null, contentScale = ContentScale.Crop,
            modifier = Modifier.size(60.dp).clip(RoundedCornerShape(8.dp)).background(Color.LightGray),
            error = painterResource(R.drawable.img_1))
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(item.productName, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = TextBlack, maxLines = 1)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("NRP ${item.price.toInt()}", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MainColor)
                Text("  x ${item.quantity}", fontSize = 14.sp, color = Gray)
            }
        }
        IconButton(onClick = onDeleteClick) { Icon(imageVector = Icons.Default.Delete, contentDescription = "Remove", tint = Color.Red.copy(alpha = 0.6f)) }
    }
}

@Composable
fun PaymentOptionRow(label: String, value: String, selectedValue: String, onSelect: (String) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().clickable { onSelect(value) }.padding(vertical = 4.dp, horizontal = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        RadioButton(selected = (value == selectedValue), onClick = { onSelect(value) }, colors = RadioButtonDefaults.colors(selectedColor = MainColor))
        Text(text = label, fontSize = 15.sp, color = TextBlack, modifier = Modifier.padding(start = 8.dp))
    }
}

@Composable
fun PriceRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, fontSize = 15.sp, color = Gray)
        Text(value, fontSize = 15.sp, color = TextBlack, fontWeight = FontWeight.Medium)
    }
}
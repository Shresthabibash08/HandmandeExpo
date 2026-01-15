package com.example.handmadeexpo.view

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
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

        // The CartScreen passes OrderItems that already contain the Accepted Bargain Price
        val cartItems = intent.getSerializableExtra("cartItems") as? ArrayList<OrderItem>
        val isFromCart = intent.getBooleanExtra("isFromCart", false)

        val finalItems = if (cartItems != null && cartItems.isNotEmpty()) {
            cartItems.toList()
        } else {
            // Logic for "Buy Now" from Product details (not from cart)
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

    // Subtotal uses the price inside OrderItem (which we ensured is the Bargain price in CartScreen)
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
        calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)
    ).apply {
        datePicker.minDate = Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, 1) }.timeInMillis
    }

    if (showStockError) {
        AlertDialog(
            onDismissRequest = { showStockError = false },
            icon = { Icon(Icons.Default.Warning, null, tint = Color(0xFFFF9800), modifier = Modifier.size(48.dp)) },
            title = { Text("Stock Unavailable", fontWeight = FontWeight.Bold, color = Color(0xFFFF9800)) },
            text = { Text(stockErrorMessage, textAlign = TextAlign.Center) },
            confirmButton = {
                Button(onClick = { showStockError = false; onBackClick() }, colors = ButtonDefaults.buttonColors(containerColor = MainColor)) {
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
                    OutlinedTextField(value = tempAddress, onValueChange = { tempAddress = it }, label = { Text("Address") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = tempPhone,
                        onValueChange = { if (it.all { c -> c.isDigit() } && it.length <= 10) tempPhone = it },
                        label = { Text("Phone Number") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = tempDeliveryDate,
                        onValueChange = { },
                        label = { Text("Delivery Date") },
                        modifier = Modifier.fillMaxWidth().clickable { datePickerDialog.show() },
                        readOnly = true, enabled = false,
                        trailingIcon = { IconButton(onClick = { datePickerDialog.show() }) { Icon(Icons.Default.CalendarToday, null, tint = MainColor) } },
                        colors = TextFieldDefaults.colors(disabledTextColor = TextBlack, disabledContainerColor = Color.Transparent)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (tempAddress.isBlank() || tempPhone.length != 10 || tempDeliveryDate.isBlank()) {
                            Toast.makeText(context, "Fill all details correctly", Toast.LENGTH_SHORT).show()
                        } else {
                            address = tempAddress; phoneNumber = tempPhone; deliveryDate = tempDeliveryDate
                            showAddressDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(MainColor)
                ) { Text("Save") }
            }
        )
    }

    Scaffold(
        containerColor = cream,
        topBar = {
            TopAppBar(
                title = { Text("Checkout", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onBackClick) { Icon(painterResource(id = R.drawable.outline_arrow_back_ios_24), "Back", tint = Color.White) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MainColor)
            )
        },
        bottomBar = {
            if (orderItems.isNotEmpty()) {
                Box(modifier = Modifier.background(Color.White).padding(16.dp)) {
                    Button(
                        onClick = {
                            if (address.isBlank() || phoneNumber.length != 10) {
                                showAddressDialog = true
                                return@Button
                            }
                            isPlacingOrder = true
                            val newOrder = OrderModel(
                                customerName = customerName, address = address, phone = phoneNumber,
                                items = orderItems, totalPrice = total, paymentMethod = selectedPaymentMethod,
                                status = "Pending", orderDate = currentDate, deliveryDate = deliveryDate
                            )
                            viewModel.placeOrder(newOrder) { success, msg ->
                                isPlacingOrder = false
                                if (success) {
                                    val userId = FirebaseAuth.getInstance().currentUser?.uid
                                    if (userId != null) {
                                        val db = FirebaseDatabase.getInstance().reference
                                        // 1. Remove cart items if ordered from cart
                                        if (isFromCart) db.child("carts").child(userId).removeValue()

                                        // 2. IMPORTANT: Clean up Bargains for purchased items
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
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MainColor),
                        enabled = !isPlacingOrder
                    ) {
                        if (isPlacingOrder) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        else Text("Place Order • NRP ${total.toInt()}", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            // Shipping Details Section
            item {
                Text("Shipping Details", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Card(colors = CardDefaults.cardColors(containerColor = Color.White), modifier = Modifier.fillMaxWidth().clickable { showAddressDialog = true }) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(painterResource(id = R.drawable.baseline_add_location_24), null, tint = MainColor)
                        Spacer(Modifier.width(16.dp))
                        Column(Modifier.weight(1f)) {
                            Text(customerName.ifEmpty { "Customer" }, fontWeight = FontWeight.Bold)
                            Text(if (address.isBlank()) "Add Shipping Info" else "$address, $phoneNumber", color = Gray, fontSize = 14.sp)
                            if (deliveryDate.isNotEmpty()) {
                                Text("Delivery by: $deliveryDate", color = MainColor, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            item { Text("Your Items", fontSize = 18.sp, fontWeight = FontWeight.Bold) }

            items(orderItems) { item ->
                OrderItemRow(item, onDeleteClick = {
                    if (orderItems.size > 1) {
                        orderItems = orderItems.toMutableList().apply { remove(item) }
                    } else {
                        onBackClick() // Exit checkout if no items left
                    }
                })
            }

            // Payment and Summary sections remain the same
            item {
                Text("Payment Method", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Card(colors = CardDefaults.cardColors(containerColor = Color.White)) {
                    Column(Modifier.padding(8.dp)) {
                        PaymentOptionRow("Cash on Delivery", "COD", selectedPaymentMethod) { selectedPaymentMethod = it }
                        PaymentOptionRow("eSewa Wallet", "eSewa", selectedPaymentMethod) { selectedPaymentMethod = it }
                    }
                }
            }

            item {
                Text("Order Summary", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Card(colors = CardDefaults.cardColors(containerColor = Color.White)) {
                    Column(Modifier.padding(16.dp)) {
                        PriceRow("Subtotal", "NRP ${subtotal.toInt()}")
                        PriceRow("Delivery Fee", "NRP ${deliveryFee.toInt()}")
                        HorizontalDivider(Modifier.padding(vertical = 12.dp), thickness = 0.5.dp)
                        PriceRow("Total Amount", "NRP ${total.toInt()}", isTotal = true)
                    }
                }
            }
        }
    }
}

@Composable
fun OrderItemRow(item: OrderItem, onDeleteClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = item.imageUrl,
                contentDescription = null,
                modifier = Modifier.size(60.dp).clip(RoundedCornerShape(8.dp)).background(Color.LightGray),
                contentScale = ContentScale.Crop
            )
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(item.productName, fontWeight = FontWeight.SemiBold, maxLines = 1)
                // This will automatically show the bargain price because CartScreen passed it here
                Text("NRP ${item.price.toInt()} x ${item.quantity}", color = MainColor, fontWeight = FontWeight.Bold)
            }
            IconButton(onClick = onDeleteClick) { Icon(Icons.Default.Delete, null, tint = Color.Red.copy(0.6f)) }
        }
    }
}

// ... PaymentOptionRow and PriceRow remain the same as your provided code ...
@Composable
fun PriceRow(label: String, value: String, isTotal: Boolean = false) {
    Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, fontSize = if (isTotal) 18.sp else 15.sp, fontWeight = if (isTotal) FontWeight.Bold else FontWeight.Normal)
        Text(value, color = if (isTotal) MainColor else TextBlack, fontSize = if (isTotal) 18.sp else 15.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun PaymentOptionRow(label: String, value: String, selectedValue: String, onSelect: (String) -> Unit) {
    Row(Modifier.fillMaxWidth().clickable { onSelect(value) }.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
        RadioButton(selected = (value == selectedValue), onClick = { onSelect(value) }, colors = RadioButtonDefaults.colors(selectedColor = MainColor))
        Text(label, modifier = Modifier.padding(start = 8.dp))
    }
}
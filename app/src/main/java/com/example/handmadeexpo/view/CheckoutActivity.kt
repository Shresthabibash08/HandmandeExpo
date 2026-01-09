package com.example.handmadeexpo.view

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
import androidx.compose.foundation.text.KeyboardOptions // <--- ADD IMPORT
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
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
import androidx.compose.ui.text.input.KeyboardType // <--- ADD IMPORT
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CheckoutActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Get List or Single Item
        val cartItems = intent.getSerializableExtra("cartItems") as? ArrayList<OrderItem>

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
                onBackClick = { finish() }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutUI(
    initialItems: List<OrderItem>,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val viewModel: CheckoutViewModel = viewModel()

    // Fetch Info
    LaunchedEffect(Unit) { viewModel.fetchUserInfo() }

    // Date
    val currentDate = remember {
        val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        sdf.format(Date())
    }

    // State
    var orderItems by remember { mutableStateOf(initialItems) }
    var selectedPaymentMethod by remember { mutableStateOf("COD") }

    var customerName by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }

    var showAddressDialog by remember { mutableStateOf(false) }
    var tempAddress by remember { mutableStateOf("") }
    var tempPhone by remember { mutableStateOf("") }

    val fetchedName by viewModel.currentUserName
    LaunchedEffect(fetchedName) { if (fetchedName.isNotEmpty()) customerName = fetchedName }

    val subtotal = orderItems.sumOf { it.price * it.quantity }
    val deliveryFee = if (orderItems.isNotEmpty()) 200.0 else 0.0
    val total = subtotal + deliveryFee

    // --- DIALOG ---
    if (showAddressDialog) {
        AlertDialog(
            onDismissRequest = { showAddressDialog = false },
            title = { Text("Shipping Details") },
            text = {
                Column {
                    OutlinedTextField(
                        value = tempAddress, onValueChange = { tempAddress = it },
                        label = { Text("Address") }, modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = tempPhone,
                        onValueChange = {
                            // Only allow typing numbers
                            if (it.all { char -> char.isDigit() } && it.length <= 10) {
                                tempPhone = it
                            }
                        },
                        label = { Text("Phone ") },
                        modifier = Modifier.fillMaxWidth(),
                        // Force Number Keyboard
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        // --- VALIDATION 1: INSIDE DIALOG ---
                        if (tempAddress.isBlank()) {
                            Toast.makeText(context, "Please enter an address", Toast.LENGTH_SHORT).show()
                        } else if (tempPhone.length != 10) {
                            Toast.makeText(context, "Phone number must be exactly 10 digits", Toast.LENGTH_SHORT).show()
                        } else {
                            address = tempAddress
                            phoneNumber = tempPhone
                            showAddressDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MainColor)
                ) { Text("Save") }
            },
            dismissButton = { TextButton(onClick = { showAddressDialog = false }) { Text("Cancel") } }
        )
    }

    Scaffold(
        containerColor = cream,
        topBar = {
            TopAppBar(
                title = { Text("Checkout (${orderItems.size})", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) { Icon(painterResource(id = R.drawable.outline_arrow_back_ios_24), "Back", tint = Color.White) }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MainColor)
            )
        },
        bottomBar = {
            if (orderItems.isNotEmpty()) {
                Box(modifier = Modifier.background(Color.White).padding(16.dp)) {
                    Button(
                        onClick = {
                            // --- VALIDATION 2: BEFORE PLACING ORDER ---
                            if (address.isBlank()) {
                                Toast.makeText(context, "Please enter shipping address!", Toast.LENGTH_SHORT).show()
                                tempAddress = address; tempPhone = phoneNumber; showAddressDialog = true
                                return@Button
                            }

                            // Check for exactly 10 digits
                            if (phoneNumber.length != 10 || !phoneNumber.all { it.isDigit() }) {
                                Toast.makeText(context, "Phone must be valid 10 digits!", Toast.LENGTH_SHORT).show()
                                tempAddress = address
                                tempPhone = phoneNumber
                                showAddressDialog = true
                                return@Button
                            }

                            Toast.makeText(context, "Processing...", Toast.LENGTH_SHORT).show()

                            val newOrder = OrderModel(
                                customerName = customerName,
                                address = address,
                                phone = phoneNumber,
                                items = orderItems,
                                totalPrice = total,
                                paymentMethod = selectedPaymentMethod,
                                status = "Pending",
                                orderDate = currentDate
                            )

                            viewModel.placeOrder(newOrder) { success, msg ->
                                Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                                if (success) onBackClick()
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MainColor)
                    ) {
                        Text("Place Order • NRP ${total.toInt()}", fontSize = 18.sp, color = Color.White, fontWeight = FontWeight.Bold)
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
                // Address Card
                item {
                    Text("Shipping Address", fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.clickable { tempAddress = address; tempPhone = phoneNumber; showAddressDialog = true }
                    ) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(painterResource(id = R.drawable.baseline_add_location_24), null, tint = MainColor)
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(if (customerName.isEmpty()) "Name not set" else customerName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                if (address.isBlank()) Text("Tap to add address details...", color = Color.Red, fontSize = 14.sp)
                                else { Text(address, color = Gray, fontSize = 14.sp); Text(phoneNumber, color = Gray, fontSize = 14.sp) }
                            }
                            Icon(painterResource(id = R.drawable.baseline_edit_24), "Edit", tint = Gray)
                        }
                    }
                }

                // Items
                item { Text("Order Items", fontSize = 18.sp, fontWeight = FontWeight.Bold) }
                items(orderItems) { item ->
                    OrderItemRow(item = item, onDeleteClick = { orderItems = orderItems.toMutableList().apply { remove(item) } })
                    Spacer(modifier = Modifier.height(10.dp))
                }

                // Payment
                item {
                    Text("Payment Method", fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
                    Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(12.dp)) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            PaymentOptionRow("Cash on Delivery", "COD", selectedPaymentMethod) { selectedPaymentMethod = it }
                            PaymentOptionRow("eSewa Wallet", "eSewa", selectedPaymentMethod) { selectedPaymentMethod = it }
                        }
                    }
                }

                // Summary
                item {
                    Text("Order Summary", fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
                    Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(12.dp)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            PriceRow("Date", currentDate)
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

// Helpers
@Composable
fun OrderItemRow(item: OrderItem, onDeleteClick: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(12.dp)).padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
        AsyncImage(model = item.imageUrl, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.size(60.dp).clip(RoundedCornerShape(8.dp)).background(Color.LightGray), error = painterResource(R.drawable.img_1))
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
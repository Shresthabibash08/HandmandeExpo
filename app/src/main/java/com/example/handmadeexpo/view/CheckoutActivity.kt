package com.example.handmadeexpo.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.handmadeexpo.R
import com.example.handmadeexpo.ui.theme.Blue1
import com.example.handmadeexpo.ui.theme.Gray
import com.example.handmadeexpo.ui.theme.MainColor
import com.example.handmadeexpo.ui.theme.TextBlack
import com.example.handmadeexpo.ui.theme.cream
import com.example.handmadeexpo.ui.theme.White12 // Assuming you have this from previous snippets

class CheckoutActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CheckoutUI(onBackClick = { finish() })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutUI(onBackClick: () -> Unit) {

    // State for Payment Selection
    var selectedPaymentMethod by remember { mutableStateOf("COD") }

    Scaffold(
        containerColor = cream, // Using your Theme Color
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Checkout",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            painter = painterResource(id = R.drawable.outline_arrow_back_ios_24),
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MainColor) // Using MainColor
            )
        },
        bottomBar = {
            // Fixed Bottom Bar for "Place Order"
            Box(
                modifier = Modifier
                    .background(Color.White)
                    .padding(16.dp)
            ) {
                Button(
                    onClick = { /* No Logic yet */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    // Using MainColor for the primary action button
                    colors = ButtonDefaults.buttonColors(containerColor = MainColor)
                ) {
                    Text(
                        "Place Order • NRP 3700",
                        fontSize = 18.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            // --- 1. SHIPPING ADDRESS ---
            item {
                SectionHeader("Shipping Address")
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Location Icon Box
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(MainColor.copy(alpha = 0.1f), RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(

                                painter = painterResource(id = R.drawable.baseline_add_location_24),
                                contentDescription = null,
                                tint = MainColor
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        // Address Details
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Ram Bahadur", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextBlack)
                            Text("Suryabinayak-5, Bhaktapur", fontSize = 14.sp, color = Gray)
                            Text("+977 9841XXXXXX", fontSize = 14.sp, color = Gray)
                        }

                        // Edit Button
                        IconButton(onClick = { /* No Logic */ }) {
                            Icon(
                                // USING DRAWABLE RESOURCE
                                painter = painterResource(id = R.drawable.baseline_edit_24),
                                contentDescription = "Edit",
                                tint = Gray
                            )
                        }
                    }
                }
            }

            // --- 2. ORDER ITEMS ---
            item {
                SectionHeader("Order Items (2)")
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    CheckoutItemCard(
                        name = "Dhaka Topi",
                        variant = "Size: M | Red",
                        price = "NRP 500",
                        qty = "1",
                        imageRes = R.drawable.img_1
                    )
                    CheckoutItemCard(
                        name = "Hemp Backpack",
                        variant = "Natural Color",
                        price = "NRP 3000",
                        qty = "1",
                        imageRes = R.drawable.img_2
                    )
                }
            }

            // --- 3. PAYMENT METHOD ---
            item {
                SectionHeader("Payment Method")
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        PaymentOptionRow("Cash on Delivery (COD)", "COD", selectedPaymentMethod) { selectedPaymentMethod = it }
                        PaymentOptionRow("eSewa Mobile Wallet", "eSewa", selectedPaymentMethod) { selectedPaymentMethod = it }
                        PaymentOptionRow("Khalti Digital Wallet", "Khalti", selectedPaymentMethod) { selectedPaymentMethod = it }
                    }
                }
            }

            // --- 4. ORDER SUMMARY ---
            item {
                SectionHeader("Order Summary")
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        PriceRow("Subtotal", "NRP 3500")
                        PriceRow("Delivery Fee", "NRP 200")
                        Divider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFEEEEEE))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Total", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextBlack)
                            Text("NRP 3700", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MainColor)
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

// --- Helper Composables ---

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = TextBlack,
        modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
    )
}

@Composable
fun CheckoutItemCard(name: String, variant: String, price: String, qty: String, imageRes: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(60.dp)
                .background(Color.LightGray, RoundedCornerShape(8.dp))
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(name, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = TextBlack)
            Text(variant, fontSize = 12.sp, color = Gray)
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Using MainColor for Price
                Text(price, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MainColor)
                Text("  x $qty", fontSize = 14.sp, color = Gray)
            }
        }
    }
}

@Composable
fun PaymentOptionRow(label: String, value: String, selectedValue: String, onSelect: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect(value) }
            .padding(vertical = 4.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = (value == selectedValue),
            onClick = { onSelect(value) },
            // Using MainColor for the Radio selection
            colors = RadioButtonDefaults.colors(selectedColor = MainColor)
        )
        Text(text = label, fontSize = 15.sp, color = TextBlack, modifier = Modifier.padding(start = 8.dp))
    }
}

@Composable
fun PriceRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 15.sp, color = Gray)
        Text(value, fontSize = 15.sp, color = TextBlack, fontWeight = FontWeight.Medium)
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun CheckoutPreview() {
    CheckoutUI(onBackClick = {})
}
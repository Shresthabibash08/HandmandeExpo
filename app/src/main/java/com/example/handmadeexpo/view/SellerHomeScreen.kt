package com.example.handmadeexpo.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.handmadeexpo.R

@Composable
fun SellerHomeScreen() {
    // 1. Categories Data
    val categories = listOf(
        "Beauty" to R.drawable.img_1,
        "Home" to R.drawable.img_2,
        "Fashion" to R.drawable.img_3,
        "Appliances" to R.drawable.img_4,
        "Party" to R.drawable.img_5,
        "Toys" to R.drawable.img_6
    )

    // 2. Seller Products Data (Declared directly using Maps, no class needed)
    val sellerProducts = listOf(
        mapOf("name" to "Handmade Vase", "price" to "NRP 1200", "stock" to 50, "sold" to 12, "img" to R.drawable.img_1),
        mapOf("name" to "Woolen Scarf", "price" to "NRP 800", "stock" to 100, "sold" to 45, "img" to R.drawable.img_6),
        mapOf("name" to "Wooden Toy", "price" to "NRP 500", "stock" to 30, "sold" to 5, "img" to R.drawable.img_10),
        mapOf("name" to "Ceramic Bowl", "price" to "NRP 1500", "stock" to 20, "sold" to 18, "img" to R.drawable.img_12),
        mapOf("name" to "Painting", "price" to "NRP 5000", "stock" to 5, "sold" to 1, "img" to R.drawable.img_17)
    )

    // Used Box to overlay the Floating Action Button on top of the list
    Box(modifier = Modifier.fillMaxSize()) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
                .padding(16.dp)
        ) {
            // --- Categories Section ---
            Text(
                text = "Categories",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(12.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(categories) { (name, imageRes) ->
                    SellerCategoryItem(name, imageRes)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- Inventory Section ---
            Text(
                text = "Your Inventory",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(12.dp))

            // --- Scrollable Product List ---
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(sellerProducts) { product ->
                    // Extracting data from the Map
                    SellerProductItem(
                        name = product["name"] as String,
                        price = product["price"] as String,
                        stock = product["stock"] as Int,
                        sold = product["sold"] as Int,
                        imageRes = product["img"] as Int
                    )
                }
            }
        }

        // --- Floating Action Button ---
        FloatingActionButton(
            onClick = { /* No navigation for now */ },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp), // Padding from the edges
            containerColor = Color(0xFFE65100), // Matching the Orange Theme
            contentColor = Color.White
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Product")
        }
    }
}

// --- Helper Composables ---

@Composable
fun SellerCategoryItem(name: String, imageRes: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .background(Color.White, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = name,
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Fit
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(name, fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun SellerProductItem(name: String, price: String, stock: Int, sold: Int, imageRes: Int) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Product Image
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(8.dp))
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Product Details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = price,
                    color = Color(0xFFE65100), // Orange
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Stats Column
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "Sold: $sold",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                Text(
                    text = "Stock: $stock",
                    fontSize = 12.sp,
                    color = if (stock < 10) Color.Red else Color(0xFF4CAF50)
                )
            }
        }
    }
}
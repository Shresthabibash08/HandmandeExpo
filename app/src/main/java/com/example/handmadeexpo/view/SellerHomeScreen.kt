package com.example.handmadeexpo.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
    // Dummy Data for Seller Products
    val sellerProducts = listOf(
        SellerProduct("Handmade Vase", "NRP 1200", 50, 12, R.drawable.img_1),
        SellerProduct("Woolen Scarf", "NRP 800", 100, 45, R.drawable.img_6),
        SellerProduct("Wooden Toy", "NRP 500", 30, 5, R.drawable.img_10),
        SellerProduct("Ceramic Bowl", "NRP 1500", 20, 18, R.drawable.img_12),
        SellerProduct("Painting", "NRP 5000", 5, 1, R.drawable.img_17)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5)) // Light Gray background
            .padding(16.dp)
    ) {
        // --- Top Stats Row ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            DashboardStatCard(
                title = "Products",
                value = "205",
                color = Color(0xFF2196F3), // Blue
                modifier = Modifier.weight(1f)
            )
            DashboardStatCard(
                title = "Sold",
                value = "81",
                color = Color(0xFF4CAF50), // Green
                modifier = Modifier.weight(1f)
            )
            DashboardStatCard(
                title = "Earning",
                value = "12k",
                color = Color(0xFFFF9800), // Orange
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

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
                SellerProductItem(product)
            }
        }
    }
}

// --- Helper Components & Data Class ---

data class SellerProduct(
    val name: String,
    val price: String,
    val totalStock: Int,
    val soldCount: Int,
    val imageRes: Int
)

@Composable
fun DashboardStatCard(title: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.height(100.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                fontSize = 12.sp,
                color = Color.Gray,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun SellerProductItem(product: SellerProduct) {
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
                painter = painterResource(id = product.imageRes),
                contentDescription = product.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(8.dp))
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Product Details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = product.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = product.price,
                    color = Color(0xFFE65100), // Orange
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Stats Column
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "Sold: ${product.soldCount}",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                Text(
                    text = "Stock: ${product.totalStock}",
                    fontSize = 12.sp,
                    color = if (product.totalStock < 10) Color.Red else Color(0xFF4CAF50) // Red if low stock
                )
            }
        }
    }
}
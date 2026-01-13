package com.example.handmadeexpo.view

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.handmadeexpo.model.ProductModel
import com.example.handmadeexpo.repo.ProductRepoImpl
import com.example.handmadeexpo.ui.theme.Orange
import com.example.handmadeexpo.viewmodel.ProductViewModel

@Composable
fun SellerHomeScreen(
    sellerId: String
) {
    val viewModel: ProductViewModel = remember {
        ProductViewModel(ProductRepoImpl())
    }

    LaunchedEffect(sellerId) {
        Log.d("SELLER_ID_DEBUG", sellerId)
        viewModel.getProductsBySeller(sellerId)
    }

    val sellerProducts by viewModel.sellerProducts.observeAsState(emptyList())

    // Calculate overall statistics
    val totalProducts = sellerProducts.size
    val totalSold = sellerProducts.sumOf { it.sold }
    val totalRatings = sellerProducts.sumOf { it.ratingCount }
    val overallAverageRating = if (totalRatings > 0) {
        sellerProducts.sumOf { it.totalRating }.toFloat() / totalRatings
    } else {
        0f
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(16.dp)
    ) {

        item {
            Text(
                text = "Your Inventory",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        // NEW: Overall Statistics Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Overall Performance",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Orange
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        // Total Products
                        StatItem(
                            label = "Products",
                            value = totalProducts.toString(),
                            icon = "📦"
                        )

                        // Total Sold
                        StatItem(
                            label = "Total Sold",
                            value = totalSold.toString(),
                            icon = "🛍️"
                        )

                        // Average Rating
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "⭐",
                                fontSize = 24.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                if (totalRatings > 0)
                                    String.format("%.1f", overallAverageRating)
                                else
                                    "N/A",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                            Text(
                                "Avg Rating",
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                            if (totalRatings > 0) {
                                Text(
                                    "($totalRatings reviews)",
                                    fontSize = 10.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                }
            }
        }

        items(sellerProducts) { product ->
            SellerProductCard(product)
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun StatItem(label: String, value: String, icon: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            icon,
            fontSize = 24.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            value,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )
        Text(
            label,
            fontSize = 11.sp,
            color = Color.Gray
        )
    }
}

@Composable
fun SellerProductCard(product: ProductModel) {
    // Calculate average rating
    val averageRating = if (product.ratingCount > 0) {
        product.totalRating.toFloat() / product.ratingCount
    } else {
        0f
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            AsyncImage(
                model = product.image,
                contentDescription = product.name,
                modifier = Modifier
                    .size(70.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                // Product Name
                Text(
                    product.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    maxLines = 1
                )

                // Price
                Text(
                    "NRP ${product.price.toInt()}",
                    color = Orange,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Rating Display with Stars
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    // Show 5 stars
                    repeat(5) { index ->
                        Icon(
                            imageVector = if (index < averageRating.toInt())
                                Icons.Filled.Star
                            else
                                Icons.Outlined.Star,
                            contentDescription = null,
                            tint = if (index < averageRating.toInt())
                                Color(0xFFFFB300)
                            else
                                Color.LightGray,
                            modifier = Modifier.size(14.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = if (product.ratingCount > 0) {
                            String.format("%.1f (%d)", averageRating, product.ratingCount)
                        } else {
                            "No ratings"
                        },
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                // Sold Count
                Text(
                    "Sold: ${product.sold}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Stock Count with color and status
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        when {
                            product.stock == 0 -> "⚠️"
                            product.stock < 10 -> "⚡"
                            else -> "✅"
                        },
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        "${product.stock}",
                        fontSize = 12.sp,
                        color = when {
                            product.stock == 0 -> Color.Red
                            product.stock < 10 -> Color(0xFFFF9800)
                            else -> Color(0xFF4CAF50)
                        },
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
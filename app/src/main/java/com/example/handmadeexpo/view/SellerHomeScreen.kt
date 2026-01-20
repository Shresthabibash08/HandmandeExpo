package com.example.handmadeexpo.view

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
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

    // Separate products by verification status
    val verifiedProducts = sellerProducts.filter { it.verificationStatus == "Verified" }
    val pendingProducts = sellerProducts.filter { it.verificationStatus == "Pending" }
    val rejectedProducts = sellerProducts.filter { it.verificationStatus == "Rejected" }
    val nonVerifiedProducts = pendingProducts + rejectedProducts

    // Tab state
    var selectedTab by remember { mutableIntStateOf(0) }

    Column(modifier = Modifier.fillMaxSize()) {
        // Tab Row
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color(0xFFF5F5F5)
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Verified")
                        if (verifiedProducts.isNotEmpty()) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Badge { Text(verifiedProducts.size.toString()) }
                        }
                    }
                }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Pending/Rejected")
                        if (nonVerifiedProducts.isNotEmpty()) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Badge(containerColor = Color(0xFFFF9800)) {
                                Text(nonVerifiedProducts.size.toString())
                            }
                        }
                    }
                }
            )
        }

        // Content
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
                .padding(16.dp)
        ) {
            when (selectedTab) {
                0 -> {
                    // Verified Products Tab
                    if (verifiedProducts.isEmpty()) {
                        item {
                            EmptyState(
                                icon = Icons.Default.CheckCircle,
                                message = "No verified products yet",
                                subMessage = "Products will appear here once admin approves them"
                            )
                        }
                    } else {
                        items(verifiedProducts) { product ->
                            SellerProductCard(product)
                        }
                    }
                }
                1 -> {
                    // Pending/Rejected Products Tab
                    if (nonVerifiedProducts.isEmpty()) {
                        item {
                            EmptyState(
                                icon = Icons.Default.HourglassEmpty,
                                message = "No pending or rejected products",
                                subMessage = "All your products are verified!"
                            )
                        }
                    } else {
                        // Pending Products Section
                        if (pendingProducts.isNotEmpty()) {
                            item {
                                Text(
                                    "Pending Verification (${pendingProducts.size})",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                            }
                            items(pendingProducts) { product ->
                                SellerProductCard(product)
                            }
                        }

                        // Rejected Products Section
                        if (rejectedProducts.isNotEmpty()) {
                            item {
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    "Rejected (${rejectedProducts.size})",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Red,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                            }
                            items(rejectedProducts) { product ->
                                SellerProductCard(product)
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
fun EmptyState(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    message: String,
    subMessage: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 60.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = Color.Gray.copy(alpha = 0.5f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            message,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Gray
        )
        Text(
            subMessage,
            fontSize = 14.sp,
            color = Color.Gray.copy(alpha = 0.7f)
        )
    }
}

@Composable
fun SellerProductCard(product: ProductModel) {
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
        Column {
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

                    // Rating
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
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

                    // Stock Count
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

            // Verification Status Banner
            if (product.verificationStatus != "Verified") {
                HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            when (product.verificationStatus) {
                                "Pending" -> Color(0xFFFF9800).copy(alpha = 0.1f)
                                else -> Color(0xFFF44336).copy(alpha = 0.1f)
                            }
                        )
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = when (product.verificationStatus) {
                            "Pending" -> Icons.Default.HourglassEmpty
                            else -> Icons.Default.Cancel
                        },
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = when (product.verificationStatus) {
                            "Pending" -> Color(0xFFFF9800)
                            else -> Color(0xFFF44336)
                        }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            when (product.verificationStatus) {
                                "Pending" -> "Pending Admin Verification"
                                else -> "Rejected by Admin"
                            },
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = when (product.verificationStatus) {
                                "Pending" -> Color(0xFFFF9800)
                                else -> Color(0xFFF44336)
                            }
                        )
                        if (product.verificationStatus == "Rejected" && product.rejectionReason.isNotEmpty()) {
                            Text(
                                "Reason: ${product.rejectionReason}",
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
        }
    }
}
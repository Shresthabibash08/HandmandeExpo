package com.example.handmadeexpo.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDescription(
    product: ProductModel,  // Change from separate parameters to ProductModel
    onBackClick: () -> Unit,
    name: String,
    price: String,
    imageRes: Int
) {
    val OrangeBrand = Color(0xFFE65100)
    val CreamBackground = Color(0xFFFFF8E1)
    val TextGray = Color(0xFF757575)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Product Details", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CreamBackground)
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = Color.White,
                tonalElevation = 8.dp,
                modifier = Modifier.height(80.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = { /* Add to cart logic */ },
                        modifier = Modifier.weight(1f).height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, OrangeBrand)
                    ) {
                        Text("Add to Cart", color = OrangeBrand, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { /* Buy now logic */ },
                        modifier = Modifier.weight(1f).height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = OrangeBrand)
                    ) {
                        Text("Buy Now", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
                .verticalScroll(rememberScrollState())
        ) {
            // Product Image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(380.dp)
                    .background(CreamBackground),
                contentAlignment = Alignment.Center
            ) {
                if (product.image.isNotEmpty()) {
                    AsyncImage(
                        model = product.image,
                        contentDescription = product.name,
                        modifier = Modifier
                            .fillMaxSize(0.85f)
                            .clip(RoundedCornerShape(16.dp)),
                        contentScale = ContentScale.Fit
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize(0.85f)
                            .background(Color.LightGray, RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No Image", color = Color.White)
                    }
                }
            }

            // Product Info Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = product.name,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.weight(1f),
                        lineHeight = 32.sp
                    )
                    Text(
                        text = "NRP ${product.price}",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = OrangeBrand
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Rating Bar
                Row(verticalAlignment = Alignment.CenterVertically) {
                    repeat(5) { index ->
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = if (index < 4) Color(0xFFFFB300) else Color.LightGray,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "4.8 (250+ Reviews)",
                        color = TextGray,
                        fontSize = 14.sp
                    )
                }

                Divider(modifier = Modifier.padding(vertical = 20.dp), thickness = 0.5.dp)

                // Description Section
                Text(
                    text = "Description",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = product.description,
                    fontSize = 16.sp,
                    color = TextGray,
                    lineHeight = 24.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Stock Information
                Text(
                    text = "Stock Information",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = if (product.stock > 0)
                        "Available: ${product.stock} items in stock"
                    else
                        "Currently out of stock",
                    fontSize = 16.sp,
                    color = if (product.stock > 0) Color.Green else Color.Red,
                    fontWeight = FontWeight.Medium
                )

                // Extra spacer to ensure content doesn't get hidden behind the BottomAppBar
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}
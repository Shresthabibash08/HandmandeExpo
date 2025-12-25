package com.example.handmadeexpo.view

import androidx.compose.foundation.Image
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDescription(
    name: String,
    price: String,
    imageRes: Int,
    onBackClick: () -> Unit
) {
    // Define the colors within the file to ensure it works
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
        // Floating bottom bar for the buttons so they are always accessible
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
                    // ADD TO CART BUTTON
                    OutlinedButton(
                        onClick = { /* Logic */ },
                        modifier = Modifier.weight(1f).height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, OrangeBrand)
                    ) {
                        Text("Add to Cart", color = OrangeBrand, fontWeight = FontWeight.Bold)
                    }

                    // BUY NOW BUTTON
                    Button(
                        onClick = { /* Logic */ },
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
        // This Column is now scrollable
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
                .verticalScroll(rememberScrollState()) // <--- ENABLE SCROLLING HERE
        ) {
            // 1. ENLARGED PRODUCT IMAGE
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(380.dp) // Large height
                    .background(CreamBackground),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = name,
                    modifier = Modifier
                        .fillMaxSize(0.85f) // Makes it look larger
                        .clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Fit
                )
            }

            // 2. PRODUCT INFO SECTION
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
                        text = name,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.weight(1f),
                        lineHeight = 32.sp
                    )
                    Text(
                        text = price,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = OrangeBrand
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 3. RATING BAR
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

                // 4. DESCRIPTION SECTION
                Text(
                    text = "Description",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "This premium handmade product is part of our exclusive Expo collection. " +
                            "It is designed with high-quality materials to ensure durability and style. " +
                            "Whether you are buying this for yourself or as a gift, it represents the " +
                            "finest craftsmanship available.\n\n" +
                            "Key Features:\n" +
                            "• Handmade with precision\n" +
                            "• Eco-friendly materials\n" +
                            "• Unique design not found in stores\n" +
                            "• Limited edition collection.",
                    fontSize = 16.sp,
                    color = TextGray,
                    lineHeight = 24.sp
                )

                // Extra spacer to ensure content doesn't get hidden behind the BottomAppBar
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}
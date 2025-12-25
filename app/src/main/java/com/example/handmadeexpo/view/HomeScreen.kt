package com.example.handmadeexpo.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.handmadeexpo.R

// Custom Colors based on screenshot
val CreamBackground = Color(0xFFFFF8E1)
val OrangeBrand = Color(0xFFE65100)
val TextGray = Color(0xFF757575)

@Composable
fun HomeScreen() {
    // ✅ Updated Categories List to include Images
    // You can rename R.drawable.img_1, img_2, etc. to your actual file names later.
    val categories = listOf(
        "All" to R.drawable.img_2,
        "Painting" to R.drawable.img_1,
        "Bag" to R.drawable.img_3,
        "Craft" to R.drawable.img_4,
        "Insane Sticks" to R.drawable.img_5,
        "Small Sclupture" to R.drawable.img_6
    )

    val products = listOf(
        Triple("Sonic Headphones", "NRP 800", R.drawable.img_1),
        Triple("Mini Clock", "NRP 500", R.drawable.img_6),
        Triple("Wireless Earpods", "NRP 1200", R.drawable.img_12),
        Triple("Smart Watch", "NRP 2500", R.drawable.img_10)
    )

    var searchQuery by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // 1. Top Section with Cream Background
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = CreamBackground,
                        shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
                    )
                    .padding(bottom = 24.dp)
            ) {
                SearchBarInput(query = searchQuery, onQueryChange = { searchQuery = it })

                PromoBanner()

                Spacer(modifier = Modifier.height(16.dp))
                // Pass the updated list to the composable
                CategoryList(categories)
            }
        }

        // 2. Sale Section
        item {
            SectionHeader(title = " Sale", subtitle = "20 items Left", showArrow = true)
            ProductRow(products)
        }

        // 3. Recommended Section
        item {
            Spacer(modifier = Modifier.height(16.dp))
            SectionHeader(title = "Recommended for you", subtitle = "Buy them before it's too late!", showArrow = true)
            ProductRow(products.reversed())
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

// --- Helper Composables ---

@Composable
fun SearchBarInput(query: String, onQueryChange: (String) -> Unit) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text("Search here", color = TextGray) },
        leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = null, tint = Color.Black)
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, start = 16.dp, end = 16.dp)
            .height(56.dp)
            .clip(RoundedCornerShape(12.dp)),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        singleLine = true
    )
}



@Composable
fun PromoBanner() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(160.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(Color(0xFFFFD54F), Color(0xFFFFB300))
                    )
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Time for Special Deal", fontSize = 12.sp, color = Color.Black)
                    Text("70% Off", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    Text("On all electronics", fontSize = 12.sp, color = Color.Black)
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = {},
                        colors = ButtonDefaults.buttonColors(containerColor = OrangeBrand),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Text("Shop Now", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

// ✅ Updated Category List to use Images
@Composable
fun CategoryList(categories: List<Pair<String, Int>>) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        items(categories) { (name, imageRes) ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .background(Color.White, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    // Using Image instead of Icon to show full color pictures
                    Image(
                        painter = painterResource(id = imageRes),
                        contentDescription = name,
                        modifier = Modifier
                            .size(32.dp) // Adjusted size slightly for better fit
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Fit
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(name, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Composable
fun SectionHeader(title: String, subtitle: String? = null, showArrow: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            if (subtitle != null) {
                Text(subtitle, fontSize = 12.sp, color = TextGray)
            }
        }
        if (showArrow) {
            Text("See all", fontSize = 12.sp, color = OrangeBrand)
            Icon(Icons.Default.KeyboardArrowRight, contentDescription = null, tint = OrangeBrand, modifier = Modifier.size(16.dp))
        }
    }
}

@Composable
fun ProductRow(products: List<Triple<String, String, Int>>) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(products) { product ->
            ProductCard(
                name = product.first,
                price = product.second,
                imageRes = product.third
            )
        }
    }
}

@Composable
fun ProductCard(name: String, price: String, imageRes: Int) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFAFAFA)),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.width(140.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(Color.White, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(imageRes),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize().padding(8.dp),
                    contentScale = ContentScale.Fit
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(name, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, maxLines = 1)
            Row(verticalAlignment = Alignment.Bottom) {
                Text(price, fontSize = 14.sp, color = OrangeBrand, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(4.dp))
                Text("NRP 1300", fontSize = 10.sp, color = TextGray, textDecoration = TextDecoration.LineThrough)
            }
        }
    }
}
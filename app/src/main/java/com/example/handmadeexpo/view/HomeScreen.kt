package com.example.handmadeexpo.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.handmadeexpo.R

// Colors
val CreamBackground = Color(0xFFFFF8E1)
val OrangeBrand = Color(0xFFE65100)
val TextGray = Color(0xFF757575)

@Composable
fun HomeScreen() {
    // --- NAVIGATION STATE ---
    // This tracks which product is clicked. If null, show Home. If not null, show Details.
    var selectedProduct by remember { mutableStateOf<Triple<String, String, Int>?>(null) }

    if (selectedProduct == null) {
        // SHOW MAIN HOME UI
        MainHomeContent(onProductClick = { product ->
            selectedProduct = product
        })
    } else {
        // SHOW DETAIL UI (Directly directed here on click)
        ProductDescription(
            name = selectedProduct!!.first,
            price = selectedProduct!!.second,
            imageRes = selectedProduct!!.third,
            onBackClick = { selectedProduct = null } // Returns to Home
        )
    }
}

@Composable
fun MainHomeContent(onProductClick: (Triple<String, String, Int>) -> Unit) {
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

    LazyColumn(modifier = Modifier.fillMaxSize().background(Color.White)) {
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = CreamBackground, shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
                    .padding(bottom = 24.dp)
            ) {
                SearchBarInput(query = searchQuery, onQueryChange = { searchQuery = it })
                PromoBanner()
                Spacer(modifier = Modifier.height(16.dp))
                CategoryList(categories)
            }
        }

        item {
            SectionHeader(title = " Sale", subtitle = "20 items Left", showArrow = true)
            ProductRow(products, onProductClick)
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
            SectionHeader(title = "Recommended for you", subtitle = "Buy them before it's too late!", showArrow = true)
            ProductRow(products.reversed(), onProductClick)
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

// --- Helper UI Components ---

@Composable
fun ProductRow(products: List<Triple<String, String, Int>>, onProductClick: (Triple<String, String, Int>) -> Unit) {
    LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        items(products) { product ->
            ProductCard(
                name = product.first,
                price = product.second,
                imageRes = product.third,
                onClick = { onProductClick(product) }
            )
        }
    }
}

@Composable
fun ProductCard(name: String, price: String, imageRes: Int, onClick: () -> Unit) {
    Card(
        modifier = Modifier.width(140.dp).clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFAFAFA))
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Image(painter = painterResource(imageRes), contentDescription = null, modifier = Modifier.fillMaxWidth().height(100.dp), contentScale = ContentScale.Fit)
            Text(name, fontSize = 14.sp, fontWeight = FontWeight.Bold, maxLines = 1)
            Text(price, fontSize = 14.sp, color = OrangeBrand, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun SearchBarInput(query: String, onQueryChange: (String) -> Unit) {
    TextField(
        value = query, onValueChange = onQueryChange,
        placeholder = { Text("Search here", color = TextGray) },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        modifier = Modifier.fillMaxWidth().padding(16.dp).height(56.dp).clip(RoundedCornerShape(12.dp)),
        colors = TextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = Color.White, focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent)
    )
}

@Composable
fun PromoBanner() {
    Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).height(140.dp), shape = RoundedCornerShape(16.dp)) {
        Box(modifier = Modifier.fillMaxSize().background(Brush.horizontalGradient(colors = listOf(Color(0xFFFFD54F), Color(0xFFFFB300))))) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("70% Off", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Button(onClick = {}, colors = ButtonDefaults.buttonColors(containerColor = OrangeBrand)) { Text("Shop Now") }
            }
        }
    }
}

@Composable
fun CategoryList(categories: List<Pair<String, Int>>) {
    LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(20.dp)) {
        items(categories) { (name, imageRes) ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(modifier = Modifier.size(50.dp).background(Color.White, RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) {
                    Image(painter = painterResource(id = imageRes), contentDescription = name, modifier = Modifier.size(32.dp))
                }
                Text(name, fontSize = 11.sp)
            }
        }
    }
}

@Composable
fun SectionHeader(title: String, subtitle: String?, showArrow: Boolean) {
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            subtitle?.let { Text(it, fontSize = 12.sp, color = TextGray) }
        }
        if (showArrow) Icon(Icons.Default.KeyboardArrowRight, contentDescription = null, tint = OrangeBrand)
    }
}
package com.example.handmadeexpo.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.handmadeexpo.R

// --- COLORS ---
val CreamBackground = Color(0xFFFFF8E1)
val OrangeBrand = Color(0xFFE65100)
val TextGray = Color(0xFF757575)
val PriceTextViolet = Color(0xFF311B92)

// Gradient Colors for Slider
val GradientStart = Color(0xFF4CAF50) // Green
val GradientMiddle = Color(0xFFFFC107) // Amber
val GradientEnd = Color(0xFFD32F2F)   // Red

@Composable
fun HomeScreen() {
    // --- NAVIGATION STATE ---
    var selectedProduct by remember { mutableStateOf<Triple<String, String, Int>?>(null) }

    if (selectedProduct == null) {
        // SHOW MAIN HOME UI
        MainHomeContent(onProductClick = { product ->
            selectedProduct = product
        })
    } else {
        // SHOW DETAIL UI
        ProductDescription(
            name = selectedProduct!!.first,
            price = selectedProduct!!.second,
            imageRes = selectedProduct!!.third,
            onBackClick = { selectedProduct = null }
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

                // --- ADDED GRADIENT PRICE SLIDER HERE ---
                Spacer(modifier = Modifier.height(16.dp))
                GradientPriceSliderSection()
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

// --- NEW COMPONENT: Gradient Slider ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GradientPriceSliderSection() {
    // State for the slider value (0 to 100 range)
    var sliderValue by remember { mutableStateOf(23f) }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .shadow(elevation = 1.dp, shape = RoundedCornerShape(12.dp))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Max Price: ",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = PriceTextViolet
                )
                // Display calculated price (e.g. 23 * 100 = 2300)
                Text(
                    text = "NRP ${(sliderValue * 100).toInt()}",
                    fontSize = 16.sp,
                    color = PriceTextViolet
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Custom Slider Layout
            Box(
                contentAlignment = Alignment.CenterStart,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
            ) {
                // A. The Visible Gradient Track
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(GradientStart, GradientMiddle, GradientEnd)
                            )
                        )
                )

                // B. The Logic (Invisible Slider Overlay)
                Slider(
                    value = sliderValue,
                    onValueChange = { sliderValue = it },
                    valueRange = 0f..100f,
                    colors = SliderDefaults.colors(
                        thumbColor = Color.Transparent,
                        activeTrackColor = Color.Transparent,
                        inactiveTrackColor = Color.Transparent
                    ),
                    thumb = {
                        // C. The Visible Custom Thumb (Circle with Number)
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(32.dp)
                                .background(Color.White, CircleShape)
                                .border(width = 3.dp, color = GradientStart, shape = CircleShape)
                                .shadow(2.dp, CircleShape)
                        ) {
                            Text(
                                text = sliderValue.toInt().toString(),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = PriceTextViolet
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
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

// --- PRODUCT DETAIL SCREEN (Must be present for Navigation to work) ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardProductDescription(name: String, price: String, imageRes: Int, onBackClick: () -> Unit) {
    val scrollState = rememberScrollState()

    Scaffold(
        bottomBar = {
            BottomAppBar(
                containerColor = Color.White,
                tonalElevation = 8.dp,
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = price,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = OrangeBrand
                    )
                    Button(
                        onClick = { },
                        colors = ButtonDefaults.buttonColors(containerColor = OrangeBrand),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.height(50.dp)
                    ) {
                        Text("Add to Cart", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(CreamBackground)
                .padding(paddingValues)
                .verticalScroll(scrollState)
        ) {
            // Header (Back Button)
            Box(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .background(Color.White, RoundedCornerShape(12.dp))
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            }

            // Main Product Image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Details Container
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(text = name, fontSize = 26.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Experience high-quality items handcrafted with love.",
                        fontSize = 16.sp,
                        color = TextGray
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    // Item Counter
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ItemCounter()
                    }

                    Spacer(modifier = Modifier.height(100.dp))
                }
            }
        }
    }
}

@Composable
fun ItemCounter() {
    var count by remember { mutableStateOf(1) }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.background(Color(0xFFF5F5F5), RoundedCornerShape(12.dp))
    ) {
        IconButton(onClick = { if (count > 1) count-- }) { Text("-", fontSize = 20.sp, fontWeight = FontWeight.Bold) }
        Text(text = count.toString(), fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 12.dp))
        IconButton(onClick = { count++ }) { Text("+", fontSize = 20.sp, fontWeight = FontWeight.Bold) }
    }
}
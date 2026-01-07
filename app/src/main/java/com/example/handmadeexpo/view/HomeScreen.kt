package com.example.handmadeexpo.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.handmadeexpo.R
import com.example.handmadeexpo.model.ProductModel
import com.example.handmadeexpo.repo.ProductRepoImpl
import com.example.handmadeexpo.viewmodel.ProductViewModel
import com.example.handmadeexpo.viewmodel.ProductViewModelFactory

// --- COLORS ---
val CreamBackground = Color(0xFFFFF8E1)
val OrangeBrand = Color(0xFFE65100)
val TextGray = Color(0xFF757575)
val PriceTextViolet = Color(0xFF311B92)

@Composable
fun HomeScreen() {
    val viewModel: ProductViewModel = viewModel(
        factory = ProductViewModelFactory(ProductRepoImpl())
    )

    val products by viewModel.filteredProducts.observeAsState(initial = emptyList())
    val sliderValue by viewModel.sliderValue.observeAsState(100f)
    val maxPriceDisplay by viewModel.maxPriceDisplay.observeAsState(100000.0)

    // --- NAVIGATION STATES ---
    var selectedProduct by remember { mutableStateOf<ProductModel?>(null) }
    var isChatOpen by remember { mutableStateOf(false) }
    var showCart by remember { mutableStateOf(false) } // New state for cart

    // This must match your User Login ID
    val currentUserId = "Buyer_User_123"

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            // 1. SHOW CART
            showCart -> {
                CartScreen()
                // If you want a back button from cart, add onBackClick parameter to CartScreen
            }
            // 2. OPEN CHAT: When both conditions are met
            isChatOpen && selectedProduct != null -> {
                ChatScreen(
                    product = selectedProduct!!,
                    currentUserId = currentUserId,
                    onBackClick = {
                        isChatOpen = false
                    }
                )
            }
            // 3. OPEN PRODUCT DESCRIPTION
            selectedProduct != null -> {
                ProductDescriptionScreen(
                    product = selectedProduct!!,
                    viewModel = viewModel,
                    onBackClick = { selectedProduct = null },
                    onChatClick = { isChatOpen = true },
                    onNavigateToCart = { showCart = true } // Navigate to cart
                )
            }
            // 4. SHOW HOME LIST
            else -> {
                MainHomeContent(
                    products = products,
                    sliderValue = sliderValue,
                    maxPrice = maxPriceDisplay,
                    onSliderChange = { viewModel.onSliderChange(it) },
                    onCategorySelect = { viewModel.onCategorySelect(it) },
                    onProductClick = { product -> selectedProduct = product },
                    onChatClick = { product ->
                        selectedProduct = product
                        isChatOpen = true
                    }
                )
            }
        }
    }
}

@Composable
fun MainHomeContent(
    products: List<ProductModel>,
    sliderValue: Float,
    maxPrice: Double,
    onSliderChange: (Float) -> Unit,
    onCategorySelect: (Double) -> Unit,
    onProductClick: (ProductModel) -> Unit,
    onChatClick: (ProductModel) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val categories = listOf(
        "All" to R.drawable.img_2, "Painting" to R.drawable.img_1,
        "Bag" to R.drawable.img_3, "Craft" to R.drawable.img_4,
        "Sticks" to R.drawable.img_5, "Small" to R.drawable.img_6
    )

    val filteredList = products.filter { it.name.contains(searchQuery, ignoreCase = true) }

    LazyColumn(modifier = Modifier.fillMaxSize().background(Color.White)) {
        item {
            Column(modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)) {
                SearchBarInput(query = searchQuery, onQueryChange = { searchQuery = it })
                CategoryList(categories)
                Spacer(modifier = Modifier.height(16.dp))
                GradientPriceSliderSection(
                    currentSliderValue = sliderValue,
                    currentMaxPrice = maxPrice,
                    onValueChange = onSliderChange,
                    onCategorySelect = onCategorySelect
                )
            }
        }

        item {
            SectionHeader(title = "Sale", subtitle = "${filteredList.size} items Found", showArrow = true)
            ProductRow(filteredList, onProductClick, onChatClick)
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
            SectionHeader(title = "Recommended", subtitle = "Handpicked for you", showArrow = true)
            ProductRow(products = filteredList.reversed(), onProductClick, onChatClick)
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun ProductRow(
    products: List<ProductModel>,
    onProductClick: (ProductModel) -> Unit,
    onChatClick: (ProductModel) -> Unit
) {
    LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        items(products) { product ->
            ProductCard(product, onProductClick, onChatClick)
        }
    }
}

@Composable
fun ProductCard(
    product: ProductModel,
    onClick: (ProductModel) -> Unit,
    onChatClick: (ProductModel) -> Unit
) {
    Card(
        modifier = Modifier.width(160.dp).clickable { onClick(product) },
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFAFAFA))
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            AsyncImage(
                model = product.image,
                contentDescription = null,
                modifier = Modifier.fillMaxWidth().height(110.dp).clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop,
                error = painterResource(R.drawable.img_1)
            )
            Text(product.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, maxLines = 1)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("NRP ${product.price.toInt()}", color = OrangeBrand, fontWeight = FontWeight.Bold)
                IconButton(onClick = { onChatClick(product) }, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Chat, contentDescription = null, tint = OrangeBrand, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

// --- UTILITY COMPONENTS (REMAIN SAME) ---
@Composable
fun CategoryList(categories: List<Pair<String, Int>>) {
    LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(20.dp)) {
        items(categories) { (name, imageRes) ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(modifier = Modifier.size(55.dp).background(CreamBackground, CircleShape), contentAlignment = Alignment.Center) {
                    Image(painter = painterResource(id = imageRes), contentDescription = name, modifier = Modifier.size(30.dp))
                }
                Text(name, fontSize = 11.sp, fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Composable
fun SearchBarInput(query: String, onQueryChange: (String) -> Unit) {
    TextField(
        value = query, onValueChange = onQueryChange,
        placeholder = { Text("Search unique handmade items...") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        modifier = Modifier.fillMaxWidth().padding(16.dp).clip(RoundedCornerShape(12.dp)),
        colors = TextFieldDefaults.colors(focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent)
    )
}

@Composable
fun SectionHeader(title: String, subtitle: String?, showArrow: Boolean) {
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            subtitle?.let { Text(it, fontSize = 12.sp, color = Color.Gray) }
        }
        if (showArrow) Icon(Icons.Default.KeyboardArrowRight, contentDescription = null, tint = OrangeBrand)
    }
}

@Composable
fun GradientPriceSliderSection(
    currentSliderValue: Float,
    currentMaxPrice: Double,
    onValueChange: (Float) -> Unit,
    onCategorySelect: (Double) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).shadow(1.dp, RoundedCornerShape(12.dp))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Max Price", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = PriceTextViolet)
                Text("NRP ${currentMaxPrice.toInt()}", fontWeight = FontWeight.Bold, color = OrangeBrand)
            }
            Slider(value = currentSliderValue, onValueChange = onValueChange, valueRange = 0f..100f)
        }
    }
}
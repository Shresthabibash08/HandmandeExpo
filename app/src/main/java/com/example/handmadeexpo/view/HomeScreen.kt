package com.example.handmadeexpo.view

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
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

// Theme Colors
val OrangeBrand = Color(0xFFE65100)
val CreamBackground = Color(0xFFFFF8E1)

/**
 * Utility for generating consistent Chat IDs
 */
object ChatUtils {
    fun generateChatId(uid1: String, uid2: String): String {
        return if (uid1 < uid2) "${uid1}_${uid2}" else "${uid2}_${uid1}"
    }
}

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
    var showCart by remember { mutableStateOf(false) }

    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: "Guest"

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            // 1. SHOW CART
            showCart -> {
                CartScreen() 
                // To enable back navigation, you could pass: onBack = { showCart = false }
            }

            // 2. OPEN CHAT: Triggers when user clicks chat from Product Details or Card
            isChatOpen && selectedProduct != null -> {
                val chatId = ChatUtils.generateChatId(currentUserId, selectedProduct!!.sellerId)

                var sellerNameState by remember { mutableStateOf("Loading...") }
                LaunchedEffect(selectedProduct!!.sellerId) {
                    FirebaseDatabase.getInstance().getReference("sellers")
                        .child(selectedProduct!!.sellerId).child("name").get()
                        .addOnSuccessListener { sellerNameState = it.value?.toString() ?: "Seller" }
                }

                ChatScreen(
                    chatId = chatId,
                    sellerId = selectedProduct!!.sellerId,
                    sellerName = sellerNameState,
                    currentUserId = currentUserId,
                    onBackClick = { isChatOpen = false }
                )
            }

            // 3. OPEN PRODUCT DESCRIPTION
            selectedProduct != null -> {
                ProductDescriptionScreen(
                    product = selectedProduct!!,
                    viewModel = viewModel,
                    onBackClick = { selectedProduct = null },
                    onChatClick = { isChatOpen = true },
                    onNavigateToCart = { showCart = true }
                )
            }

            // 4. MAIN HOME CONTENT (LIST)
            else -> {
                MainHomeContent(
                    products = products,
                    sliderValue = sliderValue,
                    maxPrice = maxPriceDisplay,
                    onSliderChange = { viewModel.onSliderChange(it) },
                    onCategorySelect = { viewModel.onCategorySelect(it) },
                    onProductClick = { selectedProduct = it },
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
        "Bag" to R.drawable.img_3, "Craft" to R.drawable.img_4
    )

    val filteredList = products.filter { it.name.contains(searchQuery, ignoreCase = true) }

    LazyColumn(modifier = Modifier.fillMaxSize().background(Color.White)) {
        item {
            SearchBarInput(query = searchQuery, onQueryChange = { searchQuery = it })
            CategoryList(categories)
            Spacer(modifier = Modifier.height(16.dp))
            GradientPriceSliderSection(sliderValue, maxPrice, onSliderChange, onCategorySelect)
        }

        item {
            SectionHeader("Sale", "${filteredList.size} items Found", true)
            ProductRow(filteredList, onProductClick, onChatClick)
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
            SectionHeader("Recommended", "Handpicked for you", true)
            ProductRow(filteredList.reversed(), onProductClick, onChatClick)
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
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
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
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            Text(product.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, maxLines = 1)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("NRP ${product.price.toInt()}", color = OrangeBrand, fontWeight = FontWeight.Bold)
                IconButton(onClick = { onChatClick(product) }, modifier = Modifier.size(24.dp)) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Chat,
                        contentDescription = null,
                        tint = OrangeBrand,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

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
                        .size(55.dp)
                        .background(CreamBackground, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = imageRes),
                        contentDescription = name,
                        modifier = Modifier.size(30.dp)
                    )
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
        placeholder = { Text("Search items...") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clip(RoundedCornerShape(12.dp)),
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        )
    )
}

@Composable
fun SectionHeader(title: String, subtitle: String?, showArrow: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            subtitle?.let { Text(it, fontSize = 12.sp, color = Color.Gray) }
        }
        if (showArrow) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = OrangeBrand
            )
        }
    }
}

@Composable
fun GradientPriceSliderSection(
    value: Float,
    max: Double,
    onValueChange: (Float) -> Unit,
    onCategorySelect: (Double) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .shadow(1.dp, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Max Price", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text("NRP ${max.toInt()}", color = OrangeBrand, fontWeight = FontWeight.Bold)
            }
            Slider(
                value = value,
                onValueChange = onValueChange,
                valueRange = 0f..100f,
                colors = SliderDefaults.colors(
                    thumbColor = OrangeBrand,
                    activeTrackColor = OrangeBrand
                )
            )
        }
    }
}
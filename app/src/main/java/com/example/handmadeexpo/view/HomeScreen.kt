package com.example.handmadeexpo.view

import CategoryScreen
import androidx.activity.compose.BackHandler
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
import com.example.handmadeexpo.repo.CartRepoImpl
import com.example.handmadeexpo.ui.theme.MainColor
import com.example.handmadeexpo.viewmodel.ProductViewModel
import com.example.handmadeexpo.viewmodel.ProductViewModelFactory
import com.example.handmadeexpo.viewmodel.CartViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

val OrangeBrand = Color(0xFFE65100)
val CreamBackground = Color(0xFFFFF8E1)

object ChatUtils {
    fun generateChatId(uid1: String, uid2: String): String {
        return if (uid1 < uid2) "${uid1}_${uid2}" else "${uid2}_${uid1}"
    }
}

@Composable
fun HomeScreen(
    onReportProductClick: (String) -> Unit,
    onReportSellerClick: (String) -> Unit
) {
    val viewModel: ProductViewModel = viewModel(
        factory = ProductViewModelFactory(ProductRepoImpl())
    )
    val cartRepo = remember { CartRepoImpl() }
    val cartViewModel = remember { CartViewModel(cartRepo) }

    val products by viewModel.filteredProducts.observeAsState(initial = emptyList())
    val sliderValue by viewModel.sliderValue.observeAsState(100f)
    val maxPriceDisplay by viewModel.maxPriceDisplay.observeAsState(100000.0)

    var selectedProduct by remember { mutableStateOf<ProductModel?>(null) }
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var isChatOpen by remember { mutableStateOf(false) }
    var showCart by remember { mutableStateOf(false) }

    val currentUserId = remember { FirebaseAuth.getInstance().currentUser?.uid ?: "Guest" }

    // Handle system back button for nested screens
    BackHandler(enabled = isChatOpen || selectedProduct != null || showCart || selectedCategory != null) {
        when {
            showCart -> showCart = false
            isChatOpen -> isChatOpen = false
            selectedProduct != null -> selectedProduct = null
            selectedCategory != null -> selectedCategory = null
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            showCart -> {
                CartScreen(cartViewModel = cartViewModel, currentUserId = currentUserId)
            }
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
            selectedCategory != null && selectedProduct == null -> {
                CategoryScreen(
                    categoryName = selectedCategory!!,
                    viewModel = viewModel,
                    onBackClick = { selectedCategory = null },
                    onProductClick = { selectedProduct = it }
                )
            }
            selectedProduct != null -> {
                ProductDescriptionScreen(
                    product = selectedProduct!!,
                    currentUserId = currentUserId,
                    cartViewModel = cartViewModel,
                    viewModel = viewModel,
                    onBackClick = { selectedProduct = null },
                    onChatClick = { isChatOpen = true },
                    onNavigateToCart = { showCart = true },
                    onReportClick = { onReportProductClick(selectedProduct!!.productId) }
                )
            }
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
                    },
                    onCategoryClick = { categoryName ->
                        selectedCategory = categoryName
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
    onChatClick: (ProductModel) -> Unit,
    onCategoryClick: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val categories = listOf(
        "All" to R.drawable.all,
        "Statue" to R.drawable.statue,
        "Wooden Mask" to R.drawable.masks,
        "Singing Bowl" to R.drawable.bowls,
        "Painting" to R.drawable.painting,
        "Thanka" to R.drawable.thankas,
        "Wall Decor" to R.drawable.walldecore,
        "Others" to R.drawable.others
    )
    val filteredList = products.filter { it.name.contains(searchQuery, ignoreCase = true) }

    LazyColumn(modifier = Modifier.fillMaxSize().background(Color.White)) {
        item {
            Column(modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp)) {
                SearchBarInput(query = searchQuery, onQueryChange = { searchQuery = it })
                Text(
                    text = "Categories",
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = MainColor,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )

                CategoryList(categories = categories, onCategoryClick = onCategoryClick)

                Spacer(modifier = Modifier.height(16.dp))

                GradientPriceSliderSection(sliderValue, maxPrice, onSliderChange, onCategorySelect)
            }
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
fun CategoryList(
    categories: List<Pair<String, Int>>,
    onCategoryClick: (String) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        items(categories) { (name, imageRes) ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable { onCategoryClick(name) }
            ) {
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Max Price", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text("NPR ${max.toInt()}", color = OrangeBrand, fontWeight = FontWeight.Bold)
            }
            Slider(
                value = value,
                onValueChange = {
                    onValueChange(it)
                    // ✅ FIX: Calculate actual price from slider percentage
                    val actualPrice = (it / 100f) * 100000.0
                    onCategorySelect(actualPrice)
                },
                valueRange = 0f..100f,  // Slider position 0-100%
                colors = SliderDefaults.colors(
                    thumbColor = OrangeBrand,
                    activeTrackColor = OrangeBrand
                )
            )
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
        modifier = Modifier
            .width(160.dp)
            .clickable { onClick(product) },
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
fun SearchBarInput(query: String, onQueryChange: (String) -> Unit) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
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
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = OrangeBrand)
        }
    }
}
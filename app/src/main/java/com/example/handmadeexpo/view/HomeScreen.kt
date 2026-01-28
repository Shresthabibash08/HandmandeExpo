package com.example.handmadeexpo.view

//import CategoryScreen
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

    BackHandler(enabled = isChatOpen || selectedProduct != null || showCart || selectedCategory != null) {
        when {
            showCart -> showCart = false
            isChatOpen -> isChatOpen = false
            selectedProduct != null -> selectedProduct = null
            selectedCategory != null -> selectedCategory = null
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F7FA))
    ) {
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

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F7FA))
    ) {
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                SearchBarInput(query = searchQuery, onQueryChange = { searchQuery = it })

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Categories",
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    color = Color(0xFF212121),
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )

                CategoryList(categories = categories, onCategoryClick = onCategoryClick)

                Spacer(modifier = Modifier.height(16.dp))

                GradientPriceSliderSection(sliderValue, maxPrice, onSliderChange, onCategorySelect)

                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        item {
            SectionHeader("Featured Products", "${filteredList.size} items available", true)
            ProductRow(filteredList, onProductClick, onChatClick)
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }

        item {
            SectionHeader("Recommended", "Handpicked for you", true)
            ProductRow(filteredList.reversed(), onProductClick, onChatClick)
            Spacer(modifier = Modifier.height(24.dp))
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
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(categories) { (name, imageRes) ->
            Card(
                modifier = Modifier
                    .width(80.dp)
                    .clickable { onCategoryClick(name) },
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(CreamBackground, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = imageRes),
                            contentDescription = name,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        name,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF424242),
                        maxLines = 1
                    )
                }
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
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "Price Range",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color(0xFF212121)
                    )
                    Text(
                        "Filter by maximum price",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = OrangeBrand.copy(alpha = 0.1f)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        "NPR ${max.toInt()}",
                        color = OrangeBrand,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Slider(
                value = value,
                onValueChange = {
                    onValueChange(it)
                    // Calculate actual price from slider percentage
                    val actualPrice = (it / 100f) * 100000.0
                    onCategorySelect(actualPrice)
                },
                valueRange = 0f..100f,
                colors = SliderDefaults.colors(
                    thumbColor = OrangeBrand,
                    activeTrackColor = OrangeBrand,
                    inactiveTrackColor = OrangeBrand.copy(alpha = 0.2f)
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
            .width(180.dp)
            .clickable { onClick(product) },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Card(
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                AsyncImage(
                    model = product.image,
                    contentDescription = product.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                product.name,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF212121),
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "Price",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                    Text(
                        "NPR ${product.price.toInt()}",
                        color = OrangeBrand,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }

                IconButton(
                    onClick = { onChatClick(product) },
                    modifier = Modifier
                        .size(40.dp)
                        .background(OrangeBrand.copy(alpha = 0.1f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Chat,
                        contentDescription = "Chat with seller",
                        tint = OrangeBrand,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBarInput(query: String, onQueryChange: (String) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        TextField(
            value = query,
            onValueChange = onQueryChange,
            placeholder = {
                Text(
                    "Search for products...",
                    color = Color.Gray
                )
            },
            leadingIcon = {
                Icon(
                    Icons.Default.Search,
                    contentDescription = "Search",
                    tint = Color(0xFF1E88E5)
                )
            },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(onClick = { onQueryChange("") }) {
                        Icon(
                            Icons.Default.Clear,
                            contentDescription = "Clear",
                            tint = Color.Gray
                        )
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            singleLine = true
        )
    }
}

@Composable
fun SectionHeader(title: String, subtitle: String?, showArrow: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF212121)
            )
            subtitle?.let {
                Text(
                    it,
                    fontSize = 13.sp,
                    color = Color.Gray
                )
            }
        }
        if (showArrow) {
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "View all",
                tint = OrangeBrand,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}
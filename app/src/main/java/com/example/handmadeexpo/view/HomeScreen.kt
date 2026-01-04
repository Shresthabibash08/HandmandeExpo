package com.example.handmadeexpo.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Brush
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
val GradientStart = Color(0xFF4CAF50)
val GradientMiddle = Color(0xFFFFC107)
val GradientEnd = Color(0xFFD32F2F)

@Composable
fun HomeScreen() {
    // 1. Initialize ViewModel with Factory
    val viewModel: ProductViewModel = viewModel(
        factory = ProductViewModelFactory(ProductRepoImpl())
    )

    // 2. Observe Data from ViewModel
    val products by viewModel.filteredProducts.observeAsState(initial = emptyList())
    val sliderValue by viewModel.sliderValue.observeAsState(100f)
    val maxPriceDisplay by viewModel.maxPriceDisplay.observeAsState(100000.0)

    // 3. Navigation State
    var selectedProduct by remember { mutableStateOf<ProductModel?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        if (selectedProduct == null) {
            MainHomeContent(
                products = products,
                sliderValue = sliderValue,
                maxPrice = maxPriceDisplay,
                onSliderChange = { viewModel.onSliderChange(it) },
                onCategorySelect = { viewModel.onCategorySelect(it) },
                onProductClick = { product -> selectedProduct = product }
            )
        } else {
            // Updated to use the ProductDescriptionScreen from development
            ProductDescriptionScreen(
                product = selectedProduct!!,
                onBackClick = { selectedProduct = null }
            )
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
    onProductClick: (ProductModel) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    // Combined Category List with Images
    val categories = listOf(
        "All" to R.drawable.img_2, "Painting" to R.drawable.img_1,
        "Bag" to R.drawable.img_3, "Craft" to R.drawable.img_4,
        "Sticks" to R.drawable.img_5, "Small" to R.drawable.img_6
    )

    val filteredList = products.filter {
        it.name.contains(searchQuery, ignoreCase = true)
    }

    LazyColumn(modifier = Modifier.fillMaxSize().background(Color.White)) {
        item {
            Column(modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)) {
                SearchBarInput(query = searchQuery, onQueryChange = { searchQuery = it })
//                PromoBanner()
                Spacer(modifier = Modifier.height(16.dp))
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
            if (filteredList.isEmpty()) {
                Box(modifier = Modifier.fillMaxWidth().padding(30.dp), contentAlignment = Alignment.Center) {
                    Text("No items found", color = Color.Gray)
                }
            } else {
                ProductRow(filteredList, onProductClick)
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
            SectionHeader(title = "Recommended", subtitle = "Handpicked for you", showArrow = true)
            ProductRow(products = filteredList.reversed(), onProductClick = onProductClick)
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GradientPriceSliderSection(
    currentSliderValue: Float,
    currentMaxPrice: Double,
    onValueChange: (Float) -> Unit,
    onCategorySelect: (Double) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }
    val pricePoints = listOf(500, 1000, 5000, 10000, 50000, 100000)

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).shadow(1.dp, RoundedCornerShape(12.dp))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(modifier = Modifier.clickable { isExpanded = !isExpanded }) {
                    Text("Max Price", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = PriceTextViolet)
                    Icon(if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown, contentDescription = null)
                }
                Text("NRP ${currentMaxPrice.toInt()}", fontWeight = FontWeight.Bold, color = OrangeBrand)
            }

            AnimatedVisibility(visible = isExpanded) {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 8.dp)) {
                    items(pricePoints) { price ->
                        Box(modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (currentMaxPrice.toInt() == price) OrangeBrand else Color(0xFFF5F5F5))
                            .clickable { onCategorySelect(price.toDouble()); isExpanded = false }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(text = if (price >= 1000) "${price/1000}k" else "$price", color = if (currentMaxPrice.toInt() == price) Color.White else TextGray)
                        }
                    }
                }
            }

            Slider(
                value = currentSliderValue,
                onValueChange = onValueChange,
                valueRange = 0f..100f,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun ProductRow(products: List<ProductModel>, onProductClick: (ProductModel) -> Unit) {
    LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        items(products) { product ->
            ProductCard(product, onProductClick)
        }
    }
}

@Composable
fun ProductCard(product: ProductModel, onClick: (ProductModel) -> Unit) {
    Card(
        modifier = Modifier.width(150.dp).clickable { onClick(product) },
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
            Text("NRP ${product.price.toInt()}", color = OrangeBrand, fontWeight = FontWeight.Bold)
        }
    }
}

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
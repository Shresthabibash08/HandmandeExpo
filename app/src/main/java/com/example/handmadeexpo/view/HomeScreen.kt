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
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
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

    // 3. Navigation State (Holds the clicked product)
    var selectedProduct by remember { mutableStateOf<ProductModel?>(null) }

    if (selectedProduct == null) {
        // Show the Main Home List
        MainHomeContent(
            products = products,
            sliderValue = sliderValue,
            maxPrice = maxPriceDisplay,
            onSliderChange = { viewModel.onSliderChange(it) },
            onCategorySelect = { viewModel.onCategorySelect(it) },
            onProductClick = { product -> selectedProduct = product } // Navigate to Detail
        )
    } else {
        // Show the Product Detail Screen
        // Ensure your ProductDescriptionScreen.kt file has the function name 'ProductDescriptionScreen'
        ProductDescriptionScreen(
            product = selectedProduct!!,
            onBackClick = { selectedProduct = null } // Navigate back to Home
        )

        // --- MAIN CONTENT ---
        if (selectedProduct == null) {
            MainHomeContent(
                products = products ?: emptyList(),
                onProductClick = { selectedProduct = it }
            )
        } else {
            ProductDescription(
                name = selectedProduct!!.name,
                price = "NRP ${selectedProduct!!.price}",
                imageUrl = selectedProduct!!.image,
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

    // Static Categories
    val categories = listOf(
        "All" to R.drawable.img_2, "Painting" to R.drawable.img_1,
        "Bag" to R.drawable.img_3, "Craft" to R.drawable.img_4,
        "Sticks" to R.drawable.img_5, "Small" to R.drawable.img_6
    )

    LazyColumn(modifier = Modifier.fillMaxSize().background(Color.White)) {
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    //.background(color = White12, shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
                    .padding(bottom = 24.dp)
            ) {
                SearchBarInput(query = searchQuery, onQueryChange = { searchQuery = it })
                PromoBanner()
                Spacer(modifier = Modifier.height(16.dp))
                CategoryList(categories)

                Spacer(modifier = Modifier.height(16.dp))

                // --- EXPANDABLE PRICE SLIDER ---
                GradientPriceSliderSection(
                    currentSliderValue = sliderValue,
                    currentMaxPrice = maxPrice,
                    onValueChange = onSliderChange,
                    onCategorySelect = onCategorySelect
                )
            }
        }

        item {
            SectionHeader(title = " Sale", subtitle = "${products.size} items Found", showArrow = true)
            if (products.isEmpty()) {
                Box(modifier = Modifier.fillMaxWidth().padding(30.dp), contentAlignment = Alignment.Center) {
                    Text("No items under NRP ${maxPrice.toInt()}", color = Color.Gray)
                }
            } else {
                ProductRow(products, onProductClick)
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
            SectionHeader(title = "Recommended for you", subtitle = "Buy them before it's too late!", showArrow = true)
            ProductRow(products = filteredProducts.reversed(), onProductClick = onProductClick)
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
    // State to handle Expand/Collapse
    var isExpanded by remember { mutableStateOf(false) }

    val pricePoints = listOf(500, 1000, 2000, 5000, 10000, 20000, 50000, 100000)

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .shadow(elevation = 1.dp, shape = RoundedCornerShape(12.dp))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Header Row: Label + Toggle Icon + Current Value
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Clickable Label Area
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { isExpanded = !isExpanded }
                        .padding(4.dp)
                ) {
                    Text(
                        text = "Max Price",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = PriceTextViolet
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = "Expand",
                        tint = TextGray,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Price Display
                Text(
                    text = "NRP ${currentMaxPrice.toInt()}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = OrangeBrand
                )
            }

            // Expandable List Content
            AnimatedVisibility(visible = isExpanded) {
                Column {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Quick Select:", fontSize = 11.sp, color = TextGray)
                    Spacer(modifier = Modifier.height(8.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(pricePoints) { price ->
                            val isSelected = currentMaxPrice.toInt() == price
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(if (isSelected) OrangeBrand else Color(0xFFF5F5F5))
                                    .clickable {
                                        onCategorySelect(price.toDouble())
                                        isExpanded = false // Close the list after selection
                                    }
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = if (price >= 1000) "${price / 1000}k" else "$price",
                                    fontSize = 13.sp,
                                    color = if (isSelected) Color.White else TextGray,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Slider Track
            Box(
                contentAlignment = Alignment.CenterStart,
                modifier = Modifier.fillMaxWidth().height(30.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(brush = Brush.horizontalGradient(colors = listOf(GradientStart, GradientMiddle, GradientEnd)))
                )
                Slider(
                    value = currentSliderValue,
                    onValueChange = onValueChange,
                    valueRange = 0f..100f,
                    colors = SliderDefaults.colors(
                        thumbColor = Color.Transparent,
                        activeTrackColor = Color.Transparent,
                        inactiveTrackColor = Color.Transparent
                    ),
                    thumb = {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .background(Color.White, CircleShape)
                                .border(width = 2.dp, color = GradientStart, shape = CircleShape)
                                .shadow(2.dp, CircleShape)
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

// --- HELPER COMPOSABLES ---

@Composable
fun ProductRow(products: List<ProductModel>, onProductClick: (ProductModel) -> Unit) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(products) { product ->
            ProductCard(product, onProductClick)
        }
    }
}

@Composable
fun ProductCard(product: ProductModel, onClick: (ProductModel) -> Unit) {
    Card(
        modifier = Modifier.width(140.dp).clickable { onClick(product) },
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFAFAFA))
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            AsyncImage(
                model = product.image,
                contentDescription = product.name,
                modifier = Modifier.fillMaxWidth().height(100.dp),
                contentScale = ContentScale.Crop,
                error = painterResource(R.drawable.img_1)
            )
            Text(product.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, maxLines = 1)
            Text("NRP ${product.price.toInt()}", fontSize = 14.sp, color = OrangeBrand, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun SearchBarInput(query: String, onQueryChange: (String) -> Unit) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text("Search here", color = TextGray) },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        modifier = Modifier.fillMaxWidth().padding(16.dp).height(56.dp).clip(RoundedCornerShape(12.dp)),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        )
    )
}

@Composable
fun PromoBanner() {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).height(140.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
                .background(Brush.horizontalGradient(colors = listOf(Color(0xFFFFD54F), Color(0xFFFFB300))))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("70% Off", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Button(onClick = {}, colors = ButtonDefaults.buttonColors(containerColor = OrangeBrand)) {
                    Text("Shop Now")
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
                    modifier = Modifier.size(50.dp).background(Color.White, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = imageRes),
                        contentDescription = name,
                        modifier = Modifier.size(32.dp)
                    )
                }
                Text(name, fontSize = 11.sp)
            }
        }
    }
}

@Composable
fun SectionHeader(title: String, subtitle: String?, showArrow: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 18.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
            subtitle?.let { Text(it, fontSize = 12.sp, color = Gray) }
        }
    }
}

@Composable
fun ProductDescription(
    name: String,
    price: String,
    imageUrl: String,
    onBackClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Button(onClick = onBackClick, modifier = Modifier.padding(16.dp)) { Text("Back") }

        AsyncImage(
            model = imageUrl,
            contentDescription = name,
            modifier = Modifier.fillMaxWidth().height(250.dp),
            contentScale = ContentScale.Crop
        )

        Text(name, fontSize = 24.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold, modifier = Modifier.padding(16.dp))
        Text(price, fontSize = 20.sp, color = Orange, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold, modifier = Modifier.padding(horizontal = 16.dp))
    }
}

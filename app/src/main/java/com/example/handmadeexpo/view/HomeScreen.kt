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
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.handmadeexpo.R
import com.example.handmadeexpo.model.ProductModel
import com.example.handmadeexpo.repo.ProductRepoImpl
import com.example.handmadeexpo.ui.theme.Gray
import com.example.handmadeexpo.ui.theme.Orange
import com.example.handmadeexpo.ui.theme.White12
import com.example.handmadeexpo.viewmodel.ProductViewModel

@Composable
fun HomeScreen() {


    var selectedProduct by remember { mutableStateOf<ProductModel?>(null) }


    val viewModel: ProductViewModel = remember { ProductViewModel(ProductRepoImpl()) }


    LaunchedEffect(Unit) {
        viewModel.getAllProduct()
    }

    val products by viewModel.allProducts.observeAsState(emptyList())

    Box(modifier = Modifier.fillMaxSize()) {


        Image(
            painter = painterResource(id = R.drawable.bg4), // Replace with your drawable
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
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
    onProductClick: (ProductModel) -> Unit
) {
    val categories = listOf(
        "All",
        "Painting",
        "Bag",
        "Craft",
        "Insane Sticks",
        "Small Sculpture"
    )

    var searchQuery by remember { mutableStateOf("") }

    val filteredProducts = products.filter {
        it.name.contains(searchQuery, ignoreCase = true)
    }

    LazyColumn(modifier = Modifier.fillMaxSize().background(Color.Transparent)) {
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
            }
        }

        item {
            SectionHeader(title = "Sale", subtitle = "${filteredProducts.size} items Left", showArrow = true)
            ProductRow(products = filteredProducts, onProductClick = onProductClick)
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
            SectionHeader(title = "Recommended for you", subtitle = "Buy them before it's too late!", showArrow = true)
            ProductRow(products = filteredProducts.reversed(), onProductClick = onProductClick)
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun ProductRow(
    products: List<ProductModel>,
    onProductClick: (ProductModel) -> Unit
) {
    LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        items(products) { product ->
            ProductCard(
                product = product,
                onClick = { onProductClick(product) }
            )
        }
    }
}

@Composable
fun ProductCard(
    product: ProductModel,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.width(140.dp).clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFAFAFA))
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            AsyncImage(
                model = product.image,
                contentDescription = product.name,
                modifier = Modifier.fillMaxWidth().height(100.dp),
                contentScale = ContentScale.Crop
            )
            Text(product.name, fontSize = 14.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold, maxLines = 1)
            Text("NRP ${product.price}", fontSize = 14.sp, color = Orange, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
        }
    }
}

@Composable
fun SearchBarInput(query: String, onQueryChange: (String) -> Unit) {
    TextField(
        value = query, onValueChange = onQueryChange,
        placeholder = { Text("Search here", color = Gray) },
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
    Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).height(140.dp), shape = RoundedCornerShape(16.dp)) {
        Box(modifier = Modifier.fillMaxSize().background(Brush.horizontalGradient(colors = listOf(Color(0xFFFFD54F), Color(0xFFFFB300))))) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("70% Off", fontSize = 24.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                Button(onClick = {}, colors = ButtonDefaults.buttonColors(containerColor = Orange)) { Text("Shop Now") }
            }
        }
    }
}

@Composable
fun CategoryList(categories: List<String>) {
    LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(20.dp)) {
        items(categories) { name ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(modifier = Modifier.size(50.dp).background(Color.White, RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) {
                    // You can add category image here if needed
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
            Text(title, fontSize = 18.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
            subtitle?.let { Text(it, fontSize = 12.sp, color = Gray) }
        }
        if (showArrow) Icon(Icons.Default.KeyboardArrowRight, contentDescription = null, tint = Orange)
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
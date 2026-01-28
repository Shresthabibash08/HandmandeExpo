package com.example.handmadeexpo.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.handmadeexpo.model.ProductModel
import com.example.handmadeexpo.viewmodel.ProductViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryScreen(
    categoryName: String,
    viewModel: ProductViewModel,
    onBackClick: () -> Unit,
    onProductClick: (ProductModel) -> Unit
) {
    // 1. Fetch data based on the selection
    LaunchedEffect(categoryName) {
        if (categoryName == "All") {
            viewModel.getAllProduct()
        } else {
            viewModel.getProductByCategory(categoryName)
        }
    }

    // 2. Switch which LiveData we observe
    val productsState by if (categoryName == "All") {
        viewModel.allProducts.observeAsState(null)
    } else {
        viewModel.allProductsCategory.observeAsState(null)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F7FA))
    ) {
        // Modern Header
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(4.dp, RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                // Back Button Row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color(0xFFF5F5F5), CircleShape)
                    ) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF212121)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        "Back",
                        fontSize = 14.sp,
                        color = Color(0xFF757575)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Category Title Row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(getCategoryColor(categoryName).copy(alpha = 0.15f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            getCategoryIcon(categoryName),
                            contentDescription = null,
                            tint = getCategoryColor(categoryName),
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            categoryName,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF212121)
                        )
                        Text(
                            "${productsState?.size ?: 0} products",
                            fontSize = 13.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Content Logic
        when {
            // State: Still Loading
            productsState == null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(
                            color = getCategoryColor(categoryName),
                            strokeWidth = 3.dp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "Loading products...",
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            // State: Loaded but No items found
            productsState!!.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.SearchOff,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = Color.Gray.copy(alpha = 0.3f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "No products found",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Gray
                        )
                        Text(
                            "Try browsing other categories",
                            fontSize = 14.sp,
                            color = Color.Gray.copy(alpha = 0.7f)
                        )
                    }
                }
            }

            // State: Data arrived successfully
            else -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(productsState!!, key = { it.productId }) { product ->
                        ProductCard(
                            product = product,
                            onClick = { onProductClick(product) },
                            onChatClick = { /* Add chat navigation if needed */ }
                        )
                    }
                }
            }
        }
    }
}

// Helper function to get category-specific icons
@Composable
private fun getCategoryIcon(categoryName: String): androidx.compose.ui.graphics.vector.ImageVector {
    return when (categoryName.lowercase()) {
        "all" -> Icons.Default.GridView
        "jewelry" -> Icons.Default.Diamond
        "clothing", "clothes" -> Icons.Default.Checkroom
        "pottery" -> Icons.Default.Coffee
        "woodwork", "wood" -> Icons.Default.Carpenter
        "textiles", "textile" -> Icons.Default.Texture
        "painting", "art" -> Icons.Default.Palette
        "decoration", "decor" -> Icons.Default.Lightbulb
        "bags" -> Icons.Default.ShoppingBag
        "accessories" -> Icons.Default.Watch
        else -> Icons.Default.Category
    }
}

// Helper function to get category-specific colors
@Composable
private fun getCategoryColor(categoryName: String): Color {
    return when (categoryName.lowercase()) {
        "all" -> Color(0xFF1E88E5) // Blue
        "jewelry" -> Color(0xFF9C27B0) // Purple
        "clothing", "clothes" -> Color(0xFFE91E63) // Pink
        "pottery" -> Color(0xFF795548) // Brown
        "woodwork", "wood" -> Color(0xFF8D6E63) // Wood brown
        "textiles", "textile" -> Color(0xFF00BCD4) // Cyan
        "painting", "art" -> Color(0xFFFF5722) // Deep Orange
        "decoration", "decor" -> Color(0xFFFFC107) // Amber
        "bags" -> Color(0xFF673AB7) // Deep Purple
        "accessories" -> Color(0xFF009688) // Teal
        else -> Color(0xFF4CAF50) // Green (default)
    }
}
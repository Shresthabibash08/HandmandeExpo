import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.handmadeexpo.model.ProductModel
import com.example.handmadeexpo.view.OrangeBrand
import com.example.handmadeexpo.view.ProductCard
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

    // 2. IMPORTANT: Switch which LiveData we observe
    // If "All", we watch 'allProducts'. If specific, we watch 'allProductsCategory'.
    val productsState by if (categoryName == "All") {
        viewModel.allProducts.observeAsState(null)
    } else {
        viewModel.allProductsCategory.observeAsState(null)
    }

    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
        // --- TOP NAVIGATION BAR ---
        TopAppBar(
            title = { Text(categoryName, fontWeight = FontWeight.Bold) },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
        )

        // --- CONTENT LOGIC ---
        when {
            // State: Still Loading
            productsState == null -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = OrangeBrand)
                }
            }

            // State: Loaded but No items found
            productsState!!.isEmpty() -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "No products found in $categoryName",
                            color = Color.Gray,
                            fontSize = 16.sp
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
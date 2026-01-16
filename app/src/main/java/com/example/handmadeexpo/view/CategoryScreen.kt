import androidx.compose.foundation.ExperimentalFoundationApi
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
import com.example.handmadeexpo.ui.theme.MainColor
import com.example.handmadeexpo.view.OrangeBrand
import com.example.handmadeexpo.view.ProductCard
import com.example.handmadeexpo.view.SearchBarInput
import com.example.handmadeexpo.viewmodel.ProductViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryScreen(
    categoryName: String,
    viewModel: ProductViewModel,
    onBackClick: () -> Unit,
    onProductClick: (ProductModel) -> Unit

) {


    LaunchedEffect(categoryName) {
        if (categoryName == "All") {
            viewModel.getAllProduct()
        } else {
            viewModel.getProductByCategory(categoryName)
        }
    }

    val products by viewModel.allProductsCategory.observeAsState(null) // Start with null to detect "Loading"

    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {

        TopAppBar(
            title = { Text(categoryName, fontWeight = FontWeight.Bold) },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            }
        )

        when {
            // Case 1: Data is still fetching (null)
            products == null -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = OrangeBrand)
                }
            }
            // Case 2: Fetch finished but list is empty
            products!!.isEmpty() -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No products found in $categoryName", color = Color.Gray)
                }
            }
            // Case 3: Data arrived successfully
            else -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(products!!, key = { it.productId }) { product ->
                        ProductCard(
                            product = product,
                            onClick = { onProductClick(product) },
                            onChatClick = { /* chat logic */ }
                        )
                    }
                }
            }
        }
    }
}
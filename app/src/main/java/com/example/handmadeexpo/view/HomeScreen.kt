package com.example.handmadeexpo.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.handmadeexpo.R

// Colors matching the design
val PrimaryBlue = Color(0xFF2196F3)
val LightBlue = Color(0xFFE3F2FD)
val OrangePrice = Color(0xFFFF5722)
val GrayText = Color(0xFF757575)
val LightGray = Color(0xFFF5F5F5)
val StarYellow = Color(0xFFFFC107)

data class Product(
    val id: Int,
    val image: Int,
    val name: String,
    val price: Double,
    val originalPrice: Double? = null,
    val rating: Float = 4.5f,
    val reviews: Int = 100,
    val isFavorite: Boolean = false
)

data class Category(
    val image: Int,
    val name: String,
    val color: Color
)

@Composable
fun HomeScreen() {
    val products = listOf(
        Product(1, R.drawable.img_1, "Painting", 500.00, 1200.00, 4.5f, 86),
        Product(2, R.drawable.img_6, "Sculpture", 600.00, 1200.00, 4.2f, 46),
        Product(3, R.drawable.img_12, "Cloth", 1500.00, 3000.00, 4.8f, 120),
        Product(4, R.drawable.img_10, "Decorator", 250.00, 500.00, 4.3f, 65),
        Product(5, R.drawable.img_17, "Hat", 180.00, null, 4.6f, 45),
    )

    val categories = listOf(
        Category(R.drawable.img_18, "All", Color(0xFF2196F3)),
        Category(R.drawable.img_19, "Clothes", Color(0xFFE91E63)),
        Category(R.drawable.img_20, "Electronic", Color(0xFF9C27B0)),
        Category(R.drawable.img_21, "Bowl", Color(0xFF4CAF50)),
        Category(R.drawable.img_22, "Shoes", Color(0xFFFF9800)),
    )

    var selectedCategory by remember { mutableStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        item { CategoryHeaderSection() }
        item { SearchBarSection(searchQuery) { searchQuery = it } }
        item { BannerSection() }
        item {
            CategoriesSection(
                categories = categories,
                selectedIndex = selectedCategory,
                onCategorySelected = { selectedCategory = it }
            )
        }
        item { FlashSaleHeader() }
        item { ProductsGrid(products) }
        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}



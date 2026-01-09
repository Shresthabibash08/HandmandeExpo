package com.example.handmadeexpo.view

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.handmadeexpo.R
import com.example.handmadeexpo.model.CartItem
import com.example.handmadeexpo.model.ProductModel
import com.example.handmadeexpo.viewmodel.CartViewModel
import com.example.handmadeexpo.viewmodel.ProductViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDescriptionScreen(
    product: ProductModel,
    currentUserId: String,
    cartViewModel: CartViewModel,
    viewModel: ProductViewModel,
    onBackClick: () -> Unit,
    onChatClick: () -> Unit,
    onNavigateToCart: () -> Unit
) {
    val context = LocalContext.current

    // Theme Colors
    val OrangeBrand = Color(0xFFE65100)
    val CreamBackground = Color(0xFFFFF8E1)
    val TextGray = Color(0xFF757575)

    // --- State Management ---
    var selectedRating by remember { mutableIntStateOf(0) }
    var hasRated by remember { mutableStateOf(false) }
    var showRatingDialog by remember { mutableStateOf(false) }
    var showCartAddedMessage by remember { mutableStateOf(false) }

    // Calculate average rating dynamically
    val averageRating = if (product.ratingCount > 0) {
        product.totalRating.toFloat() / product.ratingCount
    } else {
        0f
    }

    // --- Rating Dialog ---
    if (showRatingDialog) {
        AlertDialog(
            onDismissRequest = { showRatingDialog = false },
            title = { Text("Rate this Product") },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("How would you rate this product?")
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        repeat(5) { index ->
                            Icon(
                                imageVector = if (index < selectedRating) Icons.Filled.Star else Icons.Outlined.StarOutline,
                                contentDescription = "Rate ${index + 1} stars",
                                tint = if (index < selectedRating) Color(0xFFFFB300) else Color.LightGray,
                                modifier = Modifier
                                    .size(40.dp)
                                    .clickable { selectedRating = index + 1 }
                                    .padding(4.dp)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (selectedRating > 0) {
                            viewModel.rateProduct(product.productId, selectedRating) { success ->
                                if (success) {
                                    hasRated = true
                                    showRatingDialog = false
                                }
                            }
                        }
                    },
                    enabled = selectedRating > 0
                ) {
                    Text("Submit")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRatingDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Product Details", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CreamBackground)
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = Color.White,
                tonalElevation = 8.dp,
                modifier = Modifier.height(100.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 1. Chat Button
                    OutlinedIconButton(
                        onClick = onChatClick,
                        modifier = Modifier.size(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, OrangeBrand)
                    ) {
                        Icon(Icons.Default.Chat, contentDescription = "Chat", tint = OrangeBrand)
                    }

                    // 2. Add to Cart Button
                    OutlinedButton(
                        onClick = {
                            val cartItem = CartItem.fromProduct(product, currentUserId)
                            cartViewModel.addToCart(cartItem) { success ->
                                if (success) {
                                    showCartAddedMessage = true
                                    Toast.makeText(context, "Added to cart", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "Failed to add", Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        modifier = Modifier.weight(1f).height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, OrangeBrand)
                    ) {
                        Text("Add to Cart", color = OrangeBrand, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }

                    // 3. Buy Now Button (Merged Data Keys)
                    Button(
                        onClick = {
                            val intent = Intent(context, CheckoutActivity::class.java).apply {
                                putExtra("productId", product.productId)
                                putExtra("name", product.name)
                                putExtra("price", product.price)
                                putExtra("image", product.image)
                                // Standardized keys for consistency
                                putExtra("product_id", product.productId)
                                putExtra("product_name", product.name)
                                putExtra("product_price", product.price)
                            }
                            context.startActivity(intent)
                        },
                        modifier = Modifier.weight(1f).height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = OrangeBrand)
                    ) {
                        Text("Buy Now", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        },
        snackbarHost = {
            if (showCartAddedMessage) {
                Snackbar(
                    modifier = Modifier.padding(16.dp).clickable { onNavigateToCart() },
                    containerColor = Color(0xFF4CAF50),
                    action = {
                        TextButton(onClick = onNavigateToCart) {
                            Text("VIEW", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                ) {
                    Text("Added to cart!", color = Color.White)
                }
                LaunchedEffect(Unit) {
                    delay(3000)
                    showCartAddedMessage = false
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
                .verticalScroll(rememberScrollState())
        ) {
            // 1. Image Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(380.dp)
                    .background(CreamBackground),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = product.image,
                    contentDescription = product.name,
                    modifier = Modifier
                        .fillMaxSize(0.85f)
                        .clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Fit,
                    error = painterResource(R.drawable.img_1)
                )
            }

            // 2. Product Info Section
            Column(modifier = Modifier.fillMaxWidth().padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = product.name,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.weight(1f),
                        lineHeight = 32.sp
                    )
                    Text(
                        text = "NRP ${product.price.toInt()}",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = OrangeBrand
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 3. Rating & Feedback Section
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        repeat(5) { index ->
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = if (index < averageRating.toInt()) Color(0xFFFFB300) else Color.LightGray,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = String.format("%.1f (%d Reviews)", averageRating, product.ratingCount),
                            color = TextGray,
                            fontSize = 14.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (!hasRated) {
                        OutlinedButton(
                            onClick = { showRatingDialog = true },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, OrangeBrand)
                        ) {
                            Icon(Icons.Default.Star, contentDescription = null, tint = OrangeBrand, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Rate this Product", color = OrangeBrand)
                        }
                    } else {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(12.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFF2E7D32), modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Thank you for rating!", color = Color(0xFF2E7D32), fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 20.dp), thickness = 0.5.dp)

                // 4. Description
                Text(text = "Description", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = product.description.ifEmpty { "This premium handmade product is part of our exclusive Expo collection." },
                    fontSize = 16.sp, color = TextGray, lineHeight = 24.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                // 5. Stock Status
                Text(text = "Stock Information", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = if (product.stock > 0) "Available: ${product.stock} items in stock" else "Currently out of stock",
                    fontSize = 16.sp,
                    color = if (product.stock > 0) Color(0xFF2E7D32) else Color.Red,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}
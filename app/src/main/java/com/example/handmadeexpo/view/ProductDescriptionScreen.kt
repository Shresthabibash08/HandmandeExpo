package com.example.handmadeexpo.view

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
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

// --- Modern Palette ---
private val AppBackground = Color(0xFFF5F7FA)
private val PrimaryGreen = Color(0xFF4CAF50)
private val DarkText = Color(0xFF212121)
private val MutedText = Color(0xFF757575)
private val GoldStar = Color(0xFFFFB300)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDescriptionScreen(
    product: ProductModel,
    currentUserId: String,
    cartViewModel: CartViewModel,
    viewModel: ProductViewModel,
    onBackClick: () -> Unit,
    onChatClick: () -> Unit,
    onNavigateToCart: () -> Unit,
    onReportClick: () -> Unit
) {
    val context = LocalContext.current

    // --- State ---
    var selectedRating by remember { mutableIntStateOf(0) }
    var hasRated by remember { mutableStateOf(false) }
    var showRatingDialog by remember { mutableStateOf(false) }
    var showCartAddedMessage by remember { mutableStateOf(false) }
    var showSellerDetails by remember { mutableStateOf(false) }

    // Average Rating Calculation
    val averageRating = if (product.ratingCount > 0) {
        product.totalRating.toFloat() / product.ratingCount
    } else {
        0f
    }

    // --- Rating Dialog ---
    if (showRatingDialog) {
        AlertDialog(
            onDismissRequest = { showRatingDialog = false },
            containerColor = Color.White,
            title = {
                Text(
                    "Rate Product",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = DarkText
                )
            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("How was your experience?", color = MutedText, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        repeat(5) { index ->
                            Icon(
                                imageVector = if (index < selectedRating) Icons.Filled.Star else Icons.Outlined.StarOutline,
                                contentDescription = "Rate ${index + 1}",
                                tint = if (index < selectedRating) GoldStar else Color.LightGray,
                                modifier = Modifier
                                    .size(44.dp)
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
                    enabled = selectedRating > 0,
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Submit", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showRatingDialog = false },
                    colors = ButtonDefaults.textButtonColors(contentColor = MutedText)
                ) {
                    Text("Cancel")
                }
            },
            shape = RoundedCornerShape(16.dp)
        )
    }

    // --- ROOT BOX FOR BACKGROUND ---
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // 1. BACKGROUND IMAGE
        Image(
            painter = painterResource(id = R.drawable.bg),
            contentDescription = "Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // 2. SCAFFOLD WITH TRANSPARENT BACKGROUND
        Scaffold(
            containerColor = Color.Transparent, // Changed to Transparent
            // Custom Top Bar
            topBar = {
                Box(
                    modifier = Modifier
                        .statusBarsPadding()
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    // Back Button
                    SmallFloatingButton(
                        onClick = onBackClick,
                        icon = Icons.AutoMirrored.Filled.ArrowBack,
                        modifier = Modifier.align(Alignment.CenterStart)
                    )

                    // Report Button
                    SmallFloatingButton(
                        onClick = onReportClick,
                        icon = Icons.Default.Warning,
                        tint = Color(0xFFE53935), // Red
                        modifier = Modifier.align(Alignment.CenterEnd)
                    )
                }
            },
            bottomBar = {
                // --- Modern Action Bar ---
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(16.dp),
                    color = Color.White,
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 20.dp, vertical = 20.dp)
                            .navigationBarsPadding(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Chat Button (Icon Only)
                        OutlinedIconButton(
                            onClick = onChatClick,
                            modifier = Modifier.size(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.5.dp, PrimaryGreen.copy(alpha = 0.5f))
                        ) {
                            Icon(Icons.Default.Chat, null, tint = PrimaryGreen)
                        }

                        // Add to Cart
                        Button(
                            onClick = {
                                if (product.stock > 0) {
                                    val cartItem = CartItem.fromProduct(product, currentUserId)
                                    cartViewModel.addToCart(cartItem) { success ->
                                        if (success) {
                                            showCartAddedMessage = true
                                            Toast.makeText(context, "Added to cart", Toast.LENGTH_SHORT).show()
                                        } else {
                                            Toast.makeText(context, "Failed to add", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                } else {
                                    Toast.makeText(context, "Out of stock", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFE8F5E9),
                                contentColor = PrimaryGreen
                            ),
                            elevation = ButtonDefaults.buttonElevation(0.dp)
                        ) {
                            Icon(Icons.Default.AddShoppingCart, null, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Cart", fontWeight = FontWeight.Bold)
                        }

                        // Buy Now
                        Button(
                            onClick = {
                                if (product.stock > 0) {
                                    val intent = Intent(context, CheckoutActivity::class.java).apply {
                                        putExtra("productId", product.productId)
                                        putExtra("name", product.name)
                                        putExtra("price", product.price)
                                        putExtra("image", product.image)
                                        putExtra("product_id", product.productId)
                                        putExtra("product_name", product.name)
                                        putExtra("product_price", product.price)
                                    }
                                    context.startActivity(intent)
                                } else {
                                    Toast.makeText(context, "Out of stock", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PrimaryGreen,
                                contentColor = Color.White
                            ),
                            elevation = ButtonDefaults.buttonElevation(4.dp)
                        ) {
                            Icon(Icons.Default.ShoppingBag, null, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Buy Now", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            },
            snackbarHost = {
                if (showCartAddedMessage) {
                    Snackbar(
                        modifier = Modifier.padding(16.dp).clickable { onNavigateToCart() },
                        containerColor = PrimaryGreen,
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
            // Main Scrollable Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    // Removed solid .background(AppBackground) so image shows
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
            ) {

                // --- 1. Immersive Image Header ---
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(400.dp)
                ) {
                    // Background
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                            .background(Color.White)
                    ) {
                        AsyncImage(
                            model = product.image,
                            contentDescription = product.name,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(bottom = 20.dp), // Lift image slightly
                            contentScale = ContentScale.Fit,
                            error = painterResource(R.drawable.img_1)
                        )
                    }
                }

                // --- 2. Content Body ---
                Column(modifier = Modifier.padding(horizontal = 20.dp)) {

                    // Floating Stats Card (Overlapping)
                    Card(
                        modifier = Modifier
                            .offset(y = (-40).dp) // Pull up to overlap image
                            .fillMaxWidth()
                            .shadow(8.dp, RoundedCornerShape(16.dp)),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Rating Side
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Star, null, tint = GoldStar, modifier = Modifier.size(24.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = String.format("%.1f", averageRating),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = DarkText
                                )
                                Text(
                                    text = " (${product.ratingCount})",
                                    color = MutedText,
                                    fontSize = 14.sp
                                )
                            }

                            // Vertical Divider
                            VerticalDivider(modifier = Modifier.height(24.dp))

                            // Stock Status Side
                            Text(
                                text = when {
                                    product.stock == 0 -> "Out of Stock"
                                    product.stock < 5 -> "Only ${product.stock} left!"
                                    else -> "In Stock"
                                },
                                color = when {
                                    product.stock == 0 -> Color(0xFFE53935)
                                    product.stock < 5 -> Color(0xFFFB8C00)
                                    else -> PrimaryGreen
                                },
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }

                    // Title & Price (Adjusted offset due to card above)
                    Column(modifier = Modifier.offset(y = (-20).dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Text(
                                text = product.name,
                                fontSize = 26.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = DarkText,
                                modifier = Modifier.weight(1f),
                                lineHeight = 32.sp
                            )
                            Text(
                                text = "Rs. ${product.price.toInt()}",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryGreen
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Description
                        Text(
                            "Description",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkText
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = product.description.ifEmpty { "No description available for this product." },
                            fontSize = 15.sp,
                            color = MutedText,
                            lineHeight = 24.sp
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Seller Section
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showSellerDetails = true },
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(2.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .background(PrimaryGreen.copy(alpha = 0.1f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Store, null, tint = PrimaryGreen)
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        "Sold by",
                                        fontSize = 12.sp,
                                        color = MutedText
                                    )
                                    Text(
                                        "View Seller Profile",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = DarkText
                                    )
                                }
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = null,
                                    tint = MutedText
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Rate Button
                        if (!hasRated) {
                            OutlinedButton(
                                onClick = { showRatingDialog = true },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = DarkText)
                            ) {
                                Icon(Icons.Default.Star, null, tint = GoldStar, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Rate this Product")
                            }
                        }

                        // Spacer for bottom content
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }

        // --- Overlays ---
        if (showSellerDetails) {
            SellerDetailsScreen(
                sellerId = product.sellerId,
                onBackClick = { showSellerDetails = false },
                onContactClick = {
                    showSellerDetails = false
                    onChatClick()
                }
            )
        }
    }
}

// --- Helper Components ---

@Composable
fun SmallFloatingButton(
    onClick: () -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier,
    tint: Color = Color.Black
) {
    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = Color.White,
        shadowElevation = 4.dp,
        modifier = modifier.size(40.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
fun VerticalDivider(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .width(1.dp)
            .background(Color.Gray.copy(alpha = 0.2f))
    )
}
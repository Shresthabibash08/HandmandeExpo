package com.example.handmadeexpo.view

import android.app.Activity
import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
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
import com.example.handmadeexpo.model.OrderItem
import com.example.handmadeexpo.ui.theme.MainColor
import com.example.handmadeexpo.viewmodel.CartViewModel

@Composable
fun CartScreen(cartViewModel: CartViewModel, currentUserId: String) {

    var showDeleteDialog by remember { mutableStateOf(false) }
    var itemToDelete by remember { mutableStateOf<CartItem?>(null) }

    val context = LocalContext.current
    val activity = context as? Activity

    val isLoading = cartViewModel.isLoading.value

    LaunchedEffect(currentUserId) {
        cartViewModel.loadCart(currentUserId)
    }

    val cartItems = cartViewModel.cartItems
    val total = cartItems.sumOf { it.price * it.quantity }

    // DELETE DIALOG
    if (showDeleteDialog && itemToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Remove from Cart") },
            text = { Text("Do you want to remove ${itemToDelete?.name}?") },
            confirmButton = {
                TextButton(onClick = {
                    itemToDelete?.let {
                        cartViewModel.removeFromCart(currentUserId, it.productId) { }
                    }
                    showDeleteDialog = false
                    itemToDelete = null
                }) { Text("Remove", color = Color.Red, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Cancel") }
            }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Background Image
        Image(
            painter = painterResource(id = R.drawable.bg7),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )

        Scaffold(
            containerColor = Color.Transparent,
            bottomBar = {
                if (!isLoading && cartItems.isNotEmpty()) {
                    CartBottomBar(
                        total = total,
                        onCheckoutClick = {
                            // Convert CartItems to OrderItems
                            val orderItems = ArrayList(cartItems.map { cartItem ->
                                OrderItem(
                                    productId = cartItem.productId,
                                    productName = cartItem.name,
                                    price = cartItem.price,
                                    quantity = cartItem.quantity,
                                    imageUrl = cartItem.image
                                )
                            })

                            // Create intent to CheckoutActivity
                            val intent = Intent(context, CheckoutActivity::class.java)
                            intent.putExtra("cartItems", orderItems)
                            intent.putExtra("isFromCart", true) // NEW: Flag to indicate from cart

                            // Start CheckoutActivity
                            context.startActivity(intent)
                        }
                    )
                }
            }
        ) { paddingValues ->

            if (isLoading) {
                // LOADING SCREEN
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MainColor)
                }
            } else {
                // ACTUAL CONTENT
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp)
                ) {
                    item {
                        Text(
                            text = "My Cart",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = MainColor,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }

                    if (cartItems.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillParentMaxHeight(0.7f)
                                    .fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        "Your cart is empty",
                                        color = Color.Gray,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        "Add items to get started",
                                        color = Color.Gray,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }
                    } else {
                        items(cartItems) { item ->
                            CartItemCard(
                                item = item,
                                cartViewModel = cartViewModel,
                                userId = currentUserId,
                                onDeleteClick = {
                                    itemToDelete = item
                                    showDeleteDialog = true
                                }
                            )
                        }

                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CartItemCard(
    item: CartItem,
    cartViewModel: CartViewModel,
    userId: String,
    onDeleteClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = item.image,
                contentDescription = item.name,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop,
                error = painterResource(R.drawable.img_1)
            )

            Column(modifier = Modifier.weight(1f).padding(horizontal = 12.dp)) {
                Text(item.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, maxLines = 1)
                Text("Rs ${item.price}", color = MainColor, fontWeight = FontWeight.SemiBold)

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFF0F0F0)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = {
                                    if (item.quantity > 1) {
                                        cartViewModel.updateQuantity(userId, item.productId, item.quantity - 1) {}
                                    }
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.Remove, contentDescription = null, modifier = Modifier.size(16.dp))
                            }

                            Text("${item.quantity}", fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp))

                            IconButton(
                                onClick = {
                                    cartViewModel.updateQuantity(userId, item.productId, item.quantity + 1) {}
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }

            IconButton(
                onClick = onDeleteClick,
                colors = IconButtonDefaults.iconButtonColors(contentColor = Color(0xFFE57373))
            ) {
                Icon(Icons.Default.Delete, contentDescription = "Remove Item")
            }
        }
    }
}

@Composable
fun CartBottomBar(total: Double, onCheckoutClick: () -> Unit) {
    Surface(shadowElevation = 12.dp, color = Color.White) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("Total Amount", fontSize = 14.sp, color = Color.Gray)
                Text(
                    text = "Rs ${"%.2f".format(total)}",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MainColor
                )
            }

            Button(
                onClick = onCheckoutClick,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(MainColor),
                modifier = Modifier.height(50.dp).width(150.dp)
            ) {
                Text("Checkout", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}
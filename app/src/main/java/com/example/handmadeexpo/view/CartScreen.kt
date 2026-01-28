
package com.example.handmadeexpo.view

import android.app.Activity
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.handmadeexpo.R
import com.example.handmadeexpo.model.BargainModel
import com.example.handmadeexpo.model.CartItem
import com.example.handmadeexpo.model.OrderItem
import com.example.handmadeexpo.ui.theme.MainColor
import com.example.handmadeexpo.viewmodel.BargainViewModel
import com.example.handmadeexpo.viewmodel.CartViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

@Composable
fun CartScreen(
    cartViewModel: CartViewModel,
    currentUserId: String,
    bargainViewModel: BargainViewModel = viewModel()
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    var itemToDelete by remember { mutableStateOf<CartItem?>(null) }

    val acceptedPrices = remember { mutableStateMapOf<String, Double>() }

    val context = LocalContext.current
    val activity = context as? Activity
    val isLoading = cartViewModel.isLoading.value
    var currentUserName by remember { mutableStateOf("Customer") }

    LaunchedEffect(currentUserId) {
        cartViewModel.loadCart(currentUserId)
        FirebaseDatabase.getInstance().getReference("Buyers").child(currentUserId).child("name")
            .get().addOnSuccessListener { currentUserName = it.value?.toString() ?: "Customer" }
    }

    val cartItems = cartViewModel.cartItems

    val total = cartItems.sumOf { item ->
        val priceToUse = acceptedPrices[item.productId] ?: item.price.toDouble()
        priceToUse * item.quantity.toDouble()
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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(MainColor.copy(alpha = 0.15f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.ShoppingCart,
                            contentDescription = null,
                            tint = MainColor,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            "My Cart",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF212121)
                        )
                        Text(
                            "${cartItems.size} items",
                            fontSize = 13.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Content
        Box(modifier = Modifier.weight(1f)) {
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(
                            color = MainColor,
                            strokeWidth = 3.dp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Loading cart...", color = Color.Gray, fontSize = 14.sp)
                    }
                }
            } else if (cartItems.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.ShoppingCartCheckout,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = Color.Gray.copy(alpha = 0.3f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "Your cart is empty",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Gray
                        )
                        Text(
                            "Add items to get started",
                            fontSize = 14.sp,
                            color = Color.Gray.copy(alpha = 0.7f)
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(cartItems, key = { it.productId }) { item ->
                        ModernCartItemCard(
                            item = item,
                            cartViewModel = cartViewModel,
                            bargainViewModel = bargainViewModel,
                            userId = currentUserId,
                            userName = currentUserName,
                            onPriceUpdate = { newPrice -> acceptedPrices[item.productId] = newPrice },
                            onDeleteClick = {
                                itemToDelete = item
                                showDeleteDialog = true
                            }
                        )
                    }
                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }
        }

        // Bottom Bar
        if (!isLoading && cartItems.isNotEmpty()) {
            ModernCartBottomBar(
                total = total,
                itemCount = cartItems.size,
                onCheckoutClick = {
                    val orderItems = ArrayList(cartItems.map { cartItem ->
                        val finalPrice = acceptedPrices[cartItem.productId] ?: cartItem.price.toDouble()
                        OrderItem(
                            productId = cartItem.productId,
                            productName = cartItem.name,
                            price = finalPrice,
                            quantity = cartItem.quantity,
                            imageUrl = cartItem.image
                        )
                    })

                    val intent = Intent(context, CheckoutActivity::class.java).apply {
                        putExtra("cartItems", orderItems)
                        putExtra("isFromCart", true)
                    }
                    context.startActivity(intent)
                }
            )
        }
    }

    // Modern Delete Dialog
    if (showDeleteDialog && itemToDelete != null) {
        ModernDeleteDialog(
            itemName = itemToDelete?.name ?: "",
            onConfirm = {
                itemToDelete?.let {
                    cartViewModel.removeFromCart(currentUserId, it.productId) { }
                }
                showDeleteDialog = false
                itemToDelete = null
            },
            onDismiss = {
                showDeleteDialog = false
                itemToDelete = null
            }
        )
    }
}

@Composable
fun ModernCartItemCard(
    item: CartItem,
    cartViewModel: CartViewModel,
    bargainViewModel: BargainViewModel,
    userId: String,
    userName: String,
    onPriceUpdate: (Double) -> Unit,
    onDeleteClick: () -> Unit
) {
    var showBargainDialog by remember { mutableStateOf(false) }
    var bargainStatus by remember { mutableStateOf("None") }
    var currentDisplayPrice by remember { mutableStateOf(item.price.toDouble()) }
    var sellerCounterPrice by remember { mutableStateOf("") }

    LaunchedEffect(item.productId) {
        FirebaseDatabase.getInstance().reference.child("Bargains").child(userId).child(item.productId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val status = snapshot.child("status").value.toString()
                        bargainStatus = status
                        when (status) {
                            "Accepted" -> {
                                val price = snapshot.child("offeredPrice").value.toString().toDoubleOrNull() ?: item.price.toDouble()
                                currentDisplayPrice = price
                                onPriceUpdate(price)
                            }
                            "Counter" -> {
                                sellerCounterPrice = snapshot.child("counterPrice").value.toString()
                                currentDisplayPrice = item.price.toDouble()
                                onPriceUpdate(item.price.toDouble())
                            }
                            else -> {
                                currentDisplayPrice = item.price.toDouble()
                                onPriceUpdate(item.price.toDouble())
                            }
                        }
                    } else {
                        bargainStatus = "None"
                        currentDisplayPrice = item.price.toDouble()
                        onPriceUpdate(item.price.toDouble())
                    }
                }
                override fun onCancelled(error: DatabaseError) {}
            })
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Product Image
                Card(
                    modifier = Modifier
                        .size(80.dp)
                        .shadow(1.dp, RoundedCornerShape(12.dp)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    AsyncImage(
                        model = item.image,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        error = painterResource(R.drawable.img_1)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Product Details
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color(0xFF212121),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "NRP ${currentDisplayPrice.toInt()}",
                        color = if (bargainStatus == "Accepted") Color(0xFF4CAF50) else MainColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    if (bargainStatus == "Accepted") {
                        Spacer(modifier = Modifier.height(4.dp))
                        Surface(
                            color = Color(0xFF4CAF50).copy(alpha = 0.15f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                "Price Agreed",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF2E7D32)
                            )
                        }
                    }
                }

                // Delete Button
                IconButton(
                    onClick = onDeleteClick,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF44336).copy(alpha = 0.1f))
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Color(0xFFF44336),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Counter Offer Section
            if (bargainStatus == "Counter") {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    color = Color(0xFFFFF3E0),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.LocalOffer,
                                contentDescription = null,
                                tint = Color(0xFFFF9800),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Seller's Counter Offer",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFE65100)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "NRP $sellerCounterPrice",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFF9800)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = { showBargainDialog = true },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFF757575)
                        )
                    ) {
                        Icon(Icons.Default.Gavel, null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Counter", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    }

                    Button(
                        onClick = {
                            bargainViewModel.updateStatus(userId, item.productId, "Accepted", sellerCounterPrice, "Buyer")
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.CheckCircle, null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Accept", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            } else {
                Spacer(modifier = Modifier.height(12.dp))
                Divider(color = Color(0xFFEEEEEE))
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Quantity Controls
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFF5F5F5)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(4.dp)
                        ) {
                            IconButton(
                                modifier = Modifier.size(32.dp),
                                onClick = {
                                    if (item.quantity > 1) {
                                        cartViewModel.updateQuantity(userId, item.productId, item.quantity - 1) {}
                                    }
                                }
                            ) {
                                Icon(
                                    Icons.Default.Remove,
                                    contentDescription = "Decrease",
                                    modifier = Modifier.size(16.dp),
                                    tint = Color(0xFF424242)
                                )
                            }

                            Text(
                                "${item.quantity}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )

                            IconButton(
                                modifier = Modifier.size(32.dp),
                                onClick = {
                                    cartViewModel.updateQuantity(userId, item.productId, item.quantity + 1) {}
                                }
                            ) {
                                Icon(
                                    Icons.Default.Add,
                                    contentDescription = "Increase",
                                    modifier = Modifier.size(16.dp),
                                    tint = Color(0xFF424242)
                                )
                            }
                        }
                    }

                    Spacer(Modifier.weight(1f))

                    // Bargain Status/Button
                    when (bargainStatus) {
                        "Pending" -> {
                            Surface(
                                color = Color(0xFFFFF3E0),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.HourglassEmpty,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                        tint = Color(0xFFFF9800)
                                    )
                                    Spacer(Modifier.width(6.dp))
                                    Text(
                                        "Pending",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFFE65100)
                                    )
                                }
                            }
                        }
                        "Accepted" -> {
                            // Already shown as badge above
                        }
                        else -> {
                            Button(
                                onClick = { showBargainDialog = true },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MainColor.copy(alpha = 0.1f),
                                    contentColor = MainColor
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(
                                    Icons.Default.Gavel,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(Modifier.width(6.dp))
                                Text("Make Offer", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }

    // Bargain Dialog
    if (showBargainDialog) {
        var offerAmount by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showBargainDialog = false },
            icon = {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(MainColor.copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Gavel,
                        contentDescription = null,
                        tint = MainColor,
                        modifier = Modifier.size(28.dp)
                    )
                }
            },
            title = {
                Text(
                    if (bargainStatus == "Counter") "New Counter Offer" else "Make an Offer",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color(0xFF212121)
                )
            },
            text = {
                Column {
                    Text(
                        "Original Price: NRP ${item.price.toInt()}",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = offerAmount,
                        onValueChange = { if (it.all { c -> c.isDigit() }) offerAmount = it },
                        label = { Text("Your Offer (NRP)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (offerAmount.isNotEmpty()) {
                            val bargain = BargainModel(
                                productId = item.productId,
                                buyerId = userId,
                                buyerName = userName,
                                sellerId = item.sellerId,
                                productName = item.name,
                                originalPrice = item.price.toString(),
                                offeredPrice = offerAmount,
                                status = "Pending"
                            )
                            bargainViewModel.requestBargain(bargain)
                            showBargainDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MainColor),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Send Offer", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showBargainDialog = false },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFF757575)
                    )
                ) {
                    Text("Cancel", fontWeight = FontWeight.Medium)
                }
            },
            shape = RoundedCornerShape(20.dp),
            containerColor = Color.White
        )
    }
}

@Composable
fun ModernCartBottomBar(total: Double, itemCount: Int, onCheckoutClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 12.dp,
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Summary Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "Total Amount",
                        color = Color.Gray,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "NRP ${total.toInt()}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        color = MainColor
                    )
                    Text(
                        "$itemCount ${if (itemCount == 1) "item" else "items"}",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

                Button(
                    onClick = onCheckoutClick,
                    colors = ButtonDefaults.buttonColors(containerColor = MainColor),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .height(56.dp)
                        .width(140.dp)
                ) {
                    Icon(
                        Icons.Default.ShoppingCartCheckout,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        "Checkout",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun ModernDeleteDialog(
    itemName: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(Color(0xFFF44336).copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = null,
                    tint = Color(0xFFF44336),
                    modifier = Modifier.size(28.dp)
                )
            }
        },
        title = {
            Text(
                "Remove from Cart",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Color(0xFF212121)
            )
        },
        text = {
            Text(
                "Do you want to remove \"$itemName\" from your cart?",
                fontSize = 14.sp,
                color = Color(0xFF616161)
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFF44336)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Remove", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color(0xFF757575)
                )
            ) {
                Text("Cancel", fontWeight = FontWeight.Medium)
            }
        },
        shape = RoundedCornerShape(20.dp),
        containerColor = Color.White
    )
}
package com.example.handmadeexpo.view

import android.app.Activity
import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import androidx.compose.ui.text.input.KeyboardType
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

    // Map to track agreed-upon prices for each product (from nirajan branch)
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

    // Total calculated based on negotiated prices (from nirajan branch)
    val total = cartItems.sumOf { item ->
        val priceToUse = acceptedPrices[item.productId] ?: item.price.toDouble()
        priceToUse * item.quantity.toDouble()
    }

    Box(modifier = Modifier.fillMaxSize()) {
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
        ) { paddingValues ->
            if (isLoading) {
                Box(Modifier.fillMaxSize().padding(paddingValues), Alignment.Center) {
                    CircularProgressIndicator(color = MainColor)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = 16.dp)
                ) {
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("My Cart", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = MainColor)
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    if (cartItems.isEmpty()) {
                        item {
                            Box(Modifier.fillParentMaxHeight(0.7f).fillMaxWidth(), Alignment.Center) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Your cart is empty", color = Color.Gray, fontSize = 18.sp, fontWeight = FontWeight.Medium)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("Add items to get started", color = Color.Gray, fontSize = 14.sp)
                                }
                            }
                        }
                    } else {
                        items(cartItems, key = { it.productId }) { item ->
                            CartItemCard(
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
                        item { Spacer(modifier = Modifier.height(16.dp)) }
                    }
                }
            }
        }
    }

    // Consolidated Delete Dialog
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
}

@Composable
fun CartItemCard(
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
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = item.image,
                    contentDescription = null,
                    modifier = Modifier.size(80.dp).clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop,
                    error = painterResource(R.drawable.img_1)
                )

                Column(modifier = Modifier.weight(1f).padding(horizontal = 12.dp)) {
                    Text(item.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, maxLines = 1)
                    Text(
                        text = "NRP ${currentDisplayPrice.toInt()}",
                        color = if (bargainStatus == "Accepted") Color(0xFF4CAF50) else MainColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    if (bargainStatus == "Counter") {
                        Text("Seller's Offer: NRP $sellerCounterPrice", color = Color(0xFFFF9800), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
                IconButton(onClick = onDeleteClick) { Icon(Icons.Default.Delete, null, tint = Color.Red) }
            }

            if (bargainStatus == "Counter") {
                Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = {
                            bargainViewModel.updateStatus(userId, item.productId, "Accepted", sellerCounterPrice, "Buyer")
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                        shape = RoundedCornerShape(8.dp)
                    ) { Text("Accept NRP $sellerCounterPrice", fontSize = 11.sp) }

                    OutlinedButton(
                        onClick = { showBargainDialog = true },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) { Text("Counter Again", fontSize = 11.sp) }
                }
            } else {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
                    Surface(shape = RoundedCornerShape(8.dp), color = Color(0xFFF0F0F0)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(modifier = Modifier.size(32.dp), onClick = {
                                if (item.quantity > 1) cartViewModel.updateQuantity(userId, item.productId, item.quantity - 1) {}
                            }) { Icon(Icons.Default.Remove, null, modifier = Modifier.size(16.dp)) }

                            Text("${item.quantity}", fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp))

                            IconButton(modifier = Modifier.size(32.dp), onClick = {
                                cartViewModel.updateQuantity(userId, item.productId, item.quantity + 1) {}
                            }) { Icon(Icons.Default.Add, null, modifier = Modifier.size(16.dp)) }
                        }
                    }

                    Spacer(Modifier.weight(1f))

                    when (bargainStatus) {
                        "Pending" -> Text("Waiting for Seller...", color = Color.Gray, fontSize = 12.sp)
                        "Accepted" -> Icon(Icons.Default.CheckCircle, null, tint = Color(0xFF4CAF50))
                        else -> {
                            IconButton(onClick = { showBargainDialog = true }) {
                                Icon(Icons.Default.Gavel, null, tint = MainColor)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showBargainDialog) {
        var offerAmount by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showBargainDialog = false },
            title = { Text(if(bargainStatus == "Counter") "New Counter Offer" else "Enter Your Offer") },
            text = {
                OutlinedTextField(
                    value = offerAmount,
                    onValueChange = { if (it.all { c -> c.isDigit() } ) offerAmount = it },
                    label = { Text("NRP Amount") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            },
            confirmButton = {
                Button(onClick = {
                    if (offerAmount.isNotEmpty()) {
                        val bargain = BargainModel(
                            productId = item.productId, buyerId = userId, buyerName = userName,
                            sellerId = item.sellerId, productName = item.name, originalPrice = item.price.toString(),
                            offeredPrice = offerAmount, status = "Pending"
                        )
                        bargainViewModel.requestBargain(bargain)
                        showBargainDialog = false
                    }
                }) { Text("Send Offer") }
            },
            dismissButton = {
                TextButton(onClick = { showBargainDialog = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
fun CartBottomBar(total: Double, onCheckoutClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 12.dp, 
        color = Color.White
    ) {
        Row(
            modifier = Modifier.padding(20.dp).navigationBarsPadding(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("Total Amount", color = Color.Gray, fontSize = 14.sp)
                Text(
                    text = "NRP ${total.toInt()}", 
                    fontWeight = FontWeight.Bold, 
                    fontSize = 22.sp, 
                    color = MainColor
                )
            }
            Button(
                onClick = onCheckoutClick,
                colors = ButtonDefaults.buttonColors(containerColor = MainColor),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.height(50.dp).width(150.dp)
            ) {
                Text("Checkout", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
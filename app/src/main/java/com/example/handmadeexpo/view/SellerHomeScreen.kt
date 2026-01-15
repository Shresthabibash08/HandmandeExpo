package com.example.handmadeexpo.view

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
// Corrected import for AutoMirrored icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.handmadeexpo.model.BargainModel
import com.example.handmadeexpo.model.ProductModel
import com.example.handmadeexpo.repo.ProductRepoImpl
import com.example.handmadeexpo.ui.theme.Orange
import com.example.handmadeexpo.viewmodel.BargainViewModel
import com.example.handmadeexpo.viewmodel.ProductViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellerHomeScreen(
    sellerId: String,
    sellerName: String, // Ensure you pass the seller's name from your navigation/activity
    bargainViewModel: BargainViewModel = viewModel()
) {
    val productViewModel: ProductViewModel = remember {
        ProductViewModel(ProductRepoImpl())
    }

    var showBargainSection by remember { mutableStateOf(false) }

    LaunchedEffect(sellerId) {
        productViewModel.getProductsBySeller(sellerId)
        bargainViewModel.fetchBargainsForSeller(sellerId)
    }

    val sellerProducts by productViewModel.sellerProducts.observeAsState(emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (showBargainSection) "Customer Bargains" else "Seller Dashboard",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    if (showBargainSection) {
                        IconButton(onClick = { showBargainSection = false }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                }
            )
        },
        containerColor = Color(0xFFF5F5F5)
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            if (!showBargainSection) {
                // --- INVENTORY SECTION ---
                item {
                    BargainShortcutCard(
                        count = bargainViewModel.sellerBargains.size,
                        onClick = { showBargainSection = true }
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Text("Your Inventory", fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))
                }

                items(sellerProducts) { product ->
                    SellerProductCard(product)
                }
            } else {
                // --- BARGAIN LIST SECTION ---
                if (bargainViewModel.sellerBargains.isEmpty()) {
                    item {
                        Box(Modifier.fillParentMaxHeight(0.8f).fillMaxWidth(), Alignment.Center) {
                            Text("No pending bargain offers", color = Color.Gray)
                        }
                    }
                } else {
                    items(bargainViewModel.sellerBargains) { bargain ->
                        BargainRequestCard(
                            bargain = bargain,
                            onAccept = {
                                bargainViewModel.updateStatus(
                                    bargain.buyerId, bargain.productId, "Accepted", "", sellerName
                                )
                            },
                            onReject = {
                                bargainViewModel.updateStatus(
                                    bargain.buyerId, bargain.productId, "Rejected", "", sellerName
                                )
                            },
                            onCounter = { counterPrice ->
                                bargainViewModel.updateStatus(
                                    bargain.buyerId, bargain.productId, "Counter", counterPrice, sellerName
                                )
                            }
                        )
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

@Composable
fun BargainRequestCard(
    bargain: BargainModel,
    onAccept: () -> Unit,
    onReject: () -> Unit,
    onCounter: (String) -> Unit
) {
    var showCounterDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Product: ${bargain.productName}", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text("Buyer: ${bargain.buyerName}", fontSize = 14.sp, color = Color.Gray)

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), thickness = 0.5.dp)

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text("Original: NRP ${bargain.originalPrice}", style = androidx.compose.ui.text.TextStyle(textDecoration = TextDecoration.LineThrough), fontSize = 12.sp, color = Color.Gray)
                    Text("Offer: NRP ${bargain.offeredPrice}", color = Orange, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
                }

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(onClick = onReject) {
                        Icon(Icons.Default.Close, contentDescription = "Reject", tint = Color.Red)
                    }
                    IconButton(onClick = { showCounterDialog = true }) {
                        Icon(Icons.Default.Gavel, contentDescription = "Counter", tint = Color(0xFFFF9800))
                    }
                    Button(
                        onClick = onAccept,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Accept")
                    }
                }
            }
        }
    }

    if (showCounterDialog) {
        var counterAmount by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showCounterDialog = false },
            title = { Text("Make Counter Offer") },
            text = {
                OutlinedTextField(
                    value = counterAmount,
                    onValueChange = { if (it.all { char -> char.isDigit() }) counterAmount = it },
                    label = { Text("Counter Price (NRP)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            },
            confirmButton = {
                Button(onClick = {
                    onCounter(counterAmount)
                    showCounterDialog = false
                }) { Text("Send") }
            },
            dismissButton = {
                TextButton(onClick = { showCounterDialog = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
fun SellerProductCard(product: ProductModel) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = product.image,
                contentDescription = null,
                modifier = Modifier.size(70.dp).clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(product.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("NRP ${product.price}", color = Orange, fontWeight = FontWeight.SemiBold)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("Sold: ${product.sold}", fontSize = 12.sp)
                Text(
                    "Stock: ${product.stock}",
                    fontSize = 12.sp,
                    color = if (product.stock < 10) Color.Red else Color(0xFF4CAF50)
                )
            }
        }
    }
}

@Composable
fun BargainShortcutCard(count: Int, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().height(80.dp).clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(45.dp).background(Orange.copy(alpha = 0.1f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) { Icon(Icons.Default.Gavel, contentDescription = null, tint = Orange) }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("Bargain Offers", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("$count requests pending", color = Color.Gray, fontSize = 12.sp)
            }
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(20.dp), tint = Color.LightGray)
        }
    }
}
package com.example.handmadeexpo.view

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.ui.draw.rotate

import androidx.compose.ui.draw.clip
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Star
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
    sellerName: String,
    bargainViewModel: BargainViewModel = viewModel()
) {
    val productViewModel: ProductViewModel = remember {
        ProductViewModel(ProductRepoImpl())
    }

    // State for toggling between Dashboard and Bargain List
    var showBargainSection by remember { mutableStateOf(false) }
    // State for Tabs (Verified vs Pending/Rejected)
    var selectedTab by remember { mutableIntStateOf(0) }

    LaunchedEffect(sellerId) {
        productViewModel.getProductsBySeller(sellerId)
        bargainViewModel.fetchBargainsForSeller(sellerId)
    }

    val sellerProducts by productViewModel.sellerProducts.observeAsState(emptyList())

    // Filter Logic
    val verifiedProducts = sellerProducts.filter { it.verificationStatus == "Verified" }
    val pendingProducts = sellerProducts.filter { it.verificationStatus == "Pending" }
    val rejectedProducts = sellerProducts.filter { it.verificationStatus == "Rejected" }
    val nonVerifiedProducts = pendingProducts + rejectedProducts

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
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {

            // Only show Tabs if we are in the Dashboard (not Bargain section)
            if (!showBargainSection) {
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color(0xFFF5F5F5)
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Verified")
                                if (verifiedProducts.isNotEmpty()) {
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Badge { Text(verifiedProducts.size.toString()) }
                                }
                            }
                        }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Pending/Rejected")
                                if (nonVerifiedProducts.isNotEmpty()) {
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Badge(containerColor = Color(0xFFFF9800)) {
                                        Text(nonVerifiedProducts.size.toString())
                                    }
                                }
                            }
                        }
                    )
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                if (showBargainSection) {
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
                                    bargainViewModel.updateStatus(bargain.buyerId, bargain.productId, "Accepted", "", sellerName)
                                },
                                onReject = {
                                    bargainViewModel.updateStatus(bargain.buyerId, bargain.productId, "Rejected", "", sellerName)
                                },
                                onCounter = { counterPrice ->
                                    bargainViewModel.updateStatus(bargain.buyerId, bargain.productId, "Counter", counterPrice, sellerName)
                                }
                            )
                        }
                    }
                } else {
                    // --- DASHBOARD / INVENTORY SECTION ---
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        BargainShortcutCard(
                            count = bargainViewModel.sellerBargains.size,
                            onClick = { showBargainSection = true }
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                    }

                    when (selectedTab) {
                        0 -> { // Verified Tab
                            if (verifiedProducts.isEmpty()) {
                                item {
                                    EmptyState(
                                        icon = Icons.Default.CheckCircle,
                                        message = "No verified products yet",
                                        subMessage = "Products will appear here once admin approves them"
                                    )
                                }
                            } else {
                                items(verifiedProducts) { product ->
                                    SellerProductCard(product)
                                }
                            }
                        }
                        1 -> { // Pending/Rejected Tab
                            if (nonVerifiedProducts.isEmpty()) {
                                item {
                                    EmptyState(
                                        icon = Icons.Default.HourglassEmpty,
                                        message = "No pending or rejected products",
                                        subMessage = "All your products are verified!"
                                    )
                                }
                            } else {
                                if (pendingProducts.isNotEmpty()) {
                                    item {
                                        Text("Pending Verification (${pendingProducts.size})", fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
                                    }
                                    items(pendingProducts) { product -> SellerProductCard(product) }
                                }
                                if (rejectedProducts.isNotEmpty()) {
                                    item {
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Text("Rejected (${rejectedProducts.size})", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Red, modifier = Modifier.padding(bottom = 8.dp))
                                    }
                                    items(rejectedProducts) { product -> SellerProductCard(product) }
                                }
                            }
                        }
                    }
                }
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
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
fun EmptyState(icon: androidx.compose.ui.graphics.vector.ImageVector, message: String, subMessage: String) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(top = 60.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(80.dp), tint = Color.Gray.copy(alpha = 0.5f))
        Spacer(modifier = Modifier.height(16.dp))
        Text(message, fontSize = 18.sp, fontWeight = FontWeight.Medium, color = Color.Gray)
        Text(subMessage, fontSize = 14.sp, color = Color.Gray.copy(alpha = 0.7f))
    }
}

@Composable
fun SellerProductCard(product: ProductModel) {
    val averageRating = if (product.ratingCount > 0) {
        product.totalRating.toFloat() / product.ratingCount
    } else 0f

    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column {
            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = product.image,
                    contentDescription = product.name,
                    modifier = Modifier.size(70.dp).clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(product.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, maxLines = 1)
                    Text("NRP ${product.price.toInt()}", color = Orange, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        repeat(5) { index ->
                            Icon(
                                imageVector = if (index < averageRating.toInt()) Icons.Filled.Star else Icons.Outlined.Star,
                                contentDescription = null,
                                tint = if (index < averageRating.toInt()) Color(0xFFFFB300) else Color.LightGray,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (product.ratingCount > 0) String.format("%.1f (%d)", averageRating, product.ratingCount) else "No ratings", fontSize = 12.sp, color = Color.Gray)
                    }
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Sold: ${product.sold}", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(if (product.stock == 0) "⚠️" else if (product.stock < 10) "⚡" else "✅", fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(2.dp))
                        Text("${product.stock}", fontSize = 12.sp, color = if (product.stock == 0) Color.Red else if (product.stock < 10) Color(0xFFFF9800) else Color(0xFF4CAF50), fontWeight = FontWeight.Bold)
                    }
                }
            }

            if (product.verificationStatus != "Verified") {
                HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray)
                Row(
                    modifier = Modifier.fillMaxWidth().background(if (product.verificationStatus == "Pending") Color(0xFFFF9800).copy(alpha = 0.1f) else Color(0xFFF44336).copy(alpha = 0.1f)).padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (product.verificationStatus == "Pending") Icons.Default.HourglassEmpty else Icons.Default.Cancel,
                        contentDescription = null, modifier = Modifier.size(16.dp),
                        tint = if (product.verificationStatus == "Pending") Color(0xFFFF9800) else Color(0xFFF44336)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(if (product.verificationStatus == "Pending") "Pending Admin Verification" else "Rejected by Admin", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (product.verificationStatus == "Pending") Color(0xFFFF9800) else Color(0xFFF44336))
                        if (product.verificationStatus == "Rejected" && product.rejectionReason.isNotEmpty()) {
                            Text("Reason: ${product.rejectionReason}", fontSize = 11.sp, color = Color.Gray)
                        }
                    }
                }
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
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(20.dp).rotate(180f), tint = Color.LightGray)
        }
    }
}


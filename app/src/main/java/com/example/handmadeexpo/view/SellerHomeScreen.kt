package com.example.handmadeexpo.view

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.handmadeexpo.R
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

    var showBargainSection by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableIntStateOf(0) }

    LaunchedEffect(sellerId) {
        productViewModel.getProductsBySeller(sellerId)
        bargainViewModel.fetchBargainsForSeller(sellerId)
    }

    val sellerProducts by productViewModel.sellerProducts.observeAsState(emptyList())

    val verifiedProducts = sellerProducts.filter { it.verificationStatus == "Verified" }
    val pendingProducts = sellerProducts.filter { it.verificationStatus == "Pending" }
    val rejectedProducts = sellerProducts.filter { it.verificationStatus == "Rejected" }
    val nonVerifiedProducts = pendingProducts + rejectedProducts

    // --- ROOT BOX FOR BACKGROUND ---
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // 1. BACKGROUND IMAGE
        Image(
            painter = painterResource(id = R.drawable.bg7),
            contentDescription = "Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // 2. MAIN CONTENT
        Column(
            modifier = Modifier
                .fillMaxSize()
            // Removed solid background color so image shows
        ) {
            if (!showBargainSection) {
                // Modern Tabs Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .shadow(2.dp, RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    TabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = Color.Transparent,
                        indicator = { tabPositions ->
                            TabRowDefaults.SecondaryIndicator(
                                modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                                color = Color(0xFF4CAF50),
                                height = 3.dp
                            )
                        }
                    ) {
                        Tab(
                            selected = selectedTab == 0,
                            onClick = { selectedTab = 0 },
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        "Verified",
                                        fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 14.sp
                                    )
                                    if (verifiedProducts.isNotEmpty()) {
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Badge(containerColor = Color(0xFF4CAF50)) {
                                            Text(verifiedProducts.size.toString(), fontSize = 10.sp)
                                        }
                                    }
                                }
                            }
                        )
                        Tab(
                            selected = selectedTab == 1,
                            onClick = { selectedTab = 1 },
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        "Pending/Rejected",
                                        fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 14.sp
                                    )
                                    if (nonVerifiedProducts.isNotEmpty()) {
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Badge(containerColor = Color(0xFFFF9800)) {
                                            Text(nonVerifiedProducts.size.toString(), fontSize = 10.sp)
                                        }
                                    }
                                }
                            }
                        )
                    }
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (showBargainSection) {
                    // BARGAIN SECTION
                    if (bargainViewModel.sellerBargains.isEmpty()) {
                        item {
                            Box(
                                Modifier
                                    .fillParentMaxHeight(0.8f)
                                    .fillMaxWidth(),
                                Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        Icons.Default.Gavel,
                                        contentDescription = null,
                                        modifier = Modifier.size(64.dp),
                                        tint = Color.Gray.copy(alpha = 0.3f)
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        "No pending bargain offers",
                                        fontSize = 16.sp,
                                        color = Color.Gray
                                    )
                                }
                            }
                        }
                    } else {
                        items(bargainViewModel.sellerBargains) { bargain ->
                            ModernBargainRequestCard(
                                bargain = bargain,
                                onAccept = {
                                    bargainViewModel.updateStatus(
                                        bargain.buyerId,
                                        bargain.productId,
                                        "Accepted",
                                        "",
                                        sellerName
                                    )
                                },
                                onReject = {
                                    bargainViewModel.updateStatus(
                                        bargain.buyerId,
                                        bargain.productId,
                                        "Rejected",
                                        "",
                                        sellerName
                                    )
                                },
                                onCounter = { counterPrice ->
                                    bargainViewModel.updateStatus(
                                        bargain.buyerId,
                                        bargain.productId,
                                        "Counter",
                                        counterPrice,
                                        sellerName
                                    )
                                }
                            )
                        }
                    }
                } else {
                    // DASHBOARD SECTION
                    item {
                        ModernBargainShortcutCard(
                            count = bargainViewModel.sellerBargains.size,
                            onClick = { showBargainSection = true }
                        )
                    }

                    when (selectedTab) {
                        0 -> { // Verified Tab
                            if (verifiedProducts.isEmpty()) {
                                item {
                                    ModernEmptyState(
                                        icon = Icons.Default.CheckCircle,
                                        message = "No verified products yet",
                                        subMessage = "Products will appear here once admin approves them"
                                    )
                                }
                            } else {
                                items(verifiedProducts) { product ->
                                    ModernSellerProductCard(product)
                                }
                            }
                        }
                        1 -> { // Pending/Rejected Tab
                            if (nonVerifiedProducts.isEmpty()) {
                                item {
                                    ModernEmptyState(
                                        icon = Icons.Default.HourglassEmpty,
                                        message = "No pending or rejected products",
                                        subMessage = "All your products are verified!"
                                    )
                                }
                            } else {
                                if (pendingProducts.isNotEmpty()) {
                                    item {
                                        Card(
                                            modifier = Modifier.fillMaxWidth(),
                                            colors = CardDefaults.cardColors(
                                                containerColor = Color(0xFFFFF3E0)
                                            ),
                                            shape = RoundedCornerShape(12.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(12.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    Icons.Default.HourglassEmpty,
                                                    contentDescription = null,
                                                    tint = Color(0xFFFF9800),
                                                    modifier = Modifier.size(20.dp)
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(
                                                    "Pending Verification (${pendingProducts.size})",
                                                    fontSize = 14.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFFE65100)
                                                )
                                            }
                                        }
                                    }
                                    items(pendingProducts) { product ->
                                        ModernSellerProductCard(product)
                                    }
                                }
                                if (rejectedProducts.isNotEmpty()) {
                                    item {
                                        Card(
                                            modifier = Modifier.fillMaxWidth(),
                                            colors = CardDefaults.cardColors(
                                                containerColor = Color(0xFFFFEBEE)
                                            ),
                                            shape = RoundedCornerShape(12.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(12.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    Icons.Default.Cancel,
                                                    contentDescription = null,
                                                    tint = Color(0xFFF44336),
                                                    modifier = Modifier.size(20.dp)
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(
                                                    "Rejected (${rejectedProducts.size})",
                                                    fontSize = 14.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFFC62828)
                                                )
                                            }
                                        }
                                    }
                                    items(rejectedProducts) { product ->
                                        ModernSellerProductCard(product)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Back button handling for bargain section
    BackHandler(enabled = showBargainSection) {
        showBargainSection = false
    }
}

@Composable
fun ModernBargainRequestCard(
    bargain: BargainModel,
    onAccept: () -> Unit,
    onReject: () -> Unit,
    onCounter: (String) -> Unit
) {
    var showCounterDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color(0xFFFF9800).copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Gavel,
                        contentDescription = null,
                        tint = Color(0xFFFF9800),
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        bargain.productName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color(0xFF212121),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        "from ${bargain.buyerName}",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = Color(0xFFEEEEEE))
            Spacer(modifier = Modifier.height(16.dp))

            // Price Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "Original Price",
                        fontSize = 11.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        "NRP ${bargain.originalPrice}",
                        style = androidx.compose.ui.text.TextStyle(
                            textDecoration = TextDecoration.LineThrough
                        ),
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Offered Price",
                        fontSize = 11.sp,
                        color = Color(0xFFFF9800),
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "NRP ${bargain.offeredPrice}",
                        color = Color(0xFFFF9800),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 22.sp
                    )
                }

                // Action Buttons
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = onAccept,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4CAF50)
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.width(120.dp)
                    ) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Accept", fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = { showCounterDialog = true },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.width(120.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFFFF9800)
                        )
                    ) {
                        Icon(
                            Icons.Default.Gavel,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Counter", fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = onReject,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.width(120.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFFF44336)
                        )
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Reject", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    if (showCounterDialog) {
        var counterAmount by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showCounterDialog = false },
            icon = {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(Color(0xFFFF9800).copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Gavel,
                        contentDescription = null,
                        tint = Color(0xFFFF9800),
                        modifier = Modifier.size(28.dp)
                    )
                }
            },
            title = {
                Text(
                    "Make Counter Offer",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            },
            text = {
                Column {
                    Text(
                        "Enter your counter price for ${bargain.productName}",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = counterAmount,
                        onValueChange = { if (it.all { char -> char.isDigit() }) counterAmount = it },
                        label = { Text("Counter Price (NRP)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (counterAmount.isNotEmpty()) {
                            onCounter(counterAmount)
                            showCounterDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF9800)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Send Offer", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showCounterDialog = false },
                    shape = RoundedCornerShape(12.dp)
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
fun ModernEmptyState(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    message: String,
    subMessage: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 60.dp, bottom = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = Color.Gray.copy(alpha = 0.3f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            message,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            subMessage,
            fontSize = 14.sp,
            color = Color.Gray.copy(alpha = 0.7f)
        )
    }
}

@Composable
fun ModernSellerProductCard(product: ProductModel) {
    val averageRating = if (product.ratingCount > 0) {
        product.totalRating.toFloat() / product.ratingCount
    } else 0f

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Card(
                    modifier = Modifier.size(80.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    AsyncImage(
                        model = product.image,
                        contentDescription = product.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        product.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = Color(0xFF212121)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "NRP ${product.price.toInt()}",
                        color = Color(0xFF4CAF50),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    // Rating
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        repeat(5) { index ->
                            Icon(
                                imageVector = if (index < averageRating.toInt())
                                    Icons.Filled.Star
                                else
                                    Icons.Outlined.Star,
                                contentDescription = null,
                                tint = if (index < averageRating.toInt())
                                    Color(0xFFFFB300)
                                else
                                    Color.LightGray,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                        if (product.ratingCount > 0) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                String.format("%.1f (%d)", averageRating, product.ratingCount),
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }

                // Stock & Sales Info
                Column(horizontalAlignment = Alignment.End) {
                    Surface(
                        color = Color(0xFF1E88E5).copy(alpha = 0.1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            "Sold: ${product.sold}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF1E88E5),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = when {
                                product.stock == 0 -> Icons.Default.Warning
                                product.stock < 10 -> Icons.Default.Info
                                else -> Icons.Default.CheckCircle
                            },
                            contentDescription = null,
                            tint = when {
                                product.stock == 0 -> Color(0xFFF44336)
                                product.stock < 10 -> Color(0xFFFF9800)
                                else -> Color(0xFF4CAF50)
                            },
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            "${product.stock}",
                            fontSize = 13.sp,
                            color = when {
                                product.stock == 0 -> Color(0xFFF44336)
                                product.stock < 10 -> Color(0xFFFF9800)
                                else -> Color(0xFF4CAF50)
                            },
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Verification Status Banner
            if (product.verificationStatus != "Verified") {
                HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            if (product.verificationStatus == "Pending")
                                Color(0xFFFFF3E0)
                            else
                                Color(0xFFFFEBEE)
                        )
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (product.verificationStatus == "Pending")
                            Icons.Default.HourglassEmpty
                        else
                            Icons.Default.Cancel,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = if (product.verificationStatus == "Pending")
                            Color(0xFFFF9800)
                        else
                            Color(0xFFF44336)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            if (product.verificationStatus == "Pending")
                                "Pending Admin Verification"
                            else
                                "Rejected by Admin",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (product.verificationStatus == "Pending")
                                Color(0xFFE65100)
                            else
                                Color(0xFFC62828)
                        )
                        if (product.verificationStatus == "Rejected" &&
                            product.rejectionReason.isNotEmpty()
                        ) {
                            Text(
                                "Reason: ${product.rejectionReason}",
                                fontSize = 11.sp,
                                color = Color(0xFF6D4C41)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ModernBargainShortcutCard(count: Int, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(3.dp, RoundedCornerShape(16.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(Color(0xFFFF9800).copy(alpha = 0.15f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Gavel,
                    contentDescription = null,
                    tint = Color(0xFFFF9800),
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Bargain Offers",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = Color(0xFF212121)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (count > 0) {
                        Badge(
                            containerColor = Color(0xFFFF5722)
                        ) {
                            Text(count.toString())
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text(
                        if (count > 0) "requests pending" else "No pending requests",
                        color = Color.Gray,
                        fontSize = 13.sp
                    )
                }
            }
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color(0xFFFF9800),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
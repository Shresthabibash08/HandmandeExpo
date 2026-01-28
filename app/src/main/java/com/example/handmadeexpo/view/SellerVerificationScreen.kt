package com.example.handmadeexpo.view

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.handmadeexpo.model.SellerModel
import com.example.handmadeexpo.viewmodel.AdminViewModel

@Composable
fun SellerVerificationScreen(viewModel: AdminViewModel) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F7FA))
    ) {
        // Sub-tabs for Pending/Verified/Rejected
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .shadow(2.dp, RoundedCornerShape(12.dp)),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(12.dp)
        ) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.Transparent,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = Color(0xFFFF9800),
                        height = 2.dp
                    )
                }
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                "Pending",
                                fontSize = 13.sp,
                                fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal
                            )
                            if (viewModel.pendingSellers.isNotEmpty()) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Badge(containerColor = Color(0xFFFF9800)) {
                                    Text(viewModel.pendingSellers.size.toString(), fontSize = 9.sp)
                                }
                            }
                        }
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Text(
                            "Verified (${viewModel.verifiedSellers.size})",
                            fontSize = 13.sp,
                            fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = {
                        Text(
                            "Rejected (${viewModel.rejectedSellers.size})",
                            fontSize = 13.sp,
                            fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (selectedTab) {
            0 -> ModernSellerList(
                sellers = viewModel.pendingSellers,
                status = "Pending",
                onVerify = { seller ->
                    viewModel.verifyOrRejectSeller(seller.sellerId, "Verified") { success, msg ->
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                    }
                },
                onReject = { seller ->
                    viewModel.verifyOrRejectSeller(seller.sellerId, "Rejected") { success, msg ->
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                    }
                }
            )
            1 -> ModernSellerList(
                sellers = viewModel.verifiedSellers,
                status = "Verified",
                onVerify = null,
                onReject = null
            )
            2 -> ModernSellerList(
                sellers = viewModel.rejectedSellers,
                status = "Rejected",
                onVerify = { seller ->
                    viewModel.verifyOrRejectSeller(seller.sellerId, "Verified") { success, msg ->
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                    }
                },
                onReject = null
            )
        }
    }
}

@Composable
fun ModernSellerList(
    sellers: List<SellerModel>,
    status: String,
    onVerify: ((SellerModel) -> Unit)?,
    onReject: ((SellerModel) -> Unit)?
) {
    var selectedSeller by remember { mutableStateOf<SellerModel?>(null) }

    if (sellers.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = when(status) {
                        "Pending" -> Icons.Default.HourglassEmpty
                        "Verified" -> Icons.Default.CheckCircle
                        else -> Icons.Default.Cancel
                    },
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = Color.Gray.copy(alpha = 0.3f)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "No $status sellers",
                    fontSize = 16.sp,
                    color = Color.Gray
                )
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(sellers) { seller ->
                ModernSellerVerificationCard(
                    seller = seller,
                    status = status,
                    onViewDetails = { selectedSeller = seller },
                    onVerify = onVerify?.let { { it(seller) } },
                    onReject = onReject?.let { { it(seller) } }
                )
            }
        }
    }

    selectedSeller?.let { seller ->
        ModernSellerDetailsDialog(
            seller = seller,
            onDismiss = { selectedSeller = null }
        )
    }
}

@Composable
fun ModernSellerVerificationCard(
    seller: SellerModel,
    status: String,
    onViewDetails: () -> Unit,
    onVerify: (() -> Unit)?,
    onReject: (() -> Unit)?
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(Color(0xFFFF9800).copy(alpha = 0.15f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Store,
                            contentDescription = null,
                            tint = Color(0xFFFF9800),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            seller.shopName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color(0xFF212121)
                        )
                        Text(
                            seller.fullName,
                            fontSize = 13.sp,
                            color = Color.Gray
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = when(status) {
                        "Verified" -> Color(0xFF4CAF50).copy(alpha = 0.15f)
                        "Rejected" -> Color(0xFFF44336).copy(alpha = 0.15f)
                        else -> Color(0xFFFF9800).copy(alpha = 0.15f)
                    }
                ) {
                    Text(
                        status,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        color = when(status) {
                            "Verified" -> Color(0xFF2E7D32)
                            "Rejected" -> Color(0xFFC62828)
                            else -> Color(0xFFE65100)
                        },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Divider(color = Color(0xFFEEEEEE))
            Spacer(modifier = Modifier.height(12.dp))

            // Info Section
            ModernInfoRow(icon = Icons.Default.Email, text = seller.sellerEmail)
            Spacer(modifier = Modifier.height(8.dp))
            ModernInfoRow(icon = Icons.Default.Phone, text = seller.sellerPhoneNumber)
            Spacer(modifier = Modifier.height(8.dp))
            ModernInfoRow(icon = Icons.Default.LocationOn, text = seller.sellerAddress)
            Spacer(modifier = Modifier.height(8.dp))
            ModernInfoRow(icon = Icons.Default.Badge, text = "PAN: ${seller.panNumber}")

            if (seller.documentType.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                ModernInfoRow(
                    icon = Icons.Default.Description,
                    text = "Document: ${seller.documentType}"
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onViewDetails,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFF757575)
                    )
                ) {
                    Icon(Icons.Default.Visibility, null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("View", fontWeight = FontWeight.Medium)
                }

                if (onVerify != null) {
                    Button(
                        onClick = onVerify,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4CAF50)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.CheckCircle, null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Verify", fontWeight = FontWeight.Bold)
                    }
                }

                if (onReject != null) {
                    Button(
                        onClick = onReject,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFF44336)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Cancel, null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Reject", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun ModernInfoRow(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            modifier = Modifier.size(18.dp),
            tint = Color(0xFF757575)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text,
            fontSize = 14.sp,
            color = Color(0xFF424242)
        )
    }
}

@Composable
fun ModernDetailItem(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            label,
            fontSize = 11.sp,
            color = Color.Gray,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            value.ifEmpty { "N/A" },
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF212121)
        )
    }
}

@Composable
fun ModernSellerDetailsDialog(
    seller: SellerModel,
    onDismiss: () -> Unit
) {
    var showFullImage by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                // Header
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Color(0xFFFF9800),
                                RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
                            )
                            .padding(20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(Color.White.copy(alpha = 0.3f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Store,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                "Seller Verification",
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                color = Color.White
                            )
                        }
                        IconButton(onClick = onDismiss) {
                            Icon(
                                Icons.Default.Close,
                                "Close",
                                tint = Color.White
                            )
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(8.dp)) }

                // Business Information Section
                item {
                    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .background(Color(0xFFFF9800), CircleShape)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Business Information",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Color(0xFFFF9800)
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }

                item {
                    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                        ModernDetailItem("Shop Name", seller.shopName)
                        ModernDetailItem("Owner Name", seller.fullName)
                        ModernDetailItem("Email", seller.sellerEmail)
                        ModernDetailItem("Phone", seller.sellerPhoneNumber)
                        ModernDetailItem("Address", seller.sellerAddress)
                        ModernDetailItem("PAN Number", seller.panNumber)
                    }
                }

                // Verification Document Section
                if (seller.documentType.isNotEmpty() || seller.documentUrl.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .background(Color(0xFF4CAF50), CircleShape)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "Verification Document",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = Color(0xFF4CAF50)
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }

                    if (seller.documentType.isNotEmpty()) {
                        item {
                            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                                ModernDetailItem("Document Type", seller.documentType)
                            }
                        }
                    }

                    if (seller.documentUrl.isNotEmpty()) {
                        item {
                            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    "Document Image",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.Gray
                                )
                                Spacer(modifier = Modifier.height(8.dp))

                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(300.dp)
                                        .shadow(2.dp, RoundedCornerShape(12.dp)),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Box(modifier = Modifier.fillMaxSize()) {
                                        AsyncImage(
                                            model = seller.documentUrl,
                                            contentDescription = "Verification Document",
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Fit,
                                            error = painterResource(android.R.drawable.ic_menu_report_image)
                                        )

                                        IconButton(
                                            onClick = { showFullImage = true },
                                            modifier = Modifier
                                                .align(Alignment.TopEnd)
                                                .padding(12.dp)
                                        ) {
                                            Surface(
                                                color = Color.Black.copy(alpha = 0.7f),
                                                shape = CircleShape
                                            ) {
                                                Icon(
                                                    Icons.Default.ZoomIn,
                                                    "View Full Image",
                                                    tint = Color.White,
                                                    modifier = Modifier.padding(12.dp).size(20.dp)
                                                )
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                Surface(
                                    color = Color(0xFFE3F2FD),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            Icons.Default.Info,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp),
                                            tint = Color(0xFF1E88E5)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            "Tap zoom icon to view full image",
                                            fontSize = 12.sp,
                                            color = Color(0xFF1976D2)
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        item {
                            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(
                                        containerColor = Color(0xFFFFF3E0)
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            Icons.Default.Warning,
                                            contentDescription = null,
                                            tint = Color(0xFFFF9800),
                                            modifier = Modifier.size(24.dp)
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(
                                            "No verification document uploaded",
                                            fontSize = 14.sp,
                                            color = Color(0xFFE65100),
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Status Section
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                        ModernDetailItem("Verification Status", seller.verificationStatus)
                    }
                }

                // Close Button
                item {
                    Spacer(modifier = Modifier.height(20.dp))
                    Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp)) {
                        Button(
                            onClick = onDismiss,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFFF9800)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Close", fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 4.dp))
                        }
                    }
                }
            }
        }
    }

    if (showFullImage && seller.documentUrl.isNotEmpty()) {
        ModernFullImageDialog(
            imageUrl = seller.documentUrl,
            onDismiss = { showFullImage = false }
        )
    }
}

@Composable
fun ModernFullImageDialog(
    imageUrl: String,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f),
            shape = RoundedCornerShape(20.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = "Document Full View",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit,
                    error = painterResource(android.R.drawable.ic_menu_report_image)
                )

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                ) {
                    Surface(
                        color = Color.Black.copy(alpha = 0.7f),
                        shape = CircleShape
                    ) {
                        Icon(
                            Icons.Default.Close,
                            "Close",
                            tint = Color.White,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }
        }
    }
}
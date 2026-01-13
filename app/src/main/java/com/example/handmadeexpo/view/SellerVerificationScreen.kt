package com.example.handmadeexpo.view

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.handmadeexpo.model.SellerModel
import com.example.handmadeexpo.viewmodel.AdminViewModel

@Composable
fun SellerVerificationScreen(viewModel: AdminViewModel) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxSize()) {
        // Tab Row
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color(0xFFF5F5F5)
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Pending")
                        if (viewModel.pendingSellers.isNotEmpty()) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Badge { Text(viewModel.pendingSellers.size.toString()) }
                        }
                    }
                }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Verified") }
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                text = { Text("Rejected") }
            )
        }

        // Content
        when (selectedTab) {
            0 -> SellerList(
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
            1 -> SellerList(
                sellers = viewModel.verifiedSellers,
                status = "Verified",
                onVerify = null,
                onReject = null
            )
            2 -> SellerList(
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
fun SellerList(
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
                    tint = Color.Gray
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "No $status sellers",
                    fontSize = 18.sp,
                    color = Color.Gray
                )
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(sellers) { seller ->
                SellerVerificationCard(
                    seller = seller,
                    status = status,
                    onViewDetails = { selectedSeller = seller },
                    onVerify = onVerify?.let { { it(seller) } },
                    onReject = onReject?.let { { it(seller) } }
                )
            }
        }
    }

    // Details Dialog
    selectedSeller?.let { seller ->
        SellerDetailsDialog(
            seller = seller,
            onDismiss = { selectedSeller = null }
        )
    }
}

@Composable
fun SellerVerificationCard(
    seller: SellerModel,
    status: String,
    onViewDetails: () -> Unit,
    onVerify: (() -> Unit)?,
    onReject: (() -> Unit)?
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        seller.shopName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Text(
                        seller.fullName,
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }

                // Status Badge
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = when(status) {
                        "Verified" -> Color(0xFF4CAF50).copy(alpha = 0.1f)
                        "Rejected" -> Color(0xFFF44336).copy(alpha = 0.1f)
                        else -> Color(0xFFFF9800).copy(alpha = 0.1f)
                    }
                ) {
                    Text(
                        status,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        color = when(status) {
                            "Verified" -> Color(0xFF4CAF50)
                            "Rejected" -> Color(0xFFF44336)
                            else -> Color(0xFFFF9800)
                        },
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Seller Info
            InfoRow(icon = Icons.Default.Email, text = seller.sellerEmail)
            InfoRow(icon = Icons.Default.Phone, text = seller.sellerPhoneNumber)
            InfoRow(icon = Icons.Default.LocationOn, text = seller.sellerAddress)
            InfoRow(icon = Icons.Default.Badge, text = "PAN: ${seller.panNumber}")

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // View Details
                OutlinedButton(
                    onClick = onViewDetails,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Visibility, null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Details")
                }

                // Verify Button
                if (onVerify != null) {
                    Button(
                        onClick = onVerify,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4CAF50)
                        )
                    ) {
                        Icon(Icons.Default.CheckCircle, null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Verify")
                    }
                }

                // Reject Button
                if (onReject != null) {
                    Button(
                        onClick = onReject,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFF44336)
                        )
                    ) {
                        Icon(Icons.Default.Cancel, null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Reject")
                    }
                }
            }
        }
    }
}

@Composable
fun InfoRow(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(
        modifier = Modifier.padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = Color.Gray
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text,
            fontSize = 13.sp,
            color = Color.Gray
        )
    }
}

@Composable
fun SellerDetailsDialog(
    seller: SellerModel,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Seller Details",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                DetailItem("Shop Name", seller.shopName)
                DetailItem("Owner Name", seller.fullName)
                DetailItem("Email", seller.sellerEmail)
                DetailItem("Phone", seller.sellerPhoneNumber)
                DetailItem("Address", seller.sellerAddress)
                DetailItem("PAN Number", seller.panNumber)
                DetailItem("Document Type", seller.documentType.ifEmpty { "N/A" })
                DetailItem("Verification Status", seller.verificationStatus)
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

@Composable
fun DetailItem(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(
            label,
            fontSize = 12.sp,
            color = Color.Gray,
            fontWeight = FontWeight.Medium
        )
        Text(
            value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal
        )
    }
}
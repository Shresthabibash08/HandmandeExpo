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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.handmadeexpo.model.ProductModel
import com.example.handmadeexpo.viewmodel.AdminViewModel

@Composable
fun ProductVerificationScreen(viewModel: AdminViewModel) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxSize()) {
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
                        if (viewModel.pendingProducts.isNotEmpty()) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Badge { Text(viewModel.pendingProducts.size.toString()) }
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

        when (selectedTab) {
            0 -> ProductList(
                products = viewModel.pendingProducts,
                status = "Pending",
                onVerify = { product ->
                    viewModel.verifyOrRejectProduct(product.productId, "Verified") { success, msg ->
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                    }
                },
                onReject = { product, reason ->
                    viewModel.verifyOrRejectProduct(product.productId, "Rejected", reason) { success, msg ->
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                    }
                }
            )
            1 -> ProductList(
                products = viewModel.verifiedProducts,
                status = "Verified",
                onVerify = null,
                onReject = null
            )
            2 -> ProductList(
                products = viewModel.rejectedProducts,
                status = "Rejected",
                onVerify = { product ->
                    viewModel.verifyOrRejectProduct(product.productId, "Verified") { success, msg ->
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                    }
                },
                onReject = null
            )
        }
    }
}

@Composable
fun ProductList(
    products: List<ProductModel>,
    status: String,
    onVerify: ((ProductModel) -> Unit)?,
    onReject: ((ProductModel, String) -> Unit)?
) {
    var selectedProduct by remember { mutableStateOf<ProductModel?>(null) }
    var showRejectDialog by remember { mutableStateOf(false) }
    var productToReject by remember { mutableStateOf<ProductModel?>(null) }

    if (products.isEmpty()) {
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
                    "No $status products",
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
            items(products) { product ->
                ProductVerificationCard(
                    product = product,
                    status = status,
                    onViewDetails = { selectedProduct = product },
                    onVerify = onVerify?.let { { it(product) } },
                    onReject = onReject?.let {
                        {
                            productToReject = product
                            showRejectDialog = true
                        }
                    }
                )
            }
        }
    }

    // Product Details Dialog
    selectedProduct?.let { product ->
        ProductDetailsDialog(
            product = product,
            onDismiss = { selectedProduct = null }
        )
    }

    // Rejection Dialog
    if (showRejectDialog && productToReject != null) {
        RejectionReasonDialog(
            onConfirm = { reason ->
                onReject?.invoke(productToReject!!, reason)
                showRejectDialog = false
                productToReject = null
            },
            onDismiss = {
                showRejectDialog = false
                productToReject = null
            }
        )
    }
}

@Composable
fun ProductVerificationCard(
    product: ProductModel,
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                // Product Image
                AsyncImage(
                    model = product.image,
                    contentDescription = product.name,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(12.dp))

                // Product Info
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            product.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            modifier = Modifier.weight(1f)
                        )

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
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                color = when(status) {
                                    "Verified" -> Color(0xFF4CAF50)
                                    "Rejected" -> Color(0xFFF44336)
                                    else -> Color(0xFFFF9800)
                                },
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        "NRP ${product.price.toInt()}",
                        color = Color(0xFFE65100),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row {
                        Text("Stock: ${product.stock}", fontSize = 12.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Sold: ${product.sold}", fontSize = 12.sp, color = Color.Gray)
                    }

                    if (product.rejectionReason.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Reason: ${product.rejectionReason}",
                            fontSize = 12.sp,
                            color = Color.Red
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onViewDetails,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Visibility, null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Details")
                }

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
fun ProductDetailsDialog(
    product: ProductModel,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Product Details", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                AsyncImage(
                    model = product.image,
                    contentDescription = product.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.height(12.dp))

                DetailItem("Product Name", product.name)
                DetailItem("Price", "NRP ${product.price.toInt()}")
                DetailItem("Stock", product.stock.toString())
                DetailItem("Sold", product.sold.toString())
                DetailItem("Description", product.description.ifEmpty { "N/A" })
                DetailItem("Status", product.verificationStatus)
                if (product.rejectionReason.isNotEmpty()) {
                    DetailItem("Rejection Reason", product.rejectionReason)
                }
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
fun RejectionReasonDialog(
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var reason by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Rejection Reason") },
        text = {
            Column {
                Text(
                    "Please provide a reason for rejecting this product:",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = reason,
                    onValueChange = { reason = it },
                    label = { Text("Reason") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    placeholder = { Text("e.g., Poor quality, Inappropriate content, etc.") }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (reason.isNotBlank()) {
                        onConfirm(reason)
                    }
                },
                enabled = reason.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFF44336)
                )
            ) {
                Text("Reject")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
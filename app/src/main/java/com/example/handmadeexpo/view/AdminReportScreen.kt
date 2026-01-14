package com.example.handmadeexpo.view

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.handmadeexpo.model.ReportModel
import com.example.handmadeexpo.viewmodel.AdminReportViewModel
import com.google.firebase.database.FirebaseDatabase

@Composable
fun AdminReportScreen() {
    val viewModel: AdminReportViewModel = viewModel()
    val reports by viewModel.reports.collectAsState()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(Color.White)
    ) {
        Text(
            text = "Reported Products",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (reports.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No pending reports!", color = Color.Gray)
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(reports) { report ->
                    ReportItem(
                        report = report,
                        onAccept = {
                            viewModel.acceptReport(report) { msg ->
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                            }
                        },
                        onReject = {
                            viewModel.rejectReport(report) { msg ->
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ReportItem(
    report: ReportModel,
    onAccept: () -> Unit,
    onReject: () -> Unit
) {
    // --- State to hold fetched names ---
    var productName by remember { mutableStateOf("Loading Product...") }
    var sellerName by remember { mutableStateOf("Loading Seller...") }

    // --- Fetch Logic ---
    LaunchedEffect(report.productId) {
        val db = FirebaseDatabase.getInstance()

        // 1. Fetch Product to get Name and SellerID
        db.getReference("products").child(report.productId).get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    val pName = snapshot.child("name").value.toString()
                    val sId = snapshot.child("sellerId").value.toString()
                    productName = pName

                    // 2. Fetch Seller Name using SellerID
                    // Note: Ensure this matches your DB node ("Seller" or "sellers")
                    db.getReference("Seller").child(sId).get()
                        .addOnSuccessListener { sellerSnap ->
                            if (sellerSnap.exists()) {
                                // Try getting shopName first, fallback to fullName
                                val shop = sellerSnap.child("shopName").value?.toString()
                                    ?: sellerSnap.child("fullName").value.toString()
                                sellerName = shop
                            } else {
                                sellerName = "Unknown Seller"
                            }
                        }
                } else {
                    productName = "Product Deleted/Not Found"
                    sellerName = "N/A"
                }
            }
    }

    Card(
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF0F0)),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Reason & Date
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = Color.Red, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Complaint", fontWeight = FontWeight.Bold, color = Color.Red)
                }
                // Optional: You can format timestamp here if needed
                Text("ID: ${report.productId.take(6)}...", fontSize = 10.sp, color = Color.Gray)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Reason Body
            Text(
                text = "\"${report.reason}\"",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
            )

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(12.dp))

            // Details Section (Fetched Data)
            DetailsRow(label = "Product Name:", value = productName)
            DetailsRow(label = "Seller Name:", value = sellerName)
            DetailsRow(label = "Product ID:", value = report.productId)

            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onReject,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Gray)
                ) {
                    Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Ignore")
                }

                Button(
                    onClick = onAccept,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Delete Item")
                }
            }
        }
    }
}

@Composable
fun DetailsRow(label: String, value: String) {
    Row(modifier = Modifier.padding(vertical = 2.dp)) {
        Text(text = label, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = Color.DarkGray, modifier = Modifier.width(100.dp))
        Text(text = value, fontSize = 13.sp, color = Color.Black)
    }
}
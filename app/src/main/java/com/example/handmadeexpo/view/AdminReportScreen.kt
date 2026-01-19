package com.example.handmadeexpo.view

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
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
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("Product Reports", "Seller Reports")

    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
        // Header
        Text(
            text = "Admin Dashboard",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )

        // Tabs
        TabRow(selectedTabIndex = selectedTabIndex) {
            tabTitles.forEachIndexed { i, title ->
                Tab(
                    selected = selectedTabIndex == i,
                    onClick = { selectedTabIndex = i },
                    text = { Text(title) }
                )
            }
        }

        // Filter Logic
        val filtered = reports.filter {
            if (selectedTabIndex == 0) it.reportType == "PRODUCT" else it.reportType == "SELLER"
        }

        // List Content
        if (filtered.isEmpty()) {
            Box(Modifier.fillMaxSize(), Alignment.Center) {
                Text("No pending reports", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filtered) { report ->
                    ReportItem(
                        report = report,
                        onAccept = {
                            // --- UPDATED LOGIC HERE ---
                            if (report.reportType == "SELLER") {
                                // Call the NEW ban function
                                viewModel.banSeller(report) { msg ->
                                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                // Existing logic for products
                                viewModel.acceptReport(report) { msg ->
                                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        onReject = {
                            viewModel.rejectReport(report) {
                                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ReportItem(report: ReportModel, onAccept: () -> Unit, onReject: () -> Unit) {
    // State to hold fetched names
    var targetName by remember { mutableStateOf("Loading...") }
    var reporterName by remember { mutableStateOf("Loading...") }

    val isProduct = report.reportType == "PRODUCT"

    // Fetch Data Logic
    LaunchedEffect(report) {
        val db = FirebaseDatabase.getInstance()

        // 1. FETCH REPORTER (The User who made the report)
        if (report.reporterId.isNotEmpty()) {
            // Check "Buyer" node first
            db.getReference("Buyer").child(report.reporterId).get().addOnSuccessListener { snap ->
                if (snap.exists()) {
                    // CORRECTED: Explicitly fetching 'buyerName' as per your database structure
                    reporterName = snap.child("buyerName").value?.toString() ?: "Unknown Buyer"
                } else {
                    // If not found in Buyer, check "Seller" node
                    db.getReference("Seller").child(report.reporterId).get().addOnSuccessListener { sellerSnap ->
                        if (sellerSnap.exists()) {
                            reporterName = sellerSnap.child("shopName").value?.toString()
                                ?: sellerSnap.child("fullName").value?.toString()
                                        ?: "Seller"
                        } else {
                            reporterName = "User ID not found"
                        }
                    }
                }
            }.addOnFailureListener {
                reporterName = "Error fetching name"
            }
        } else {
            reporterName = "Anonymous"
        }

        // 2. FETCH TARGET (The Product or Seller being reported)
        if (isProduct) {
            db.getReference("products").child(report.reportedId).get().addOnSuccessListener { snap ->
                targetName = snap.child("name").value?.toString() ?: "Product Deleted"
            }
        } else {
            db.getReference("Seller").child(report.reportedId).get().addOnSuccessListener { snap ->
                targetName = snap.child("shopName").value?.toString()
                    ?: snap.child("fullName").value?.toString()
                            ?: "Unknown Shop"
            }
        }
    }

    // UI Card
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF0F0)), // Light Red background
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Card Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    if (isProduct) Icons.Default.ShoppingCart else Icons.Default.Person,
                    contentDescription = null,
                    tint = Color.Red
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    if (isProduct) "Product Report" else "Seller Report",
                    fontWeight = FontWeight.Bold,
                    color = Color.Red
                )
            }

            Spacer(Modifier.height(12.dp))

            // Reporter Info
            Text("Reported By: $reporterName", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            Text("Reporter ID: ${report.reporterId}", fontSize = 10.sp, color = Color.Gray)

            Spacer(Modifier.height(8.dp))

            // Target Info
            Text("Target: $targetName", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            Text("Target ID: ${report.reportedId}", fontSize = 10.sp, color = Color.Gray)

            Spacer(Modifier.height(12.dp))

            // Reason
            Text("Reason:", fontSize = 12.sp, color = Color.DarkGray, fontWeight = FontWeight.Bold)
            Text("\"${report.reason}\"", fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)

            Spacer(Modifier.height(16.dp))

            // Action Buttons
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = onReject,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Ignore")
                }
                Button(
                    onClick = onAccept,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text(if (isProduct) "Delete Item" else "Ban Seller")
                }
            }
        }
    }
}
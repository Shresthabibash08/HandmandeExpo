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
import androidx.compose.material.icons.filled.Warning // Added for Warning Icon
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

    // 1. ADD "Buyer Reports" TO TABS
    val tabTitles = listOf("Product Reports", "Seller Reports", "Buyer Reports")

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

        // 2. UPDATE FILTER LOGIC
        val filtered = reports.filter {
            when (selectedTabIndex) {
                0 -> it.reportType == "PRODUCT"
                1 -> it.reportType == "SELLER"
                else -> it.reportType == "BUYER" // Show Buyer reports in 3rd tab
            }
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
                            // 3. HANDLE DIFFERENT BAN TYPES
                            when (report.reportType) {
                                "SELLER" -> {
                                    viewModel.banSeller(report) { msg ->
                                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                    }
                                }
                                "BUYER" -> {
                                    // Call the new banBuyer function
                                    viewModel.banBuyer(report) { msg ->
                                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                    }
                                }
                                else -> {
                                    // Product Deletion
                                    viewModel.acceptReport(report) { msg ->
                                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                    }
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
    var targetName by remember { mutableStateOf("Loading...") }
    var reporterName by remember { mutableStateOf("Loading...") }

    // Helper booleans
    val isProduct = report.reportType == "PRODUCT"
    val isBuyerReport = report.reportType == "BUYER"

    LaunchedEffect(report) {
        val db = FirebaseDatabase.getInstance()

        // --- 1. FETCH REPORTER NAME ---
        // (If it's a Buyer Report, the reporter is a Seller. If it's a Seller Report, reporter is a Buyer)
        if (report.reporterId.isNotEmpty()) {
            db.getReference("Buyer").child(report.reporterId).get().addOnSuccessListener { snap ->
                if (snap.exists()) {
                    reporterName = snap.child("buyerName").value?.toString() ?: "Unknown Buyer"
                } else {
                    // Check Seller node if not found in Buyer
                    db.getReference("Seller").child(report.reporterId).get().addOnSuccessListener { sellerSnap ->
                        if (sellerSnap.exists()) {
                            reporterName = sellerSnap.child("shopName").value?.toString() ?: "Seller"
                        } else {
                            reporterName = "User ID not found"
                        }
                    }
                }
            }
        } else {
            reporterName = "Anonymous"
        }

        // --- 2. FETCH TARGET NAME (Who/What is being reported) ---
        when (report.reportType) {
            "PRODUCT" -> {
                db.getReference("products").child(report.reportedId).get().addOnSuccessListener { snap ->
                    targetName = snap.child("name").value?.toString() ?: "Product Deleted"
                }
            }
            "SELLER" -> {
                db.getReference("Seller").child(report.reportedId).get().addOnSuccessListener { snap ->
                    targetName = snap.child("shopName").value?.toString() ?: "Unknown Shop"
                }
            }
            "BUYER" -> {
                // NEW: Fetch Buyer Name for the target
                db.getReference("Buyer").child(report.reportedId).get().addOnSuccessListener { snap ->
                    targetName = snap.child("buyerName").value?.toString() ?: "Unknown Buyer"
                }
            }
        }
    }

    // UI Card
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF0F0)),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Update Icon based on type
                val icon = when (report.reportType) {
                    "PRODUCT" -> Icons.Default.ShoppingCart
                    "BUYER" -> Icons.Default.Warning // Different icon for abusive buyers
                    else -> Icons.Default.Person
                }

                Icon(icon, contentDescription = null, tint = Color.Red)
                Spacer(Modifier.width(8.dp))

                // Dynamic Title
                val title = when (report.reportType) {
                    "PRODUCT" -> "Product Report"
                    "SELLER" -> "Seller Report"
                    else -> "Abusive Buyer Report"
                }

                Text(title, fontWeight = FontWeight.Bold, color = Color.Red)
            }

            Spacer(Modifier.height(12.dp))

            Text("Reported By: $reporterName", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            Text("Reporter ID: ${report.reporterId}", fontSize = 10.sp, color = Color.Gray)

            Spacer(Modifier.height(8.dp))

            Text("Target: $targetName", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            Text("Target ID: ${report.reportedId}", fontSize = 10.sp, color = Color.Gray)

            Spacer(Modifier.height(12.dp))

            Text("Reason:", fontSize = 12.sp, color = Color.DarkGray, fontWeight = FontWeight.Bold)
            Text("\"${report.reason}\"", fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)

            Spacer(Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = onReject,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Ignore")
                }

                // Dynamic Button Text
                val buttonText = when(report.reportType) {
                    "PRODUCT" -> "Delete Item"
                    "SELLER" -> "Ban Seller"
                    else -> "Ban Buyer"
                }

                Button(
                    onClick = onAccept,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text(buttonText)
                }
            }
        }
    }
}
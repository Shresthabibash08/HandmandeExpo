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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.handmadeexpo.model.ReportModel
import com.example.handmadeexpo.viewmodel.AdminReportViewModel
import com.google.firebase.database.FirebaseDatabase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminReportScreen() {
    val viewModel: AdminReportViewModel = viewModel()
    val reports by viewModel.reports.collectAsState()
    val context = LocalContext.current
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    val tabTitles = listOf("Products", "Sellers", "Buyers")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F7FA))
    ) {
        // Modern Header
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(4.dp, RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(Color(0xFFF44336).copy(alpha = 0.15f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Report,
                            contentDescription = null,
                            tint = Color(0xFFF44336),
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            "Report Management",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF212121)
                        )
                        Text(
                            "${reports.size} total reports",
                            fontSize = 13.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Modern Tabs
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .shadow(2.dp, RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp)
        ) {
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = Color.Transparent,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                        color = Color(0xFFF44336),
                        height = 3.dp
                    )
                }
            ) {
                tabTitles.forEachIndexed { i, title ->
                    val count = reports.count {
                        when (i) {
                            0 -> it.reportType == "PRODUCT"
                            1 -> it.reportType == "SELLER"
                            else -> it.reportType == "BUYER"
                        }
                    }

                    Tab(
                        selected = selectedTabIndex == i,
                        onClick = { selectedTabIndex = i },
                        text = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    title,
                                    fontWeight = if (selectedTabIndex == i) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 14.sp
                                )
                                if (count > 0) {
                                    Spacer(Modifier.width(8.dp))
                                    Badge(
                                        containerColor = Color(0xFFF44336)
                                    ) {
                                        Text(count.toString(), fontSize = 10.sp)
                                    }
                                }
                            }
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Filter Reports
        val filtered = reports.filter {
            when (selectedTabIndex) {
                0 -> it.reportType == "PRODUCT"
                1 -> it.reportType == "SELLER"
                else -> it.reportType == "BUYER"
            }
        }

        // List Content
        if (filtered.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = Color.Gray.copy(alpha = 0.3f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "No pending reports",
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
                items(filtered) { report ->
                    ModernReportItem(
                        report = report,
                        onAccept = {
                            when (report.reportType) {
                                "SELLER" -> {
                                    viewModel.banSeller(report) { msg ->
                                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                    }
                                }
                                "BUYER" -> {
                                    viewModel.banBuyer(report) { msg ->
                                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                    }
                                }
                                else -> {
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
fun ModernReportItem(report: ReportModel, onAccept: () -> Unit, onReject: () -> Unit) {
    var targetName by remember { mutableStateOf("Loading...") }
    var reporterName by remember { mutableStateOf("Loading...") }

    LaunchedEffect(report) {
        val db = FirebaseDatabase.getInstance()

        // Fetch Reporter Name
        if (report.reporterId.isNotEmpty()) {
            db.getReference("Buyer").child(report.reporterId).get().addOnSuccessListener { snap ->
                if (snap.exists()) {
                    reporterName = snap.child("buyerName").value?.toString() ?: "Unknown Buyer"
                } else {
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

        // Fetch Target Name
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
                db.getReference("Buyer").child(report.reportedId).get().addOnSuccessListener { snap ->
                    targetName = snap.child("buyerName").value?.toString() ?: "Unknown Buyer"
                }
            }
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header with Icon
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                val (icon, color, title) = when (report.reportType) {
                    "PRODUCT" -> Triple(Icons.Default.ShoppingCart, Color(0xFF4CAF50), "Product Report")
                    "SELLER" -> Triple(Icons.Default.Store, Color(0xFFFF9800), "Seller Report")
                    else -> Triple(Icons.Default.Warning, Color(0xFFF44336), "Buyer Report")
                }

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(color.copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        icon,
                        contentDescription = null,
                        tint = color,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(Modifier.width(12.dp))

                Text(
                    title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFF212121)
                )
            }

            Spacer(Modifier.height(16.dp))
            Divider(color = Color(0xFFEEEEEE))
            Spacer(Modifier.height(12.dp))

            // Reporter Info
            InfoRow(
                label = "Reported By",
                value = reporterName,
                subValue = report.reporterId
            )

            Spacer(Modifier.height(8.dp))

            // Target Info
            InfoRow(
                label = "Target",
                value = targetName,
                subValue = report.reportedId
            )

            Spacer(Modifier.height(12.dp))

            // Reason Section
            Surface(
                color = Color(0xFFFFF3E0),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        "Reason:",
                        fontSize = 12.sp,
                        color = Color(0xFFE65100),
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "\"${report.reason}\"",
                        fontSize = 14.sp,
                        color = Color(0xFF424242),
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onReject,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFF757575)
                    )
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text("Ignore", fontWeight = FontWeight.Medium)
                }

                val (buttonText, buttonIcon) = when(report.reportType) {
                    "PRODUCT" -> Pair("Delete", Icons.Default.Delete)
                    "SELLER" -> Pair("Ban Seller", Icons.Default.Block)
                    else -> Pair("Ban Buyer", Icons.Default.Block)
                }

                Button(
                    onClick = onAccept,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFF44336)
                    )
                ) {
                    Icon(
                        buttonIcon,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(buttonText, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun InfoRow(label: String, value: String, subValue: String) {
    Column {
        Text(
            label,
            fontSize = 11.sp,
            color = Color.Gray,
            fontWeight = FontWeight.Medium
        )
        Spacer(Modifier.height(2.dp))
        Text(
            value,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF212121)
        )
        Text(
            "ID: $subValue",
            fontSize = 10.sp,
            color = Color.Gray
        )
    }
}
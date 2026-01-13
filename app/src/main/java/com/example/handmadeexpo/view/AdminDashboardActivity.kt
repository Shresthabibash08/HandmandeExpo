package com.example.handmadeexpo.view

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.handmadeexpo.viewmodel.AdminViewModel

class AdminDashboardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Add error logging
        Thread.setDefaultUncaughtExceptionHandler { _, e ->
            Log.e("AdminDashboard", "Uncaught exception", e)
        }

        setContent {
            AdminDashboardScreen()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(adminViewModel: AdminViewModel = viewModel()) {
    var selectedTab by remember { mutableIntStateOf(0) }

    // Add error state
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Show error dialog if there's an error
    if (errorMessage != null) {
        AlertDialog(
            onDismissRequest = { errorMessage = null },
            title = { Text("Error") },
            text = { Text(errorMessage ?: "Unknown error occurred") },
            confirmButton = {
                Button(onClick = { errorMessage = null }) {
                    Text("OK")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Admin Dashboard", color = Color.White) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF4CAF50)
                )
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    label = { Text("Home") },
                    icon = { Icon(Icons.Default.Home, null) }
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    label = { Text("Verify") },
                    icon = {
                        BadgedBox(
                            badge = {
                                val totalPending = adminViewModel.pendingSellers.size + adminViewModel.pendingProducts.size
                                if (totalPending > 0) {
                                    Badge { Text(totalPending.toString()) }
                                }
                            }
                        ) {
                            Icon(Icons.Default.VerifiedUser, null)
                        }
                    }
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    label = { Text("Users") },
                    icon = { Icon(Icons.Default.Person, null) }
                )
                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    label = { Text("Products") },
                    icon = { Icon(Icons.Default.ShoppingBag, null) }
                )
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            when (selectedTab) {
                0 -> AdminOverview(
                    adminViewModel,
                    onVerificationClick = { selectedTab = 1 },
                    onUserClick = { selectedTab = 2 },
                    onProductClick = { selectedTab = 3 }
                )
                1 -> VerificationHub(adminViewModel)
                2 -> AdminUserListScreen(adminViewModel)
                3 -> AdminProductListScreen(adminViewModel)
            }
        }
    }
}

@Composable
fun VerificationHub(viewModel: AdminViewModel) {
    var selectedVerificationTab by remember { mutableIntStateOf(0) }

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = selectedVerificationTab) {
            Tab(
                selected = selectedVerificationTab == 0,
                onClick = { selectedVerificationTab = 0 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Sellers")
                        if (viewModel.pendingSellers.isNotEmpty()) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Badge { Text(viewModel.pendingSellers.size.toString()) }
                        }
                    }
                }
            )
            Tab(
                selected = selectedVerificationTab == 1,
                onClick = { selectedVerificationTab = 1 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Products")
                        if (viewModel.pendingProducts.isNotEmpty()) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Badge { Text(viewModel.pendingProducts.size.toString()) }
                        }
                    }
                }
            )
        }

        when (selectedVerificationTab) {
            0 -> SellerVerificationScreen(viewModel)
            1 -> ProductVerificationScreen(viewModel)
        }
    }
}

@Composable
fun AdminOverview(
    viewModel: AdminViewModel,
    onVerificationClick: () -> Unit,
    onUserClick: () -> Unit,
    onProductClick: () -> Unit
) {
    val totalUsers = viewModel.sellers.size + viewModel.buyers.size
    val totalPending = viewModel.pendingSellers.size + viewModel.pendingProducts.size

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("System Statistics", fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Spacer(Modifier.height(16.dp))

        if (viewModel.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = Color(0xFF4CAF50))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Loading statistics...")
                }
            }
        } else {
            // Pending Verification Card
            if (totalPending > 0) {
                DashboardStatCard(
                    "Pending Verification",
                    totalPending.toString(),
                    Color(0xFFFF9800),
                    Modifier.fillMaxWidth().clickable { onVerificationClick() }
                )
                Spacer(Modifier.height(8.dp))
            }

            DashboardStatCard(
                "Total Users",
                totalUsers.toString(),
                Color(0xFF6200EE),
                Modifier.fillMaxWidth().clickable { onUserClick() }
            )
            Spacer(Modifier.height(8.dp))

            DashboardStatCard(
                "Total Products",
                viewModel.products.size.toString(),
                Color(0xFF2196F3),
                Modifier.fillMaxWidth().clickable { onProductClick() }
            )
            Spacer(Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                DashboardStatCard(
                    "Sellers",
                    viewModel.sellers.size.toString(),
                    Color(0xFFFF9800),
                    Modifier.weight(1f).clickable { onUserClick() }
                )
                DashboardStatCard(
                    "Buyers",
                    viewModel.buyers.size.toString(),
                    Color(0xFF4CAF50),
                    Modifier.weight(1f).clickable { onUserClick() }
                )
            }

            Spacer(Modifier.height(16.dp))

            // Verification Stats
            Text("Verification Overview", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                VerificationStatCard(
                    "Pending Sellers",
                    viewModel.pendingSellers.size.toString(),
                    Color(0xFFFF9800),
                    Modifier.weight(1f)
                )
                VerificationStatCard(
                    "Pending Products",
                    viewModel.pendingProducts.size.toString(),
                    Color(0xFFFF9800),
                    Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                VerificationStatCard(
                    "Verified Sellers",
                    viewModel.verifiedSellers.size.toString(),
                    Color(0xFF4CAF50),
                    Modifier.weight(1f)
                )
                VerificationStatCard(
                    "Verified Products",
                    viewModel.verifiedProducts.size.toString(),
                    Color(0xFF4CAF50),
                    Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun DashboardStatCard(label: String, count: String, color: Color, modifier: Modifier) {
    Card(
        modifier = modifier.height(100.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f)),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            Modifier.fillMaxSize(),
            Arrangement.Center,
            Alignment.CenterHorizontally
        ) {
            Text(count, fontSize = 32.sp, fontWeight = FontWeight.Bold, color = color)
            Text(label, fontSize = 14.sp, color = Color.Gray)
        }
    }
}

@Composable
fun VerificationStatCard(label: String, count: String, color: Color, modifier: Modifier) {
    Card(
        modifier = modifier.height(80.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f)),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            Modifier.fillMaxSize(),
            Arrangement.Center,
            Alignment.CenterHorizontally
        ) {
            Text(count, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = color)
            Text(label, fontSize = 12.sp, maxLines = 2, color = Color.Gray)
        }
    }
}
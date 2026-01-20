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
import com.example.handmadeexpo.viewmodel.AdminViewModel

class AdminDashboardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d("AdminDashboard", "onCreate called")

        setContent {
            // State-hoisted ViewModel to maintain data during recomposition
            val viewModel = remember { AdminViewModel() }
            AdminDashboardScreen(viewModel)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(adminViewModel: AdminViewModel) {
    // Tab Index: 0=Home, 1=Verify, 2=Users, 3=Products, 4=Reports
    var selectedTab by remember { mutableIntStateOf(0) }

    // Log for debugging
    LaunchedEffect(Unit) {
        Log.d("AdminDashboard", "Screen composed")
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
                        val totalPending = adminViewModel.pendingSellers.size + adminViewModel.pendingProducts.size
                        if (totalPending > 0) {
                            BadgedBox(badge = { Badge { Text(totalPending.toString()) } }) {
                                Icon(Icons.Default.VerifiedUser, null)
                            }
                        } else {
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
                NavigationBarItem(
                    selected = selectedTab == 4,
                    onClick = { selectedTab = 4 },
                    label = { Text("Reports") },
                    icon = { Icon(Icons.Default.Warning, null) }
                )
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when (selectedTab) {
                0 -> AdminOverviewSimple(
                    viewModel = adminViewModel,
                    onUserClick = { selectedTab = 2 },
                    onProductClick = { selectedTab = 3 }
                )
                1 -> VerificationHubSimple(adminViewModel)
                2 -> AdminUserListScreen(adminViewModel)
                3 -> AdminProductListScreen(adminViewModel)
                4 -> AdminReportScreen()
            }
        }
    }
}

@Composable
fun AdminOverviewSimple(
    viewModel: AdminViewModel,
    onUserClick: () -> Unit,
    onProductClick: () -> Unit
) {
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
                    Text("Loading data...", color = Color.Gray)
                }
            }
        } else {
            val totalUsers = viewModel.sellers.size + viewModel.buyers.size
            val totalPending = viewModel.pendingSellers.size + viewModel.pendingProducts.size

            if (totalPending > 0) {
                SimpleStatCard(
                    label = "Pending Verification",
                    count = totalPending.toString(),
                    color = Color(0xFFFF9800)
                )
                Spacer(Modifier.height(12.dp))
            }

            SimpleStatCard(
                label = "Total Users",
                count = totalUsers.toString(),
                color = Color(0xFF6200EE),
                modifier = Modifier.fillMaxWidth().clickable { onUserClick() }
            )
            Spacer(Modifier.height(12.dp))

            SimpleStatCard(
                label = "Total Products",
                count = viewModel.products.size.toString(),
                color = Color(0xFF2196F3),
                modifier = Modifier.fillMaxWidth().clickable { onProductClick() }
            )
            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SmallStatCard(
                    label = "Sellers",
                    count = viewModel.sellers.size.toString(),
                    color = Color(0xFFFF9800),
                    modifier = Modifier.weight(1f).clickable { onUserClick() }
                )
                SmallStatCard(
                    label = "Buyers",
                    count = viewModel.buyers.size.toString(),
                    color = Color(0xFF4CAF50),
                    modifier = Modifier.weight(1f).clickable { onUserClick() }
                )
            }
        }
    }
}

@Composable
fun SimpleStatCard(
    label: String,
    count: String,
    color: Color,
    modifier: Modifier = Modifier.fillMaxWidth()
) {
    Card(
        modifier = modifier.height(100.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f)),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(count, fontSize = 32.sp, fontWeight = FontWeight.Bold, color = color)
            Text(label, fontSize = 14.sp, color = Color.Gray)
        }
    }
}

@Composable
fun SmallStatCard(
    label: String,
    count: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(80.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f)),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(count, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = color)
            Text(label, fontSize = 11.sp, color = Color.Gray, maxLines = 2)
        }
    }
}

@Composable
fun VerificationHubSimple(viewModel: AdminViewModel) {
    var selectedTab by remember { mutableIntStateOf(0) }

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = selectedTab) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Sellers")
                        if (viewModel.pendingSellers.isNotEmpty()) {
                            Spacer(Modifier.width(8.dp))
                            Badge { Text(viewModel.pendingSellers.size.toString()) }
                        }
                    }
                }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Products")
                        if (viewModel.pendingProducts.isNotEmpty()) {
                            Spacer(Modifier.width(8.dp))
                            Badge { Text(viewModel.pendingProducts.size.toString()) }
                        }
                    }
                }
            )
        }

        when (selectedTab) {
            0 -> SellerVerificationScreen(viewModel)
            1 -> ProductVerificationScreen(viewModel)
        }
    }
}

@Composable
fun AdminReportScreen() {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Default.Warning, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color.Gray)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Reports & Complaints", fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Text("No active reports to display.", color = Color.Gray, fontSize = 14.sp)
    }
}
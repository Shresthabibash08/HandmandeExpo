package com.example.handmadeexpo.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.handmadeexpo.viewmodel.AdminViewModel

class AdminDashboardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("AdminDashboard", "onCreate called")
        setContent {
            val viewModel = remember { AdminViewModel() }
            ProfessionalAdminDashboard(viewModel)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfessionalAdminDashboard(adminViewModel: AdminViewModel) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val activity = context as? Activity

    Scaffold(
        topBar = {
            ModernTopBar(onLogoutClick = { showLogoutDialog = true })
        },
        bottomBar = {
            ModernBottomNav(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it },
                pendingCount = adminViewModel.pendingSellers.size + adminViewModel.pendingProducts.size
            )
        },
        containerColor = Color(0xFFF5F7FA)
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            when (selectedTab) {
                0 -> ModernOverviewScreen(
                    viewModel = adminViewModel,
                    onUserClick = { selectedTab = 2 },
                    onProductClick = { selectedTab = 3 },
                    onVerifyClick = { selectedTab = 1 }
                )
                1 -> ModernVerificationHub(adminViewModel)
                2 -> AdminUserListScreen(adminViewModel)
                3 -> AdminProductListScreen(adminViewModel)
                4 -> AdminReportScreen()
            }
        }
    }

    if (showLogoutDialog) {
        ModernLogoutDialog(
            onConfirm = {
                val intent = Intent(context, SignInActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                context.startActivity(intent)
                activity?.finish()
            },
            onDismiss = { showLogoutDialog = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModernTopBar(onLogoutClick: () -> Unit) {
    CenterAlignedTopAppBar(
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "Handmade Expo",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Admin Dashboard",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 12.sp
                )
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color(0xFF1E88E5)
        ),
        actions = {
            IconButton(onClick = onLogoutClick) {
                Icon(
                    Icons.Default.ExitToApp,
                    contentDescription = "Logout",
                    tint = Color.White
                )
            }
        }
    )
}

@Composable
fun ModernBottomNav(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    pendingCount: Int
) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            selected = selectedTab == 0,
            onClick = { onTabSelected(0) },
            icon = { Icon(Icons.Default.Dashboard, null) },
            label = { Text("Overview") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF1E88E5),
                selectedTextColor = Color(0xFF1E88E5),
                indicatorColor = Color(0xFFE3F2FD)
            )
        )
        NavigationBarItem(
            selected = selectedTab == 1,
            onClick = { onTabSelected(1) },
            icon = {
                if (pendingCount > 0) {
                    BadgedBox(
                        badge = {
                            Badge(
                                containerColor = Color(0xFFFF5722)
                            ) {
                                Text(pendingCount.toString())
                            }
                        }
                    ) {
                        Icon(Icons.Default.VerifiedUser, null)
                    }
                } else {
                    Icon(Icons.Default.VerifiedUser, null)
                }
            },
            label = { Text("Verify") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFFFF9800),
                selectedTextColor = Color(0xFFFF9800),
                indicatorColor = Color(0xFFFFF3E0)
            )
        )
        NavigationBarItem(
            selected = selectedTab == 2,
            onClick = { onTabSelected(2) },
            icon = { Icon(Icons.Default.People, null) },
            label = { Text("Users") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF9C27B0),
                selectedTextColor = Color(0xFF9C27B0),
                indicatorColor = Color(0xFFF3E5F5)
            )
        )
        NavigationBarItem(
            selected = selectedTab == 3,
            onClick = { onTabSelected(3) },
            icon = { Icon(Icons.Default.Inventory, null) },
            label = { Text("Products") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF4CAF50),
                selectedTextColor = Color(0xFF4CAF50),
                indicatorColor = Color(0xFFE8F5E9)
            )
        )
        NavigationBarItem(
            selected = selectedTab == 4,
            onClick = { onTabSelected(4) },
            icon = { Icon(Icons.Default.Report, null) },
            label = { Text("Reports") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFFF44336),
                selectedTextColor = Color(0xFFF44336),
                indicatorColor = Color(0xFFFFEBEE)
            )
        )
    }
}

@Composable
fun ModernOverviewScreen(
    viewModel: AdminViewModel,
    onUserClick: () -> Unit,
    onProductClick: () -> Unit,
    onVerifyClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        // Welcome Section
        WelcomeSection()

        Spacer(modifier = Modifier.height(24.dp))

        if (viewModel.isLoading) {
            LoadingSection()
        } else {
            // Quick Stats
            QuickStatsSection(viewModel, onUserClick, onProductClick)

            Spacer(modifier = Modifier.height(20.dp))

            // Pending Verifications Alert
            val totalPending = viewModel.pendingSellers.size + viewModel.pendingProducts.size
            if (totalPending > 0) {
                PendingVerificationCard(totalPending, onVerifyClick)
                Spacer(modifier = Modifier.height(20.dp))
            }

            // Detailed Stats
            DetailedStatsSection(viewModel, onUserClick)
        }
    }
}

@Composable
fun WelcomeSection() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1E88E5)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(
                        Color.White.copy(alpha = 0.2f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.AdminPanelSettings,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    "Welcome Back, Admin!",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "System Overview & Management",
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun LoadingSection() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(
                color = Color(0xFF1E88E5),
                strokeWidth = 3.dp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text("Loading dashboard data...", color = Color.Gray, fontSize = 14.sp)
        }
    }
}

@Composable
fun QuickStatsSection(
    viewModel: AdminViewModel,
    onUserClick: () -> Unit,
    onProductClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        QuickStatCard(
            icon = Icons.Default.People,
            count = (viewModel.sellers.size + viewModel.buyers.size).toString(),
            label = "Total Users",
            color = Color(0xFF9C27B0),
            modifier = Modifier.weight(1f).clickable { onUserClick() }
        )
        QuickStatCard(
            icon = Icons.Default.Inventory,
            count = viewModel.products.size.toString(),
            label = "Products",
            color = Color(0xFF4CAF50),
            modifier = Modifier.weight(1f).clickable { onProductClick() }
        )
    }
}

@Composable
fun QuickStatCard(
    icon: ImageVector,
    count: String,
    label: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(120.dp)
            .shadow(2.dp, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
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
                    modifier = Modifier.size(24.dp)
                )
            }
            Column {
                Text(
                    count,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF212121)
                )
                Text(
                    label,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun PendingVerificationCard(count: Int, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .shadow(2.dp, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFF3E0)
        ),
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
                    .size(48.dp)
                    .background(Color(0xFFFF9800), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Notifications,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Pending Verifications",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFFE65100)
                )
                Text(
                    "$count items need your attention",
                    fontSize = 13.sp,
                    color = Color(0xFF6D4C41)
                )
            }
            Icon(
                Icons.Default.ArrowForward,
                contentDescription = null,
                tint = Color(0xFFFF9800)
            )
        }
    }
}

@Composable
fun DetailedStatsSection(viewModel: AdminViewModel, onUserClick: () -> Unit) {
    Column {
        Text(
            "Detailed Statistics",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF212121)
        )
        Spacer(modifier = Modifier.height(12.dp))

        DetailedStatItem(
            icon = Icons.Default.Store,
            label = "Sellers",
            count = viewModel.sellers.size.toString(),
            color = Color(0xFFFF9800),
            onClick = onUserClick
        )
        Spacer(modifier = Modifier.height(8.dp))

        DetailedStatItem(
            icon = Icons.Default.ShoppingCart,
            label = "Buyers",
            count = viewModel.buyers.size.toString(),
            color = Color(0xFF4CAF50),
            onClick = onUserClick
        )
    }
}

@Composable
fun DetailedStatItem(
    icon: ImageVector,
    label: String,
    count: String,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .shadow(1.dp, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
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
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                label,
                fontSize = 15.sp,
                color = Color(0xFF424242),
                modifier = Modifier.weight(1f)
            )
            Text(
                count,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}

@Composable
fun ModernVerificationHub(viewModel: AdminViewModel) {
    var selectedTab by remember { mutableIntStateOf(0) }

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.White,
            contentColor = Color(0xFF1E88E5),
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = Color(0xFF1E88E5)
                )
            }
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text("Sellers", fontWeight = FontWeight.Medium)
                        if (viewModel.pendingSellers.isNotEmpty()) {
                            Spacer(Modifier.width(8.dp))
                            Badge(
                                containerColor = Color(0xFFFF5722)
                            ) {
                                Text(viewModel.pendingSellers.size.toString())
                            }
                        }
                    }
                }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text("Products", fontWeight = FontWeight.Medium)
                        if (viewModel.pendingProducts.isNotEmpty()) {
                            Spacer(Modifier.width(8.dp))
                            Badge(
                                containerColor = Color(0xFFFF5722)
                            ) {
                                Text(viewModel.pendingProducts.size.toString())
                            }
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
fun ModernLogoutDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                Icons.Default.ExitToApp,
                contentDescription = null,
                tint = Color(0xFF1E88E5),
                modifier = Modifier.size(32.dp)
            )
        },
        title = {
            Text(
                "Confirm Logout",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        },
        text = {
            Text(
                "Are you sure you want to logout from the Admin Dashboard?",
                fontSize = 14.sp,
                color = Color.Gray
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFF44336)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Logout")
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Cancel", color = Color(0xFF757575))
            }
        },
        shape = RoundedCornerShape(16.dp)
    )
}
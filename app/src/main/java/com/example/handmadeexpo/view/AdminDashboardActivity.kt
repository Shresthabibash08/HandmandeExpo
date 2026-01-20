package com.example.handmadeexpo.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.handmadeexpo.viewmodel.AdminViewModel

class AdminDashboardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { AdminDashboardScreen() }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(adminViewModel: AdminViewModel = viewModel()) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val activity = context as? Activity

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Admin Dashboard", color = Color.White) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF4CAF50)
                ),
                actions = {
                    IconButton(onClick = { showLogoutDialog = true }) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Logout", tint = Color.White)
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(selected = selectedTab == 0, onClick = { selectedTab = 0 }, label = { Text("Home") }, icon = { Icon(Icons.Default.Home, null) })
                NavigationBarItem(selected = selectedTab == 1, onClick = { selectedTab = 1 }, label = { Text("Users") }, icon = { Icon(Icons.Default.Person, null) })
                NavigationBarItem(selected = selectedTab == 2, onClick = { selectedTab = 2 }, label = { Text("Products") }, icon = { Icon(Icons.Default.ShoppingBag, null) })
                NavigationBarItem(selected = selectedTab == 3, onClick = { selectedTab = 3 }, label = { Text("Complaints") }, icon = { Icon(Icons.Default.Warning, null) })
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            when (selectedTab) {
                0 -> AdminOverview(
                    adminViewModel,
                    onUserClick = { selectedTab = 1 },
                    onProductClick = { selectedTab = 2 }
                )
                1 -> AdminUserListScreen(adminViewModel)
                2 -> AdminProductListScreen(adminViewModel)
                3 -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Complaints Section: Coming Soon", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                }
            }
        }
    }

    if (showLogoutDialog) {
        AdminLogoutConfirmationDialog(
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

@Composable
fun AdminLogoutConfirmationDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Confirm Logout") },
        text = { Text("Are you sure you want to logout from Admin Dashboard?") },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Text("Logout")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun AdminOverview(viewModel: AdminViewModel, onUserClick: () -> Unit, onProductClick: () -> Unit) {
    val totalUsers = viewModel.sellers.size + viewModel.buyers.size

    Column(modifier = Modifier.padding(16.dp)) {
        Text("System Statistics", fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Spacer(Modifier.height(16.dp))

        if (viewModel.isLoading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth(), color = Color(0xFF4CAF50))
        } else {
            // All cards are now clickable as requested
            DashboardStatCard("Total Users", totalUsers.toString(), Color(0xFF6200EE), Modifier.fillMaxWidth().clickable { onUserClick() })
            Spacer(Modifier.height(8.dp))

            DashboardStatCard("Total Products", viewModel.products.size.toString(), Color(0xFF2196F3), Modifier.fillMaxWidth().clickable { onProductClick() })
            Spacer(Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                DashboardStatCard("Sellers", viewModel.sellers.size.toString(), Color(0xFFFF9800), Modifier.weight(1f).clickable { onUserClick() })
                DashboardStatCard("Buyers", viewModel.buyers.size.toString(), Color(0xFF4CAF50), Modifier.weight(1f).clickable { onUserClick() })
            }
        }
    }
}

@Composable
fun DashboardStatCard(label: String, count: String, color: Color, modifier: Modifier) {
    Card(
        modifier = modifier.height(100.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f))
    ) {
        Column(Modifier.fillMaxSize(), Arrangement.Center, Alignment.CenterHorizontally) {
            Text(count, fontSize = 32.sp, fontWeight = FontWeight.Bold, color = color)
            Text(label, fontSize = 14.sp)
        }
    }
}
package com.example.handmadeexpo.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
        setContent { AdminDashboardScreen() }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(adminViewModel: AdminViewModel = viewModel()) {
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Admin Dashboard", color = Color.White) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color(0xFF6200EE))
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(selected = selectedTab == 0, onClick = { selectedTab = 0 }, label = { Text("Home") }, icon = { Icon(Icons.Default.Home, null) })
                NavigationBarItem(selected = selectedTab == 1, onClick = { selectedTab = 1 }, label = { Text("Complaints") }, icon = { Icon(Icons.Default.Warning, null) })
                NavigationBarItem(selected = selectedTab == 2, onClick = { selectedTab = 2 }, label = { Text("Users") }, icon = { Icon(Icons.Default.Person, null) })
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            when (selectedTab) {
                0 -> AdminOverview(adminViewModel) { selectedTab = 2 }
                1 -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Coming Soon") }
                2 -> AdminUserListScreen(adminViewModel)
            }
        }
    }
}

@Composable
fun AdminOverview(viewModel: AdminViewModel, onRedirect: () -> Unit) {
    val totalUsers = viewModel.sellers.size + viewModel.buyers.size

    Column(modifier = Modifier.padding(16.dp)) {
        OutlinedTextField(
            value = viewModel.searchQuery,
            onValueChange = { viewModel.searchQuery = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search...") },
            leadingIcon = { Icon(Icons.Default.Search, null) }
        )
        Spacer(Modifier.height(20.dp))
        Text("Statistics", fontWeight = FontWeight.Bold, fontSize = 20.sp)

        if (viewModel.isSellersLoading || viewModel.isBuyersLoading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth().padding(top = 10.dp), color = Color(0xFF6200EE))
        } else {
            StatCard("Total Users", totalUsers.toString(), Modifier.fillMaxWidth().padding(vertical = 8.dp), Color(0xFF6200EE))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatCard("Sellers", viewModel.sellers.size.toString(), Modifier.weight(1f), Color(0xFFFF9800))
                StatCard("Buyers", viewModel.buyers.size.toString(), Modifier.weight(1f), Color(0xFF4CAF50))
            }
        }
    }
}

@Composable
fun StatCard(label: String, count: String, modifier: Modifier, color: Color) {
    Card(modifier = modifier.height(100.dp), colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f))) {
        Column(Modifier.fillMaxSize(), Arrangement.Center, Alignment.CenterHorizontally) {
            Text(count, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = color)
            Text(label, fontSize = 14.sp)
        }
    }
}
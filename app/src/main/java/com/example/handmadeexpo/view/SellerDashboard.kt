package com.example.handmadeexpo.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.example.handmadeexpo.R
import com.example.handmadeexpo.repo.SellerRepoImpl
import com.example.handmadeexpo.ui.theme.MainColor
import com.example.handmadeexpo.viewmodel.SellerViewModel

class SellerDashboard : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Capture the sellerId from the login intent
        val sellerId = intent.getStringExtra("userId") ?: ""

        setContent {
            SellerDashboardBody(sellerId)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellerDashboardBody(sellerId: String) {

    data class NavItem(val icon: Int, val label: String)

    val listItems = listOf(
        NavItem(R.drawable.outline_home_24, "Home"),
        NavItem(R.drawable.baseline_inventory_24, "Inventory"),
        NavItem(R.drawable.outline_contacts_product_24, "Profile")
    )

    // State management
    var selectedIndex by remember { mutableStateOf(0) }
    var editing by remember { mutableStateOf(false) }
    
    // ViewModel initialization
    val repo = remember { SellerRepoImpl() }
    val viewModel = remember { SellerViewModel(repo) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MainColor,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                ),
                title = { Text("HandMade Expo", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = { /* Handle navigation drawer or back */ }) {
                        Icon(
                            painter = painterResource(R.drawable.outline_arrow_back_ios_24),
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* Notifications */ }) {
                        Icon(
                            painter = painterResource(R.drawable.outline_home_24), // Replace with notification icon
                            contentDescription = "Notifications"
                        )
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp
            ) {
                listItems.forEachIndexed { index, item ->
                    NavigationBarItem(
                        icon = { Icon(painterResource(item.icon), contentDescription = item.label) },
                        label = { Text(item.label) },
                        selected = selectedIndex == index,
                        onClick = { 
                            selectedIndex = index 
                            editing = false // Reset editing state when switching tabs
                        }
                    )
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (selectedIndex) {
                0 -> SellerHomeScreen(sellerId)
                1 -> InvetoryScreen(sellerId)
                2 -> {
                    if (editing) {
                        EditSellerProfileScreen(
                            viewModel = viewModel,
                            onBack = { editing = false }
                        )
                    } else {
                        SellerProfileScreen(
                            sellerId = sellerId,
                            viewModel = viewModel,
                            onEditProfileClick = { editing = true }
                        )
                    }
                }
            }
        }
    }
}
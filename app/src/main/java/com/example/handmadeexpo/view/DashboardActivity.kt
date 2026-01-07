package com.example.handmadeexpo.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.handmadeexpo.R
import com.example.handmadeexpo.repo.BuyerRepoImpl
import com.example.handmadeexpo.repo.CartRepoImpl
import com.example.handmadeexpo.ui.theme.MainColor
import com.example.handmadeexpo.ui.theme.White12
import com.example.handmadeexpo.viewmodel.BuyerViewModel
import com.example.handmadeexpo.viewmodel.CartViewModel
import com.google.firebase.auth.FirebaseAuth

class DashboardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DashboardBody()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardBody() {
    // --- 1. DATA AND NAVIGATION STATE ---
    data class NavItem(val icon: Int, val label: String)

    val listItems = listOf(
        NavItem(R.drawable.outline_home_24, "Home"),
        NavItem(R.drawable.outline_search_24, "Search"),
        NavItem(R.drawable.baseline_shopping_cart_24, "Cart"),
        NavItem(R.drawable.outline_contacts_product_24, "Profile")
    )

    var selectedIndex by remember { mutableStateOf(0) }
    var editing by remember { mutableStateOf(false) }

    // --- 2. INITIALIZE REPOS AND VIEWMODELS ---
    // User Profile logic
    val buyerRepo = remember { BuyerRepoImpl() }
    val buyerViewModel = remember { BuyerViewModel(buyerRepo) }

    // Cart logic (The fix)
    val cartRepo = remember { CartRepoImpl() }
    val cartViewModel = remember { CartViewModel(cartRepo) }

    // Get Current User ID from Firebase
    val currentUserId = remember { FirebaseAuth.getInstance().currentUser?.uid ?: "" }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MainColor,
                    titleContentColor = White12
                ),
                title = {
                    Text(
                        "Handmade Expo",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(
                            painter = painterResource(R.drawable.outline_more_horiz_24),
                            contentDescription = null,
                            tint = White12
                        )
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                listItems.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedIndex == index,
                        onClick = {
                            selectedIndex = index
                            editing = false // reset when switching tabs
                        },
                        icon = {
                            Icon(
                                painter = painterResource(item.icon),
                                contentDescription = null
                            )
                        },
                        label = { Text(item.label) }
                    )
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (selectedIndex) {
                0 -> HomeScreen()
                1 -> Text("Search Screen", modifier = Modifier.padding(16.dp))
                2 -> {
                    // Pass the initialized ViewModel and the current User ID
                    CartScreen(
                        cartViewModel = cartViewModel,
                        currentUserId = currentUserId
                    )
                }
                3 -> {
                    if (editing) {
                        EditBuyerProfileScreen(
                            viewModel = buyerViewModel,
                            onBack = { editing = false }
                        )
                    } else {
                        BuyerProfileScreen(
                            viewModel = buyerViewModel,
                            onEditClick = { editing = true }
                        )
                    }
                }
            }
        }
    }
}
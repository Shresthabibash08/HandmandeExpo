package com.example.handmadeexpo.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.handmadeexpo.repo.BuyerRepoImpl
import com.example.handmadeexpo.ui.theme.MainColor
import com.example.handmadeexpo.ui.theme.White12
import com.example.handmadeexpo.viewmodel.BuyerViewModel
import com.google.firebase.auth.FirebaseAuth

class DashboardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Get the real Firebase UID.
        // Logic Fix: Default to empty string or handle null to prevent crashes
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        setContent {
            DashboardBody(currentUserId)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardBody(userId: String) {
    data class NavItem(val icon: ImageVector, val label: String)
    val listItems = listOf(
        NavItem(Icons.Default.Home, "Home"),
        NavItem(Icons.AutoMirrored.Filled.Chat, "Inbox"),
        NavItem(Icons.Default.ShoppingCart, "Cart"),
        NavItem(Icons.Default.Person, "Profile")
    )

    var selectedIndex by remember { mutableIntStateOf(0) }
    var editing by remember { mutableStateOf(false) }
    var showAllSellers by remember { mutableStateOf(false) }

    // Triple stores: (ChatID, SellerID, SellerName)
    // This state allows the name to persist when navigating into a ChatScreen
    var activeChatData by remember { mutableStateOf<Triple<String, String, String>?>(null) }

    val repo = remember { BuyerRepoImpl() }
    val viewModel = remember { BuyerViewModel(repo) }

    // Handle back button for the Chat flow
    BackHandler(enabled = (selectedIndex == 1 && (activeChatData != null || showAllSellers))) {
        if (activeChatData != null) {
            activeChatData = null
        } else if (showAllSellers) {
            showAllSellers = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MainColor,
                    titleContentColor = White12
                ),
                title = {
                    val title = when {
                        selectedIndex == 1 && activeChatData != null -> activeChatData!!.third // Shows Seller Name
                        selectedIndex == 1 && showAllSellers -> "Select Seller"
                        selectedIndex == 1 -> "Messages"
                        selectedIndex == 2 -> "My Cart"
                        selectedIndex == 3 -> "Profile"
                        else -> "Handmade Expo"
                    }
                    Text(title, fontSize = 20.sp, fontWeight = FontWeight.Bold)
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
                            showAllSellers = false
                            // Important: Clear chat data if navigating away from Inbox
                            if (index != 1) activeChatData = null
                        },
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) }
                    )
                }
            }
        },
        floatingActionButton = {
            if (selectedIndex == 1 && activeChatData == null && !showAllSellers) {
                FloatingActionButton(
                    onClick = { showAllSellers = true },
                    containerColor = Color(0xFFE65100),
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.Add, contentDescription = "New Chat")
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

                1 -> {
                    when {
                        // 1. Specific Chat Screen
                        activeChatData != null -> {
                            ChatScreen(
                                chatId = activeChatData!!.first,
                                sellerId = activeChatData!!.second,
                                sellerName = activeChatData!!.third,
                                currentUserId = userId,
                                onBackClick = { activeChatData = null }
                            )
                        }
                        // 2. New Chat Selection
                        showAllSellers -> {
                            AllSellersListScreen(userId) { chatId, sellerId, sellerName ->
                                activeChatData = Triple(chatId, sellerId, sellerName)
                                showAllSellers = false
                            }
                        }
                        // 3. Main Inbox List
                        else -> {
                            // Logic Fix: Ensure this composable fetches names from Firebase
                            BuyerChatListScreen(userId) { chatId, sellerId, sellerName ->
                                activeChatData = Triple(chatId, sellerId, sellerName)
                            }
                        }
                    }
                }

                2 -> CartScreen()

                3 -> {
                    if (editing) {
                        EditBuyerProfileScreen(viewModel, onBack = { editing = false })
                    } else {
                        BuyerProfileScreen(viewModel, onEditClick = { editing = true })
                    }
                }
            }
        }
    }
}
package com.example.handmadeexpo.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.handmadeexpo.R
import com.example.handmadeexpo.repo.SellerRepoImpl
import com.example.handmadeexpo.ui.theme.MainColor
import com.example.handmadeexpo.viewmodel.SellerViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SellerDashboard : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val sellerId = intent.getStringExtra("userId") ?: ""

        setContent {
            SellerDashboardBody(sellerId)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellerDashboardBody(sellerId: String) {
    // --- 1. STATE DEFINITIONS (Must be at the top to fix Unresolved Reference) ---
    var selectedIndex by remember { mutableIntStateOf(0) }
    var editing by remember { mutableStateOf(false) }

    // Triple stores: (ChatID, BuyerID, BuyerName)
    var activeChatData by remember { mutableStateOf<Triple<String, String, String>?>(null) }

    val inboxRef = remember { FirebaseDatabase.getInstance().getReference("seller_inbox").child(sellerId) }
    var chatCount by remember { mutableStateOf(0) }

    // --- 2. FIREBASE LISTENER ---
    LaunchedEffect(sellerId) {
        inboxRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                chatCount = snapshot.childrenCount.toInt()
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    // --- 3. NAVIGATION LOGIC ---
    val repo = remember { SellerRepoImpl() }
    val viewModel = remember { SellerViewModel(repo) }

    data class NavItem(val icon: ImageVector, val label: String)
    val listItems = listOf(
        NavItem(Icons.Default.Home, "Home"),
        NavItem(Icons.Default.Inventory, "Inventory"),
        NavItem(Icons.Default.Chat, "Chats"),
        NavItem(Icons.Default.Person, "Profile")
    )

    // Handle back button specifically for closing a chat thread
    BackHandler(enabled = (selectedIndex == 2 && activeChatData != null)) {
        activeChatData = null
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MainColor,
                    titleContentColor = Color.White
                ),
                title = {
                    val titleText = when {
                        selectedIndex == 2 && activeChatData != null -> activeChatData!!.third // Show Buyer Name
                        selectedIndex == 0 -> "HandMade Expo"
                        selectedIndex == 1 -> "My Inventory"
                        selectedIndex == 2 -> "Messages"
                        else -> "Profile"
                    }
                    Text(titleText, style = MaterialTheme.typography.titleLarge)
                },
                navigationIcon = {
                    // Show back arrow ONLY when inside a chat
                    if (selectedIndex == 2 && activeChatData != null) {
                        IconButton(onClick = { activeChatData = null }) {
                            Icon(
                                painter = painterResource(R.drawable.outline_arrow_back_ios_24),
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }
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
                        icon = {
                            BadgedBox(
                                badge = {
                                    if (index == 2 && chatCount > 0) {
                                        Badge { Text(chatCount.toString()) }
                                    }
                                }
                            ) {
                                Icon(item.icon, contentDescription = item.label)
                            }
                        },
                        label = { Text(item.label) },
                        selected = selectedIndex == index,
                        onClick = {
                            selectedIndex = index
                            editing = false
                            // Reset chat state if navigating away from the chat tab
                            if (index != 2) activeChatData = null
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MainColor,
                            selectedTextColor = MainColor,
                            indicatorColor = Color(0xFFFFE0B2)
                        )
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
            // --- 4. CONTENT SWITCHER ---
            when (selectedIndex) {
                0 -> SellerHomeScreen(sellerId)
                1 -> InvetoryScreen(sellerId)
                2 -> {
                    // This section handles both the List and the Message Thread
                    if (activeChatData != null) {
                        // FIX: Calling the View inside SellerChatListScreen.kt
                        SellerReplyView(
                            chatId = activeChatData!!.first,
                            buyerId = activeChatData!!.second,
                            sellerId = sellerId
                        )
                    } else {
                        // FIX: Providing the onChatSelected parameter
                        ChatListContent(sellerId = sellerId) { chatId, buyerId, buyerName ->
                            activeChatData = Triple(chatId, buyerId, buyerName)
                        }
                    }
                }
                3 -> {
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
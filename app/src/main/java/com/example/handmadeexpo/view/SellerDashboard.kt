package com.example.handmadeexpo.view

import android.os.Bundle
import androidx.activity.ComponentActivity
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

    // 1. Firebase Listener for Inbox Count
    // This allows the badge to update in real-time when a buyer initiates a chat
    val inboxRef = remember { FirebaseDatabase.getInstance().getReference("seller_inbox").child(sellerId) }
    var chatCount by remember { mutableStateOf(0) }

    LaunchedEffect(sellerId) {
        inboxRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                chatCount = snapshot.childrenCount.toInt()
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    // 2. Navigation Item Definition
    data class NavItem(val icon: ImageVector, val label: String)
    val listItems = listOf(
        NavItem(Icons.Default.Home, "Home"),
        NavItem(Icons.Default.Inventory, "Inventory"),
        NavItem(Icons.Default.Chat, "Chats"),
        NavItem(Icons.Default.Person, "Profile")
    )

    // 3. State management
    var selectedIndex by remember { mutableStateOf(0) }
    var editing by remember { mutableStateOf(false) }

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
                title = {
                    val titleText = when (selectedIndex) {
                        0 -> "HandMade Expo"
                        1 -> "My Inventory"
                        2 -> "Messages"
                        else -> "Profile"
                    }
                    Text(titleText, style = MaterialTheme.typography.titleLarge)
                },
                navigationIcon = {
                    IconButton(onClick = { /* Handle navigation drawer or back */ }) {
                        Icon(
                            painter = painterResource(R.drawable.outline_arrow_back_ios_24),
                            contentDescription = "Back"
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
                        icon = {
                            BadgedBox(
                                badge = {
                                    // Show badge only on index 2 (Chats) if there are active inquiries
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
                            editing = false // Reset profile editing state
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
            // 4. Content Switcher
            when (selectedIndex) {
                0 -> SellerHomeScreen(sellerId)
                1 -> InventoryScreen(sellerId)
                2 -> {
                    // This screen only shows if a buyer has initiated chat
                    SellerChatListScreen(sellerId = sellerId)
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
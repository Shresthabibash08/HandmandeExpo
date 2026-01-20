package com.example.handmadeexpo.view

import android.app.Activity
import android.content.Intent
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
import androidx.compose.ui.platform.LocalContext
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
    // --- 1. STATE DEFINITIONS ---
    var selectedIndex by remember { mutableIntStateOf(0) }
    var editing by remember { mutableStateOf(false) }
    var changingPassword by remember { mutableStateOf(false) }
    var chatCount by remember { mutableStateOf(0) }

    // NEW: State to hold the seller's name fetched from Firebase
    var sellerName by remember { mutableStateOf("Seller") }

    // Triple stores: (ChatID, BuyerID, BuyerName)
    var activeChatData by remember { mutableStateOf<Triple<String, String, String>?>(null) }

    val context = LocalContext.current
    val activity = context as? Activity
    val repo = remember { SellerRepoImpl() }
    val viewModel = remember { SellerViewModel(repo) }

    // --- 2. FIREBASE LISTENERS ---

    // Fetch Seller Name from Firebase to use in Bargain Logic
    LaunchedEffect(sellerId) {
        FirebaseDatabase.getInstance().getReference("Sellers").child(sellerId).child("name")
            .get().addOnSuccessListener { snapshot ->
                sellerName = snapshot.value?.toString() ?: "Seller"
            }
    }

    // Badge Listener for Incoming Chats
    val inboxRef = remember { FirebaseDatabase.getInstance().getReference("seller_inbox").child(sellerId) }
    LaunchedEffect(sellerId) {
        inboxRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                chatCount = snapshot.childrenCount.toInt()
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    // --- 3. NAVIGATION ITEMS ---
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
                        selectedIndex == 2 && activeChatData != null -> activeChatData!!.third
                        selectedIndex == 0 -> "HandMade Expo"
                        selectedIndex == 1 -> "My Inventory"
                        selectedIndex == 2 -> "Messages"
                        else -> "Profile"
                    }
                    Text(titleText, style = MaterialTheme.typography.titleLarge)
                },
                navigationIcon = {
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
                            changingPassword = false
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
            when (selectedIndex) {
                // FIXED: Now passing both sellerId and the fetched sellerName
                0 -> SellerHomeScreen(sellerId = sellerId, sellerName = sellerName)

                1 -> InventoryScreen(sellerId)
                2 -> {
                    if (activeChatData != null) {
                        SellerReplyView(
                            chatId = activeChatData!!.first,
                            buyerId = activeChatData!!.second,
                            sellerId = sellerId
                        )
                    } else {
                        ChatListContent(sellerId = sellerId) { chatId, buyerId, buyerName ->
                            activeChatData = Triple(chatId, buyerId, buyerName)
                        }
                    }
                }
                3 -> {
                    when {
                        changingPassword -> {
                            SellerChangePasswordScreen(
                                viewModel = viewModel,
                                onBackClick = { changingPassword = false },
                                onPasswordChanged = { changingPassword = false }
                            )
                        }
                        editing -> {
                            EditSellerProfileScreen(
                                viewModel = viewModel,
                                onBack = { editing = false }
                            )
                        }
                        else -> {
                            SellerProfileScreen(
                                sellerId = sellerId,
                                viewModel = viewModel,
                                onEditProfileClick = { editing = true },
                                onChangePasswordClick = { changingPassword = true },
                                onLogoutSuccess = {
                                    val intent = Intent(context, SignInActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    context.startActivity(intent)
                                    activity?.finish()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
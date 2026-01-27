package com.example.handmadeexpo.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.handmadeexpo.repo.SellerRepoImpl
import com.example.handmadeexpo.ui.theme.MainColor
import com.example.handmadeexpo.ui.theme.White12
import com.example.handmadeexpo.viewmodel.SellerViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SellerDashboard : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Get the current logged-in user ID safely
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        setContent {
            SellerDashboardBody(currentUserId)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellerDashboardBody(sellerId: String) {
    val context = LocalContext.current
    val activity = context as? Activity

    // --- State Definitions ---
    var selectedIndex by remember { mutableIntStateOf(0) }
    var editing by remember { mutableStateOf(false) }
    var changingPassword by remember { mutableStateOf(false) }
    var chatCount by remember { mutableIntStateOf(0) }

    // State to hold the seller's name fetched from Firebase
    var sellerName by remember { mutableStateOf("Seller") }

    // Triple stores: (ChatID, BuyerID, BuyerName)
    var activeChatData by remember { mutableStateOf<Triple<String, String, String>?>(null) }

    // --- NEW: Reporting State ---
    var reportingChatUserId by remember { mutableStateOf<String?>(null) }

    val sellerRepo = remember { SellerRepoImpl() }
    val sellerViewModel = remember { SellerViewModel(sellerRepo) }

    // --- FIREBASE LISTENERS ---

    // 1. Fetch Seller Name (Needed for Bargain/Home logic)
    LaunchedEffect(sellerId) {
        if (sellerId.isNotEmpty()) {
            FirebaseDatabase.getInstance().getReference("Sellers").child(sellerId).child("name")
                .get().addOnSuccessListener { snapshot ->
                    sellerName = snapshot.value?.toString() ?: "Seller"
                }
        }
    }

    // 2. Badge Listener for Incoming Chats
    val inboxRef = remember { FirebaseDatabase.getInstance().getReference("seller_inbox").child(sellerId) }
    LaunchedEffect(sellerId) {
        if (sellerId.isNotEmpty()) {
            val listener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    chatCount = snapshot.childrenCount.toInt()
                }
                override fun onCancelled(error: DatabaseError) {}
            }
            inboxRef.addValueEventListener(listener)
        }
    }

    // --- Navigation Items ---
    data class NavItem(val icon: ImageVector, val label: String)
    val listItems = listOf(
        NavItem(Icons.Default.Home, "Home"),
        NavItem(Icons.AutoMirrored.Filled.List, "Inventory"),
        NavItem(Icons.AutoMirrored.Filled.Chat, "Chats"),
        NavItem(Icons.Default.Person, "Profile")
    )

    // Handle Back Press
    // Includes logic to close report screen if open
    BackHandler(enabled = activeChatData != null || editing || changingPassword || reportingChatUserId != null) {
        when {
            reportingChatUserId != null -> reportingChatUserId = null
            activeChatData != null -> activeChatData = null
            editing -> editing = false
            changingPassword -> changingPassword = false
        }
    }

    Scaffold(
        topBar = {
            // Hide TopBar if reporting screen is active
            if (reportingChatUserId == null) {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MainColor,
                        titleContentColor = White12
                    ),
                    title = {
                        val titleText = when {
                            selectedIndex == 2 && activeChatData != null -> activeChatData!!.third
                            selectedIndex == 0 -> "Seller Dashboard"
                            selectedIndex == 1 -> "My Inventory"
                            selectedIndex == 2 -> "Messages"
                            else -> "My Profile"
                        }
                        Text(titleText, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                )
            }
        },
        bottomBar = {
            // Hide BottomBar if chat is open, editing profile, or reporting
            if (activeChatData == null && !editing && !changingPassword && reportingChatUserId == null) {
                NavigationBar {
                    listItems.forEachIndexed { index, item ->
                        NavigationBarItem(
                            selected = selectedIndex == index,
                            onClick = {
                                selectedIndex = index
                                editing = false
                                changingPassword = false
                                reportingChatUserId = null
                                if (index != 2) activeChatData = null
                            },
                            icon = {
                                if (index == 2 && chatCount > 0) {
                                    BadgedBox(badge = { Badge { Text("$chatCount") } }) {
                                        Icon(item.icon, contentDescription = item.label)
                                    }
                                } else {
                                    Icon(item.icon, contentDescription = item.label)
                                }
                            },
                            label = { Text(item.label) }
                        )
                    }
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            // --- FULL SCREEN REPORT OVERLAY ---
            if (reportingChatUserId != null) {
                ReportScreen(
                    reportTargetId = reportingChatUserId!!,
                    isReportingSeller = false, // Seller is reporting a Buyer
                    onBackClick = { reportingChatUserId = null }
                )
            } else {
                // --- MAIN CONTENT ---
                when (selectedIndex) {
                    0 -> SellerHomeScreen(sellerId = sellerId, sellerName = sellerName)

                    1 -> InventoryScreen(sellerId)

                    2 -> {
                        if (activeChatData != null) {
                            // *** FIXED CHAT SCREEN CALL ***
                            ChatScreen(
                                chatId = activeChatData!!.first,
                                sellerId = activeChatData!!.second, // This is the Buyer ID
                                sellerName = activeChatData!!.third, // This is the Buyer Name
                                currentUserId = sellerId,            // I am the Seller
                                onBackClick = { activeChatData = null },
                                isReportingSeller = false, // FALSE because I am a Seller reporting a Buyer

                                // --- Callback triggers the Report Screen ---
                                onReportClick = {
                                    reportingChatUserId = activeChatData!!.second
                                }
                            )
                        } else {
                            SellerChatListScreen(sellerId) { chatId, buyerId, buyerName ->
                                activeChatData = Triple(chatId, buyerId, buyerName)
                            }
                        }
                    }
                    3 -> {
                        when {
                            changingPassword -> {
                                SellerChangePasswordScreen(
                                    viewModel = sellerViewModel,
                                    onBackClick = { changingPassword = false },
                                    onPasswordChanged = { changingPassword = false }
                                )
                            }
                            editing -> {
                                EditSellerProfileScreen(
                                    viewModel = sellerViewModel,
                                    onBack = { editing = false }
                                )
                            }
                            else -> {
                                SellerProfileScreen(
                                    sellerId = sellerId,
                                    onEditProfileClick = { editing = true },
                                    onChangePasswordClick = { changingPassword = true },
                                    onLogoutSuccess = {
                                        val intent = Intent(context, SignInActivity::class.java)
                                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                        context.startActivity(intent)
                                        activity?.finish()
                                    },
                                    viewModel = sellerViewModel
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
package com.example.handmadeexpo.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.handmadeexpo.repo.SellerRepoImpl
import com.example.handmadeexpo.viewmodel.SellerViewModel
import com.example.handmadeexpo.viewmodel.SellerViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SellerDashboard : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        setContent {
            ModernSellerDashboardBody(currentUserId)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModernSellerDashboardBody(sellerId: String) {
    val context = LocalContext.current
    val activity = context as? Activity

    var selectedIndex by remember { mutableIntStateOf(0) }
    var editing by remember { mutableStateOf(false) }
    var changingPassword by remember { mutableStateOf(false) }
    var chatCount by remember { mutableIntStateOf(0) }
    var sellerName by remember { mutableStateOf("Seller") }
    var activeChatData by remember { mutableStateOf<Triple<String, String, String>?>(null) }

    // State for Reporting Buyer
    var reportBuyerId by remember { mutableStateOf<String?>(null) }

    val sellerViewModel: SellerViewModel = viewModel(
        factory = SellerViewModelFactory(SellerRepoImpl())
    )

    // Fetch Seller Name (Corrected Node)
    LaunchedEffect(sellerId) {
        if (sellerId.isNotEmpty()) {
            // Fetch 'shopName' from 'Seller' node
            FirebaseDatabase.getInstance().getReference("Seller").child(sellerId).child("shopName")
                .get().addOnSuccessListener { snapshot ->
                    val name = snapshot.value?.toString()
                    sellerName = if (!name.isNullOrEmpty()) name else "Seller"
                }
        }
    }

    // Badge Listener for Incoming Chats
    DisposableEffect(sellerId) {
        if (sellerId.isNotEmpty()) {
            val inboxRef = FirebaseDatabase.getInstance().getReference("seller_inbox").child(sellerId)
            val listener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    chatCount = snapshot.childrenCount.toInt()
                }
                override fun onCancelled(error: DatabaseError) {}
            }
            inboxRef.addValueEventListener(listener)
            onDispose { inboxRef.removeEventListener(listener) }
        } else {
            onDispose { }
        }
    }

    data class NavItem(val icon: ImageVector, val label: String, val color: Color)
    val listItems = listOf(
        NavItem(Icons.Default.Home, "Home", Color(0xFF1E88E5)),
        NavItem(Icons.AutoMirrored.Filled.List, "Inventory", Color(0xFF4CAF50)),
        NavItem(Icons.AutoMirrored.Filled.Chat, "Chats", Color(0xFFFF9800)),
        NavItem(Icons.Default.Person, "Profile", Color(0xFF9C27B0))
    )

    val isChatActive = selectedIndex == 2 && activeChatData != null
    val isReporting = reportBuyerId != null
    val isProfileActive = editing || changingPassword

    BackHandler(enabled = isChatActive || isReporting || isProfileActive) {
        when {
            reportBuyerId != null -> reportBuyerId = null
            activeChatData != null -> activeChatData = null
            editing -> editing = false
            changingPassword -> changingPassword = false
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF4CAF50) // Changed to Green
                ),
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        val titleText = when {
                            reportBuyerId != null -> "Report Buyer"
                            selectedIndex == 2 && activeChatData != null -> activeChatData!!.third
                            selectedIndex == 0 -> "Seller Dashboard"
                            selectedIndex == 1 -> "My Inventory"
                            selectedIndex == 2 -> "Messages"
                            else -> "My Profile"
                        }
                        Text(
                            titleText,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            )
        },
        bottomBar = {
            if (activeChatData == null && reportBuyerId == null && !editing && !changingPassword) {
                NavigationBar(
                    containerColor = Color.White,
                    tonalElevation = 8.dp
                ) {
                    listItems.forEachIndexed { index, item ->
                        NavigationBarItem(
                            selected = selectedIndex == index,
                            onClick = {
                                selectedIndex = index
                                editing = false
                                changingPassword = false
                                reportBuyerId = null
                                if (index != 2) activeChatData = null
                            },
                            icon = {
                                if (index == 2 && chatCount > 0) {
                                    BadgedBox(
                                        badge = {
                                            Badge(containerColor = Color(0xFFFF5722)) {
                                                Text("$chatCount")
                                            }
                                        }
                                    ) {
                                        Icon(item.icon, contentDescription = item.label)
                                    }
                                } else {
                                    Icon(item.icon, contentDescription = item.label)
                                }
                            },
                            label = { Text(item.label) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = item.color,
                                selectedTextColor = item.color,
                                indicatorColor = item.color.copy(alpha = 0.15f)
                            )
                        )
                    }
                }
            }
        },
        containerColor = Color(0xFFF5F7FA)
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when {
                reportBuyerId != null -> {
                    ReportScreen(
                        targetId = reportBuyerId!!,
                        isReportingSeller = false, // Seller reporting Buyer
                        onBackClick = { reportBuyerId = null }
                    )
                }
                else -> when (selectedIndex) {
                    0 -> SellerHomeScreen(sellerId = sellerId, sellerName = sellerName)
                    1 -> InventoryScreen(sellerId)
                    2 -> {
                        if (activeChatData != null) {
                            ChatScreen(
                                chatId = activeChatData!!.first,
                                sellerId = activeChatData!!.second, // Other person's ID (Buyer)
                                sellerName = activeChatData!!.third,
                                currentUserId = sellerId,
                                onBackClick = { activeChatData = null },
                                isReportingSeller = false, // Seller viewing Buyer
                                onReportClick = { reportBuyerId = activeChatData!!.second }
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
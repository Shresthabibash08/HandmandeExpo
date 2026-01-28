package com.example.handmadeexpo.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.*
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
import com.example.handmadeexpo.repo.BuyerRepoImpl
import com.example.handmadeexpo.repo.CartRepoImpl
import com.example.handmadeexpo.viewmodel.BuyerViewModel
import com.example.handmadeexpo.viewmodel.CartViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class DashboardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        setContent {
            DashboardBody(currentUserId)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardBody(userId: String) {
    val context = LocalContext.current
    val activity = context as? Activity

    // Navigation State
    var selectedIndex by remember { mutableIntStateOf(0) }
    var editing by remember { mutableStateOf(false) }
    var changingPassword by remember { mutableStateOf(false) }
    var showAllSellers by remember { mutableStateOf(false) }
    var buyerName by remember { mutableStateOf("Buyer") }
    var chatCount by remember { mutableIntStateOf(0) }

    // Reporting State
    var reportProductId by remember { mutableStateOf<String?>(null) }
    var reportSellerId by remember { mutableStateOf<String?>(null) }

    // Chat State: (ChatID, SellerID, SellerName)
    var activeChatData by remember { mutableStateOf<Triple<String, String, String>?>(null) }

    // Initialize Repos and ViewModels
    val buyerRepo = remember { BuyerRepoImpl() }
    val buyerViewModel = remember { BuyerViewModel(buyerRepo) }

    val cartRepo = remember { CartRepoImpl() }
    val cartViewModel = remember { CartViewModel(cartRepo) }

    // Fetch Buyer Name
    LaunchedEffect(userId) {
        if (userId.isNotEmpty()) {
            FirebaseDatabase.getInstance().getReference("Buyers").child(userId).child("name")
                .get().addOnSuccessListener { snapshot ->
                    buyerName = snapshot.value?.toString() ?: "Buyer"
                }
        }
    }

    // Badge Listener for Incoming Chats
    val inboxRef = remember { FirebaseDatabase.getInstance().getReference("buyer_inbox").child(userId) }
    LaunchedEffect(userId) {
        if (userId.isNotEmpty()) {
            val listener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    chatCount = snapshot.childrenCount.toInt()
                }
                override fun onCancelled(error: DatabaseError) {}
            }
            inboxRef.addValueEventListener(listener)
        }
    }

    data class NavItem(val icon: ImageVector, val label: String, val color: Color)
    val listItems = listOf(
        NavItem(Icons.Default.Home, "Home", Color(0xFF1E88E5)),
        NavItem(Icons.AutoMirrored.Filled.Chat, "Inbox", Color(0xFF4CAF50)),
        NavItem(Icons.Default.ShoppingCart, "Cart", Color(0xFFFF9800)),
        NavItem(Icons.Default.Person, "Profile", Color(0xFF9C27B0))
    )

    // Back Handler Logic
    val isChatActive = selectedIndex == 1 && (activeChatData != null || showAllSellers)
    val isReportingProduct = reportProductId != null
    val isReportingSeller = reportSellerId != null
    val isProfileOverlay = selectedIndex == 3 && (editing || changingPassword)

    BackHandler(enabled = isChatActive || isReportingProduct || isReportingSeller || isProfileOverlay) {
        when {
            isReportingSeller -> reportSellerId = null
            isReportingProduct -> reportProductId = null
            activeChatData != null -> activeChatData = null
            showAllSellers -> showAllSellers = false
            editing -> editing = false
            changingPassword -> changingPassword = false
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF1E88E5)
                ),
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        val titleText = when {
                            reportSellerId != null -> "Report Seller"
                            reportProductId != null -> "Report Product"
                            selectedIndex == 1 && activeChatData != null -> activeChatData!!.third
                            selectedIndex == 1 && showAllSellers -> "Select Seller"
                            selectedIndex == 0 -> "Handmade Expo"
                            selectedIndex == 1 -> "Messages"
                            selectedIndex == 2 -> "My Cart"
                            else -> "My Profile"
                        }
                        Text(
                            titleText,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        if (selectedIndex == 0 && activeChatData == null && reportProductId == null && reportSellerId == null) {
                            Text(
                                "Discover unique products",
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    }
                },
                navigationIcon = {
                    // Show back button for chat, seller list, or reporting screens
                    if (activeChatData != null || showAllSellers || reportProductId != null || reportSellerId != null) {
                        IconButton(
                            onClick = {
                                when {
                                    reportSellerId != null -> reportSellerId = null
                                    reportProductId != null -> reportProductId = null
                                    activeChatData != null -> activeChatData = null
                                    showAllSellers -> showAllSellers = false
                                }
                            }
                        ) {
                            Icon(
                                Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }
                    }
                }
            )
        },
        bottomBar = {
            if (activeChatData == null && reportProductId == null && reportSellerId == null && !editing && !changingPassword) {
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
                                showAllSellers = false
                                reportProductId = null
                                reportSellerId = null
                                if (index != 1) activeChatData = null
                            },
                            icon = {
                                if (index == 1 && chatCount > 0) {
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
        containerColor = Color(0xFFF5F7FA),
        floatingActionButton = {
            // Floating Action Button for New Chat
            if (selectedIndex == 1 && activeChatData == null && !showAllSellers && reportProductId == null && reportSellerId == null) {
                FloatingActionButton(
                    onClick = { showAllSellers = true },
                    containerColor = Color(0xFF4CAF50),
                    contentColor = Color.White,
                    modifier = Modifier.size(64.dp)
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "New Chat",
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            // Global Overlays (Reporting)
            if (reportSellerId != null) {
                ReportSellerScreen(
                    sellerId = reportSellerId!!,
                    onBackClick = { reportSellerId = null }
                )
            } else if (reportProductId != null) {
                ReportProductScreen(
                    productId = reportProductId!!,
                    onBackClick = { reportProductId = null }
                )
            } else {
                when (selectedIndex) {
                    // Home Tab
                    0 -> {
                        HomeScreen(
                            onReportProductClick = { productId ->
                                reportProductId = productId
                            },
                            onReportSellerClick = { sellerId ->
                                reportSellerId = sellerId
                            }
                        )
                    }

                    // Inbox Tab
                    1 -> when {
                        activeChatData != null -> {
                            ChatScreen(
                                chatId = activeChatData!!.first,
                                sellerId = activeChatData!!.second,
                                sellerName = activeChatData!!.third,
                                currentUserId = userId,
                                onBackClick = { activeChatData = null }
                            )
                        }
                        showAllSellers -> {
                            AllSellersListScreen(userId) { chatId, sellerId, sellerName ->
                                activeChatData = Triple(chatId, sellerId, sellerName)
                                showAllSellers = false
                            }
                        }
                        else -> {
                            BuyerChatListScreen(userId) { chatId, sellerId, sellerName ->
                                activeChatData = Triple(chatId, sellerId, sellerName)
                            }
                        }
                    }

                    // Cart Tab
                    2 -> {
                        CartScreen(
                            cartViewModel = cartViewModel,
                            currentUserId = userId
                        )
                    }

                    // Profile Tab
                    3 -> when {
                        changingPassword -> {
                            ChangePasswordScreen(
                                viewModel = buyerViewModel,
                                onBackClick = { changingPassword = false },
                                onPasswordChanged = { changingPassword = false }
                            )
                        }
                        editing -> {
                            EditBuyerProfileScreen(
                                viewModel = buyerViewModel,
                                onBack = { editing = false }
                            )
                        }
                        else -> {
                            BuyerProfileScreen(
                                viewModel = buyerViewModel,
                                onEditClick = { editing = true },
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
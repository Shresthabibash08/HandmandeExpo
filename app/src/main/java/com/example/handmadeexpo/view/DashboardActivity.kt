package com.example.handmadeexpo.view

import android.app.Activity
import android.content.Intent
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
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

    // --- 1. NAVIGATION STATE ---
    var selectedIndex by remember { mutableIntStateOf(0) }
    var editing by remember { mutableStateOf(false) }
    var changingPassword by remember { mutableStateOf(false) }
    var showAllSellers by remember { mutableStateOf(false) }

    // --- NEW: Report Product State ---
    var reportProductId by remember { mutableStateOf<String?>(null) }

    // Chat State: (ChatID, SellerID, SellerName)
    var activeChatData by remember { mutableStateOf<Triple<String, String, String>?>(null) }

    // --- 2. INITIALIZE REPOS AND VIEWMODELS ---
    val buyerRepo = remember { BuyerRepoImpl() }
    val buyerViewModel = remember { BuyerViewModel(buyerRepo) }

    val cartRepo = remember { CartRepoImpl() }
    val cartViewModel = remember { CartViewModel(cartRepo) }

    data class NavItem(val icon: ImageVector, val label: String)
    val listItems = listOf(
        NavItem(Icons.Default.Home, "Home"),
        NavItem(Icons.AutoMirrored.Filled.Chat, "Inbox"),
        NavItem(Icons.Default.ShoppingCart, "Cart"),
        NavItem(Icons.Default.Person, "Profile")
    )

    // --- 3. BACK HANDLER LOGIC ---
    // This ensures pressing "Back" closes overlays before exiting the app
    val isChatActive = selectedIndex == 1 && (activeChatData != null || showAllSellers)
    val isReporting = selectedIndex == 0 && reportProductId != null
    val isProfileOverlay = selectedIndex == 3 && (editing || changingPassword)

    BackHandler(enabled = isChatActive || isReporting || isProfileOverlay) {
        when {
            // Priority 1: Close Report Screen
            isReporting -> reportProductId = null

            // Priority 2: Close Chat Screen
            activeChatData != null -> activeChatData = null

            // Priority 3: Close "New Chat" List
            showAllSellers -> showAllSellers = false

            // Priority 4: Close Profile Edits
            editing -> editing = false
            changingPassword -> changingPassword = false
        }
    }

    Scaffold(
        topBar = {
            // We HIDE the Dashboard TopBar if we are in the Report Screen
            // (Because ReportProductScreen has its own TopBar with a back arrow)
            if (reportProductId == null) {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MainColor,
                        titleContentColor = White12
                    ),
                    title = {
                        val title = when {
                            // Chat Title logic
                            selectedIndex == 1 && activeChatData != null -> activeChatData!!.third
                            selectedIndex == 1 && showAllSellers -> "Select Seller"
                            selectedIndex == 1 -> "Messages"

                            // Tab Titles
                            selectedIndex == 2 -> "My Cart"
                            selectedIndex == 3 -> "Profile"
                            else -> "Handmade Expo"
                        }
                        Text(title, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                )
            }
        },
        bottomBar = {
            // Hide Bottom Bar when inside a Chat or Reporting or Editing Profile
            if (activeChatData == null && reportProductId == null && !editing && !changingPassword) {
                NavigationBar {
                    listItems.forEachIndexed { index, item ->
                        NavigationBarItem(
                            selected = selectedIndex == index,
                            onClick = {
                                selectedIndex = index
                                // Reset sub-states when switching tabs
                                editing = false
                                changingPassword = false
                                showAllSellers = false
                                reportProductId = null
                                if (index != 1) activeChatData = null
                            },
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) }
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            // Only show "New Chat" button when on Inbox tab and not currently in a chat
            if (selectedIndex == 1 && activeChatData == null && !showAllSellers) {
                FloatingActionButton(
                    onClick = { showAllSellers = true },
                    containerColor = MainColor,
                    contentColor = White12
                ) {
                    Icon(Icons.Default.Add, contentDescription = "New Chat")
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (selectedIndex) {
                // --- HOME TAB ---
                0 -> {
                    if (reportProductId != null) {
                        // Show Report Screen (Overlay)
                        ReportProductScreen(
                            productId = reportProductId!!,
                            onBackClick = { reportProductId = null }
                        )
                    } else {
                        // Show Home Screen
                        HomeScreen(
                            onReportClick = { productId ->
                                reportProductId = productId
                            }
                        )
                    }
                }

                // --- INBOX TAB ---
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

                // --- CART TAB ---
                2 -> {
                    CartScreen(
                        cartViewModel = cartViewModel,
                        currentUserId = userId
                    )
                }

                // --- PROFILE TAB ---
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
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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.handmadeexpo.repo.BuyerRepoImpl
import com.example.handmadeexpo.repo.CartRepoImpl
import com.example.handmadeexpo.ui.theme.MainColor
import com.example.handmadeexpo.ui.theme.White12
import com.example.handmadeexpo.viewmodel.BuyerViewModel
import com.example.handmadeexpo.viewmodel.CartViewModel
import com.example.handmadeexpo.viewmodel.ReportViewModel
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
    val reportViewModel: ReportViewModel = viewModel()

    // Check if user is banned
    var isBanned by remember { mutableStateOf(false) }
    var banExpiresAt by remember { mutableStateOf<Long?>(null) }
    var showBanDialog by remember { mutableStateOf(false) }
    var checkingBan by remember { mutableStateOf(true) }

    LaunchedEffect(userId) {
        reportViewModel.checkIfUserBanned(userId) { banned, expiryTime ->
            isBanned = banned
            banExpiresAt = expiryTime
            checkingBan = false
            if (banned) {
                showBanDialog = true
            }
        }
    }

    // Show loading while checking ban status
    if (checkingBan) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = MainColor)
        }
        return
    }

    // Show ban dialog if user is banned
    if (showBanDialog && isBanned && banExpiresAt != null) {
        BanDialog(
            banExpiresAt = banExpiresAt!!,
            onDismiss = {
                // User can't dismiss, redirect to sign in
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(context, SignInActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                context.startActivity(intent)
                activity?.finish()
            }
        )
        return // Don't show the rest of the UI
    }

    // --- 1. NAVIGATION STATE ---
    var selectedIndex by remember { mutableIntStateOf(0) }
    var editing by remember { mutableStateOf(false) }
    var changingPassword by remember { mutableStateOf(false) }
    var showAllSellers by remember { mutableStateOf(false) }

    // (ChatID, SellerID, SellerName)
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
                        selectedIndex == 1 && activeChatData != null -> activeChatData!!.third
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
                            // Reset sub-states when switching tabs
                            editing = false
                            changingPassword = false
                            showAllSellers = false
                            if (index != 1) activeChatData = null
                        },
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) }
                    )
                }
            }
        },
        floatingActionButton = {
            // Only show "New Chat" button when on the Inbox tab and not currently in a chat
            if (selectedIndex == 1 && activeChatData == null && !showAllSellers && !isBanned) {
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
                0 -> HomeScreen()

                1 -> {
                    // Block inbox access if banned
                    if (isBanned) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Block,
                                    contentDescription = "Blocked",
                                    modifier = Modifier.size(80.dp),
                                    tint = Color(0xFFD32F2F)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    "Inbox Access Restricted",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFD32F2F),
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(8.dp))

                                banExpiresAt?.let { expiryTime ->
                                    val remainingTime = expiryTime - System.currentTimeMillis()
                                    val remainingDays = (remainingTime / (24 * 60 * 60 * 1000)).toInt()
                                    val remainingHours = ((remainingTime % (24 * 60 * 60 * 1000)) / (60 * 60 * 1000)).toInt()

                                    Text(
                                        "Time remaining: $remainingDays days, $remainingHours hours",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFD32F2F)
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    "Due to multiple warnings from sellers, your messaging privileges have been temporarily suspended. Please review our community guidelines.",
                                    textAlign = TextAlign.Center,
                                    color = Color.Gray,
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                )
                            }
                        }
                    } else {
                        // Normal inbox flow
                        when {
                            activeChatData != null -> {
                                ChatScreen(
                                    chatId = activeChatData!!.first,
                                    sellerId = activeChatData!!.second,
                                    sellerName = activeChatData!!.third,
                                    currentUserId = userId,
                                    currentUserRole = "buyer",
                                    buyerId = userId,
                                    buyerName = "Buyer",
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
                    }
                }

                2 -> {
                    CartScreen(
                        cartViewModel = cartViewModel,
                        currentUserId = userId
                    )
                }

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

@Composable
fun BanDialog(banExpiresAt: Long, onDismiss: () -> Unit) {
    val remainingTime = banExpiresAt - System.currentTimeMillis()
    val remainingDays = (remainingTime / (24 * 60 * 60 * 1000)).toInt()
    val remainingHours = ((remainingTime % (24 * 60 * 60 * 1000)) / (60 * 60 * 1000)).toInt()

    AlertDialog(
        onDismissRequest = { /* Can't dismiss */ },
        icon = {
            Icon(
                imageVector = Icons.Default.Block,
                contentDescription = "Banned",
                modifier = Modifier.size(48.dp),
                tint = Color(0xFFD32F2F)
            )
        },
        title = {
            Text(
                "Account Temporarily Suspended",
                fontWeight = FontWeight.Bold,
                color = Color(0xFFD32F2F),
                textAlign = TextAlign.Center
            )
        },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "Your account has been temporarily suspended due to multiple warnings from sellers.",
                    modifier = Modifier.padding(bottom = 16.dp),
                    textAlign = TextAlign.Center
                )
                Text(
                    "Ban Duration:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Text(
                    "$remainingDays days, $remainingHours hours",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color(0xFFD32F2F)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Please review our community guidelines and ensure proper conduct when your access is restored.",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFD32F2F)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Sign Out")
            }
        },
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    )
}
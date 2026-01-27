package com.example.handmadeexpo.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.handmadeexpo.model.BuyerModel
import com.example.handmadeexpo.model.SellerModel
import com.example.handmadeexpo.viewmodel.AdminViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminUserListScreen(viewModel: AdminViewModel) {
    var mainTabIndex by remember { mutableIntStateOf(0) }
    var bannedTabIndex by remember { mutableIntStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }

    var selectedSeller by remember { mutableStateOf<SellerModel?>(null) }
    var selectedBuyer by remember { mutableStateOf<BuyerModel?>(null) }
    var userToDelete by remember { mutableStateOf<Pair<String, String>?>(null) }
    var userToUnban by remember { mutableStateOf<Pair<String, String>?>(null) }

    // Filter Data
    val activeSellers = viewModel.sellers.filter { !it.banned }
    val activeBuyers = viewModel.buyers.filter { !it.banned }
    val bannedSellers = viewModel.sellers.filter { it.banned }
    val bannedBuyers = viewModel.buyers.filter { it.banned }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F7FA))
    ) {
        // Modern Header
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(4.dp, RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(Color(0xFF9C27B0).copy(alpha = 0.15f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.People,
                            contentDescription = null,
                            tint = Color(0xFF9C27B0),
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            "User Management",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF212121)
                        )
                        Text(
                            "${viewModel.sellers.size + viewModel.buyers.size} total users",
                            fontSize = 13.sp,
                            color = Color.Gray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Search Bar (only for active tabs)
                if (mainTabIndex != 2) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Search users...", color = Color.Gray) },
                        leadingIcon = {
                            Icon(Icons.Default.Search, null, tint = Color(0xFF9C27B0))
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF9C27B0),
                            unfocusedBorderColor = Color(0xFFE0E0E0),
                            focusedContainerColor = Color(0xFFF3E5F5),
                            unfocusedContainerColor = Color(0xFFFAFAFA)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Main Tabs
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .shadow(2.dp, RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp)
        ) {
            TabRow(
                selectedTabIndex = mainTabIndex,
                containerColor = Color.Transparent,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[mainTabIndex]),
                        color = Color(0xFF9C27B0),
                        height = 3.dp
                    )
                }
            ) {
                Tab(
                    selected = mainTabIndex == 0,
                    onClick = {
                        mainTabIndex = 0
                        searchQuery = ""
                    },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                "Sellers",
                                fontWeight = if (mainTabIndex == 0) FontWeight.Bold else FontWeight.Normal
                            )
                            Spacer(Modifier.width(8.dp))
                            Badge(containerColor = Color(0xFFFF9800)) {
                                Text(activeSellers.size.toString(), fontSize = 10.sp)
                            }
                        }
                    }
                )
                Tab(
                    selected = mainTabIndex == 1,
                    onClick = {
                        mainTabIndex = 1
                        searchQuery = ""
                    },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                "Buyers",
                                fontWeight = if (mainTabIndex == 1) FontWeight.Bold else FontWeight.Normal
                            )
                            Spacer(Modifier.width(8.dp))
                            Badge(containerColor = Color(0xFF4CAF50)) {
                                Text(activeBuyers.size.toString(), fontSize = 10.sp)
                            }
                        }
                    }
                )
                Tab(
                    selected = mainTabIndex == 2,
                    onClick = {
                        mainTabIndex = 2
                        searchQuery = ""
                    },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                "Banned",
                                fontWeight = if (mainTabIndex == 2) FontWeight.Bold else FontWeight.Normal
                            )
                            Spacer(Modifier.width(8.dp))
                            Badge(containerColor = Color(0xFFF44336)) {
                                Text((bannedSellers.size + bannedBuyers.size).toString(), fontSize = 10.sp)
                            }
                        }
                    }
                )
            }
        }

        // Sub Tabs for Banned Section
        if (mainTabIndex == 2) {
            Spacer(modifier = Modifier.height(12.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .shadow(1.dp, RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp)
            ) {
                TabRow(
                    selectedTabIndex = bannedTabIndex,
                    containerColor = Color.Transparent,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[bannedTabIndex]),
                            color = Color(0xFFF44336),
                            height = 2.dp
                        )
                    }
                ) {
                    Tab(
                        selected = bannedTabIndex == 0,
                        onClick = { bannedTabIndex = 0 },
                        text = {
                            Text(
                                "Sellers (${bannedSellers.size})",
                                fontSize = 13.sp,
                                fontWeight = if (bannedTabIndex == 0) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                    Tab(
                        selected = bannedTabIndex == 1,
                        onClick = { bannedTabIndex = 1 },
                        text = {
                            Text(
                                "Buyers (${bannedBuyers.size})",
                                fontSize = 13.sp,
                                fontWeight = if (bannedTabIndex == 1) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Content
        if (viewModel.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(
                        color = Color(0xFF9C27B0),
                        strokeWidth = 3.dp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Loading users...", color = Color.Gray, fontSize = 14.sp)
                }
            }
        } else {
            val currentList = when (mainTabIndex) {
                0 -> activeSellers.filter {
                    searchQuery.isBlank() || it.shopName.contains(searchQuery, ignoreCase = true) ||
                            it.sellerEmail.contains(searchQuery, ignoreCase = true)
                }
                1 -> activeBuyers.filter {
                    searchQuery.isBlank() || it.buyerName.contains(searchQuery, ignoreCase = true) ||
                            it.buyerEmail.contains(searchQuery, ignoreCase = true)
                }
                else -> if (bannedTabIndex == 0) bannedSellers else bannedBuyers
            }

            if (currentList.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            if (mainTabIndex == 2) Icons.Default.CheckCircle else Icons.Default.People,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = Color.Gray.copy(alpha = 0.3f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            if (searchQuery.isBlank()) "No users found" else "No matching users",
                            fontSize = 16.sp,
                            color = Color.Gray
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    when (mainTabIndex) {
                        0 -> items(currentList as List<SellerModel>) { seller ->
                            ModernUserCard(
                                name = seller.shopName,
                                email = seller.sellerEmail,
                                userType = "Seller",
                                isBanned = false,
                                icon = Icons.Default.Store,
                                iconColor = Color(0xFFFF9800),
                                actionIcon = Icons.Default.Delete,
                                actionColor = Color(0xFFF44336),
                                onAction = { userToDelete = seller.sellerId to "seller" },
                                onClick = { selectedSeller = seller }
                            )
                        }
                        1 -> items(currentList as List<BuyerModel>) { buyer ->
                            ModernUserCard(
                                name = buyer.buyerName,
                                email = buyer.buyerEmail,
                                userType = "Buyer",
                                isBanned = false,
                                icon = Icons.Default.ShoppingCart,
                                iconColor = Color(0xFF4CAF50),
                                actionIcon = Icons.Default.Delete,
                                actionColor = Color(0xFFF44336),
                                onAction = { userToDelete = buyer.buyerId to "buyer" },
                                onClick = { selectedBuyer = buyer }
                            )
                        }
                        2 -> {
                            if (bannedTabIndex == 0) {
                                items(currentList as List<SellerModel>) { seller ->
                                    ModernUserCard(
                                        name = seller.shopName,
                                        email = seller.sellerEmail,
                                        userType = "Seller",
                                        isBanned = true,
                                        icon = Icons.Default.Store,
                                        iconColor = Color(0xFFFF9800),
                                        actionIcon = Icons.Default.Refresh,
                                        actionColor = Color(0xFF4CAF50),
                                        onAction = { userToUnban = seller.sellerId to "seller" },
                                        onClick = { selectedSeller = seller }
                                    )
                                }
                            } else {
                                items(currentList as List<BuyerModel>) { buyer ->
                                    ModernUserCard(
                                        name = buyer.buyerName,
                                        email = buyer.buyerEmail,
                                        userType = "Buyer",
                                        isBanned = true,
                                        icon = Icons.Default.ShoppingCart,
                                        iconColor = Color(0xFF4CAF50),
                                        actionIcon = Icons.Default.Refresh,
                                        actionColor = Color(0xFF4CAF50),
                                        onAction = { userToUnban = buyer.buyerId to "buyer" },
                                        onClick = { selectedBuyer = buyer }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Dialogs
    if (userToDelete != null) {
        ModernConfirmationDialog(
            title = "Delete User",
            message = "This will permanently delete this user and all their data. This action cannot be undone.",
            confirmText = "Delete",
            confirmColor = Color(0xFFF44336),
            icon = Icons.Default.Delete,
            onConfirm = {
                viewModel.deleteUser(userToDelete!!.first, userToDelete!!.second)
                userToDelete = null
            },
            onDismiss = { userToDelete = null }
        )
    }

    if (userToUnban != null) {
        ModernConfirmationDialog(
            title = "Unban User",
            message = "This user will be able to access their account again. Are you sure?",
            confirmText = "Unban",
            confirmColor = Color(0xFF4CAF50),
            icon = Icons.Default.CheckCircle,
            onConfirm = {
                viewModel.unbanUser(userToUnban!!.first, userToUnban!!.second)
                userToUnban = null
            },
            onDismiss = { userToUnban = null }
        )
    }

    selectedSeller?.let { seller ->
        ModernUserDetailDialog(
            title = "Seller Details",
            details = mapOf(
                "Shop Name" to seller.shopName,
                "Email" to seller.sellerEmail,
                "Phone" to seller.sellerPhoneNumber,
                "Address" to seller.sellerAddress,
                "Status" to if (seller.banned) "BANNED" else "Active"
            ),
            onDismiss = { selectedSeller = null }
        )
    }

    selectedBuyer?.let { buyer ->
        ModernUserDetailDialog(
            title = "Buyer Details",
            details = mapOf(
                "Name" to buyer.buyerName,
                "Email" to buyer.buyerEmail,
                "Phone" to buyer.buyerPhoneNumber,
                "Address" to buyer.buyerAddress,
                "Status" to if (buyer.banned) "BANNED" else "Active"
            ),
            onDismiss = { selectedBuyer = null }
        )
    }
}

@Composable
fun ModernUserCard(
    name: String,
    email: String,
    userType: String,
    isBanned: Boolean,
    icon: ImageVector,
    iconColor: Color,
    actionIcon: ImageVector,
    actionColor: Color,
    onAction: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .shadow(2.dp, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(
            containerColor = if (isBanned) Color(0xFFFFEBEE) else Color.White
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(iconColor.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color(0xFF212121),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (isBanned) {
                        Spacer(Modifier.width(8.dp))
                        Surface(
                            color = Color(0xFFF44336),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                "BANNED",
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    email,
                    fontSize = 13.sp,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    userType,
                    fontSize = 11.sp,
                    color = Color(0xFF9E9E9E)
                )
            }

            IconButton(
                onClick = onAction,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(actionColor.copy(alpha = 0.1f))
            ) {
                Icon(
                    actionIcon,
                    contentDescription = null,
                    tint = actionColor,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun ModernConfirmationDialog(
    title: String,
    message: String,
    confirmText: String,
    confirmColor: Color,
    icon: ImageVector,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(confirmColor.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = confirmColor,
                    modifier = Modifier.size(28.dp)
                )
            }
        },
        title = {
            Text(
                title,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Color(0xFF212121)
            )
        },
        text = {
            Text(
                message,
                fontSize = 14.sp,
                color = Color(0xFF616161)
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = confirmColor),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(confirmText, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color(0xFF757575)
                )
            ) {
                Text("Cancel", fontWeight = FontWeight.Medium)
            }
        },
        shape = RoundedCornerShape(20.dp),
        containerColor = Color.White
    )
}

@Composable
fun ModernUserDetailDialog(
    title: String,
    details: Map<String, String>,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                title,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Color(0xFF212121)
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                details.forEach { (key, value) ->
                    Column {
                        Text(
                            key,
                            fontSize = 11.sp,
                            color = Color.Gray,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            value.ifEmpty { "N/A" },
                            fontSize = 15.sp,
                            fontWeight = if (key == "Status") FontWeight.Bold else FontWeight.Normal,
                            color = if (value == "BANNED") Color(0xFFF44336) else Color(0xFF212121)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF9C27B0)
                )
            ) {
                Text("Close", fontWeight = FontWeight.Bold)
            }
        },
        shape = RoundedCornerShape(20.dp),
        containerColor = Color.White
    )
}
package com.example.handmadeexpo.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset // <--- ADDED IMPORT
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.handmadeexpo.model.BuyerModel
import com.example.handmadeexpo.model.SellerModel
import com.example.handmadeexpo.viewmodel.AdminViewModel

@Composable
fun AdminUserListScreen(viewModel: AdminViewModel) {
    // State 1: Main Tab (0=Sellers, 1=Buyers, 2=Banned)
    var mainTabIndex by remember { mutableIntStateOf(0) }

    // State 2: Sub Tab for Banned Section (0=Sellers, 1=Buyers)
    var bannedTabIndex by remember { mutableIntStateOf(0) }

    // Dialog States
    var selectedSeller by remember { mutableStateOf<SellerModel?>(null) }
    var selectedBuyer by remember { mutableStateOf<BuyerModel?>(null) }
    var userToDelete by remember { mutableStateOf<Pair<String, String>?>(null) }
    var userToUnban by remember { mutableStateOf<Pair<String, String>?>(null) }

    // --- FILTER DATA ---
    val activeSellers = viewModel.sellers.filter { !it.banned }
    val activeBuyers = viewModel.buyers.filter { !it.banned }

    val bannedSellers = viewModel.sellers.filter { it.banned }
    val bannedBuyers = viewModel.buyers.filter { it.banned }

    Column(modifier = Modifier.fillMaxSize()) {
        // --- MAIN TABS ---
        TabRow(selectedTabIndex = mainTabIndex) {
            Tab(selected = mainTabIndex == 0, onClick = { mainTabIndex = 0 }, text = { Text("Sellers") })
            Tab(selected = mainTabIndex == 1, onClick = { mainTabIndex = 1 }, text = { Text("Buyers") })
            Tab(selected = mainTabIndex == 2, onClick = { mainTabIndex = 2 }, text = { Text("Banned") })
        }

        // --- SUB TABS (Visible only when 'Banned' is selected) ---
        if (mainTabIndex == 2) {
            TabRow(
                selectedTabIndex = bannedTabIndex,
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.primary,
                indicator = { tabPositions ->
                    // Added safety check
                    if (bannedTabIndex < tabPositions.size) {
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[bannedTabIndex]),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            ) {
                Tab(selected = bannedTabIndex == 0, onClick = { bannedTabIndex = 0 }, text = { Text("Banned Sellers", fontSize = 12.sp) })
                Tab(selected = bannedTabIndex == 1, onClick = { bannedTabIndex = 1 }, text = { Text("Banned Buyers", fontSize = 12.sp) })
            }
        }

        if (viewModel.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        } else {
            LazyColumn(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {

                // === TAB 0: ACTIVE SELLERS ===
                if (mainTabIndex == 0) {
                    if (activeSellers.isEmpty()) item { EmptyState("No active sellers") }
                    items(activeSellers) { seller ->
                        UserRow(
                            name = seller.shopName,
                            email = seller.sellerEmail,
                            icon = Icons.Default.Delete, iconColor = Color.Red,
                            onAction = { userToDelete = seller.sellerId to "seller" },
                            onClick = { selectedSeller = seller }
                        )
                    }
                }

                // === TAB 1: ACTIVE BUYERS ===
                else if (mainTabIndex == 1) {
                    if (activeBuyers.isEmpty()) item { EmptyState("No active buyers") }
                    items(activeBuyers) { buyer ->
                        UserRow(
                            name = buyer.buyerName,
                            email = buyer.buyerEmail,
                            icon = Icons.Default.Delete, iconColor = Color.Red,
                            onAction = { userToDelete = buyer.buyerId to "buyer" },
                            onClick = { selectedBuyer = buyer }
                        )
                    }
                }

                // === TAB 2: BANNED USERS ===
                else {
                    if (bannedTabIndex == 0) {
                        // --- Sub-section: Banned Sellers ---
                        if (bannedSellers.isEmpty()) item { EmptyState("No banned sellers") }
                        items(bannedSellers) { seller ->
                            UserRow(
                                name = "${seller.shopName} (BANNED)",
                                email = seller.sellerEmail,
                                icon = Icons.Default.Refresh, iconColor = Color(0xFF4CAF50), // Green for Unban
                                onAction = { userToUnban = seller.sellerId to "seller" },
                                onClick = { selectedSeller = seller }
                            )
                        }
                    } else {
                        // --- Sub-section: Banned Buyers ---
                        if (bannedBuyers.isEmpty()) item { EmptyState("No banned buyers") }
                        items(bannedBuyers) { buyer ->
                            UserRow(
                                name = "${buyer.buyerName} (BANNED)",
                                email = buyer.buyerEmail,
                                icon = Icons.Default.Refresh, iconColor = Color(0xFF4CAF50), // Green for Unban
                                onAction = { userToUnban = buyer.buyerId to "buyer" },
                                onClick = { selectedBuyer = buyer }
                            )
                        }
                    }
                }
            }
        }
    }

    // --- DIALOGS ---
    if (userToDelete != null) {
        ConfirmationDialog(
            title = "Confirm Delete",
            message = "Are you sure you want to PERMANENTLY delete this user?",
            confirmText = "Delete", confirmColor = Color.Red,
            onConfirm = {
                viewModel.deleteUser(userToDelete!!.first, userToDelete!!.second)
                userToDelete = null
            },
            onDismiss = { userToDelete = null }
        )
    }

    if (userToUnban != null) {
        ConfirmationDialog(
            title = "Unban User",
            message = "Are you sure you want to unban this user? They will be allowed to login again.",
            confirmText = "Unban", confirmColor = Color(0xFF4CAF50),
            onConfirm = {
                viewModel.unbanUser(userToUnban!!.first, userToUnban!!.second)
                userToUnban = null
            },
            onDismiss = { userToUnban = null }
        )
    }

    selectedSeller?.let { seller ->
        UserDetailPopup("Seller Details", mapOf(
            "Shop" to seller.shopName, "Email" to seller.sellerEmail,
            "Status" to if(seller.banned) "Banned" else "Active",
            "Phone" to seller.sellerPhoneNumber, "Address" to seller.sellerAddress
        )) { selectedSeller = null }
    }
    selectedBuyer?.let { buyer ->
        UserDetailPopup("Buyer Details", mapOf(
            "Name" to buyer.buyerName, "Email" to buyer.buyerEmail,
            "Status" to if(buyer.banned) "Banned" else "Active",
            "Phone" to buyer.buyerPhoneNumber, "Address" to buyer.buyerAddress
        )) { selectedBuyer = null }
    }
}

// --- HELPER COMPOSABLES ---
@Composable
fun EmptyState(text: String) {
    Text(text, color = Color.Gray, modifier = Modifier.padding(8.dp))
}

@Composable
fun UserRow(name: String, email: String, icon: ImageVector, iconColor: Color, onAction: () -> Unit, onClick: () -> Unit) {
    Card(Modifier.fillMaxWidth().clickable { onClick() }) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(name, fontWeight = FontWeight.Bold)
                Text(email, fontSize = 12.sp, color = Color.Gray)
            }
            IconButton(onClick = onAction) {
                Icon(icon, contentDescription = null, tint = iconColor)
            }
        }
    }
}

@Composable
fun ConfirmationDialog(title: String, message: String, confirmText: String, confirmColor: Color, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { Text(message) },
        confirmButton = { Button(onClick = onConfirm, colors = ButtonDefaults.buttonColors(containerColor = confirmColor)) { Text(confirmText) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
fun UserDetailPopup(title: String, details: Map<String, String>, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title, fontWeight = FontWeight.Bold) },
        text = {
            Column {
                details.forEach { (k, v) ->
                    Text("$k:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Text(v.ifEmpty { "N/A" }, modifier = Modifier.padding(bottom = 8.dp))
                }
            }
        },
        confirmButton = { Button(onClick = onDismiss) { Text("Close") } }
    )
}
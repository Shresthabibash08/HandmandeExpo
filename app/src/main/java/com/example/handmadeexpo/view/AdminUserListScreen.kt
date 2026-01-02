package com.example.handmadeexpo.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.handmadeexpo.model.BuyerModel
import com.example.handmadeexpo.model.SellerModel
import com.example.handmadeexpo.viewmodel.AdminViewModel

@Composable
fun AdminUserListScreen(viewModel: AdminViewModel) {
    var tabIndex by remember { mutableIntStateOf(0) }
    var selectedSeller by remember { mutableStateOf<SellerModel?>(null) }
    var selectedBuyer by remember { mutableStateOf<BuyerModel?>(null) }

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = tabIndex) {
            Tab(selected = tabIndex == 0, onClick = { tabIndex = 0 }, text = { Text("Sellers") })
            Tab(selected = tabIndex == 1, onClick = { tabIndex = 1 }, text = { Text("Buyers") })
        }

        // Check if data is still loading
        if (viewModel.isSellersLoading || viewModel.isBuyersLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF6200EE))
            }
        } else {
            LazyColumn(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if (tabIndex == 0) {
                    items(viewModel.sellers.filter { it.shopName.contains(viewModel.searchQuery, true) }) { seller ->
                        UserRow(seller.shopName, seller.sellerEmail, onDelete = { viewModel.deleteUser(seller.sellerId, "seller") }) {
                            selectedSeller = seller
                        }
                    }
                } else {
                    items(viewModel.buyers.filter { it.buyerName.contains(viewModel.searchQuery, true) }) { buyer ->
                        UserRow(buyer.buyerName, buyer.buyerEmail, onDelete = { viewModel.deleteUser(buyer.buyerId, "buyer") }) {
                            selectedBuyer = buyer
                        }
                    }
                }
            }
        }
    }

    // Detail Popups showing ALL data
    selectedSeller?.let { seller ->
        UserDetailPopup(
            title = "Seller Details",
            details = mapOf(
                "Shop Name" to seller.shopName,
                "Seller ID" to seller.sellerId,
                "Email" to seller.sellerEmail,
                "Phone" to seller.sellerPhoneNumber,
                "Address" to seller.sellerAddress,
                "PAN Number" to seller.panNumber
            )
        ) { selectedSeller = null }
    }

    selectedBuyer?.let { buyer ->
        UserDetailPopup(
            title = "Buyer Details",
            details = mapOf(
                "Name" to buyer.buyerName,
                "Buyer ID" to buyer.buyerId,
                "Email" to buyer.buyerEmail,
                "Phone" to buyer.buyerPhoneNumber,
                "Address" to buyer.buyerAddress
            )
        ) { selectedBuyer = null }
    }
}

@Composable
fun UserRow(name: String, email: String, onDelete: () -> Unit, onClick: () -> Unit) {
    Card(Modifier.fillMaxWidth().clickable { onClick() }) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(name, fontWeight = FontWeight.Bold)
                Text(email, fontSize = 12.sp, color = Color.Gray)
            }
            IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, null, tint = Color.Red) }
        }
    }
}

@Composable
fun UserDetailPopup(title: String, details: Map<String, String>, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title, fontWeight = FontWeight.Bold) },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                details.forEach { (label, value) ->
                    Column(modifier = Modifier.padding(vertical = 4.dp)) {
                        Text(label, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.Gray)
                        Text(value.ifEmpty { "Not Provided" }, fontSize = 16.sp)
                        HorizontalDivider(modifier = Modifier.padding(top = 4.dp), thickness = 0.5.dp)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Close") }
        }
    )
}
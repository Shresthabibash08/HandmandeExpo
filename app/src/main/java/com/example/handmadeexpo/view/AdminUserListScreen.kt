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

    // Detail Popups
    selectedSeller?.let { UserDetailPopup(it.shopName, mapOf("Email" to it.sellerEmail, "Phone" to it.sellerPhoneNumber, "Address" to it.sellerAddress, "PAN" to it.panNumber)) { selectedSeller = null } }
    selectedBuyer?.let { UserDetailPopup(it.buyerName, mapOf("Email" to it.buyerEmail, "Phone" to it.buyerPhoneNumber, "Address" to it.buyerAddress)) { selectedBuyer = null } }
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
fun UserDetailPopup(name: String, details: Map<String, String>, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(name, fontWeight = FontWeight.Bold) },
        text = {
            Column {
                details.forEach { (k, v) ->
                    Text("$k:", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.Gray)
                    Text(v.ifEmpty { "N/A" }, modifier = Modifier.padding(bottom = 8.dp))
                }
            }
        },
        confirmButton = { Button(onClick = onDismiss) { Text("Close") } }
    )
}
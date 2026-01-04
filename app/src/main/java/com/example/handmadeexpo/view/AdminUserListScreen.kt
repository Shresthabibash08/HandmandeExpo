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
    var userToDelete by remember { mutableStateOf<Pair<String, String>?>(null) } // ID to Role

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = tabIndex) {
            Tab(selected = tabIndex == 0, onClick = { tabIndex = 0 }, text = { Text("Sellers") })
            Tab(selected = tabIndex == 1, onClick = { tabIndex = 1 }, text = { Text("Buyers") })
        }

        if (viewModel.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        } else {
            LazyColumn(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if (tabIndex == 0) {
                    items(viewModel.sellers) { seller ->
                        UserRow(seller.shopName, seller.sellerEmail, onDelete = { userToDelete = seller.sellerId to "seller" }) { selectedSeller = seller }
                    }
                } else {
                    items(viewModel.buyers) { buyer ->
                        UserRow(buyer.buyerName, buyer.buyerEmail, onDelete = { userToDelete = buyer.buyerId to "buyer" }) { selectedBuyer = buyer }
                    }
                }
            }
        }
    }

    if (userToDelete != null) {
        DeleteConfirmationDialog(onConfirm = {
            viewModel.deleteUser(userToDelete!!.first, userToDelete!!.second)
            userToDelete = null
        }, onDismiss = { userToDelete = null })
    }

    selectedSeller?.let { seller ->
        UserDetailPopup("Seller Details", mapOf("Shop" to seller.shopName, "Email" to seller.sellerEmail, "Phone" to seller.sellerPhoneNumber, "Address" to seller.sellerAddress)) { selectedSeller = null }
    }
    selectedBuyer?.let { buyer ->
        UserDetailPopup("Buyer Details", mapOf("Name" to buyer.buyerName, "Email" to buyer.buyerEmail, "Phone" to buyer.buyerPhoneNumber, "Address" to buyer.buyerAddress)) { selectedBuyer = null }
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

@Composable
fun DeleteConfirmationDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Confirm Delete") },
        text = { Text("Are you sure you want to delete this? This action cannot be undone.") },
        confirmButton = { Button(onClick = onConfirm, colors = ButtonDefaults.buttonColors(containerColor = Color.Red)) { Text("Delete") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
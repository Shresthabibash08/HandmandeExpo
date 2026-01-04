package com.example.handmadeexpo.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.handmadeexpo.model.BuyerModel
import com.example.handmadeexpo.ui.theme.MainColor
import com.example.handmadeexpo.viewmodel.BuyerViewModel

@Composable
fun EditBuyerProfileScreen(
    viewModel: BuyerViewModel,
    onBack: () -> Unit
) {

    val buyer by viewModel.buyer.observeAsState()

    var name by remember { mutableStateOf(buyer?.buyerName ?: "") }
    var email by remember { mutableStateOf(buyer?.buyerEmail ?: "") }
    var phone by remember { mutableStateOf(buyer?.buyerPhoneNumber ?: "") }
    var address by remember { mutableStateOf(buyer?.buyerAddress ?: "") }

    var loading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Phone") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = address,
            onValueChange = { address = it },
            label = { Text("Address") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                buyer?.buyerId?.let { id ->
                    loading = true
                    val updatedBuyer = BuyerModel(
                        buyerId = id,
                        buyerName = name,
                        buyerEmail = email,
                        buyerPhoneNumber = phone,
                        buyerAddress = address
                    )
                    viewModel.updateProfile(id, updatedBuyer) { success, _ ->
                        loading = false
                        if (success) onBack()
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !loading,
            colors = ButtonDefaults.buttonColors(containerColor = MainColor)
        ) {
            Text(if (loading) "Saving..." else "Save")
        }
    }
}

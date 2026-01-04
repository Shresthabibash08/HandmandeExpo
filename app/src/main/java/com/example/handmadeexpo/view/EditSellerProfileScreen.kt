package com.example.handmadeexpo.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.handmadeexpo.model.SellerModel
import com.example.handmadeexpo.ui.theme.MainColor
import com.example.handmadeexpo.viewmodel.SellerViewModel

@Composable
fun EditSellerProfileScreen(
    viewModel: SellerViewModel,
    onBack: () -> Unit
) {
    val seller by viewModel.seller.observeAsState()
    var shopName by remember { mutableStateOf(seller?.shopName ?: "") }
    var email by remember { mutableStateOf(seller?.sellerEmail ?: "") }
    var phone by remember { mutableStateOf(seller?.sellerPhoneNumber ?: "") }
    var address by remember { mutableStateOf(seller?.sellerAddress ?: "") }
    var pan by remember { mutableStateOf(seller?.panNumber ?: "") }

    var loading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {

        OutlinedTextField(
            value = shopName,
            onValueChange = { shopName = it },
            label = { Text("Shop Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Phone Number") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = address,
            onValueChange = { address = it },
            label = { Text("Address") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = pan,
            onValueChange = { pan = it },
            label = { Text("PAN Number") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (errorMessage != null) {
            Text(text = errorMessage!!, color = MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.height(8.dp))
        }

        Button(
            onClick = {
                seller?.sellerId?.let { id ->
                    loading = true
                    val updatedSeller = SellerModel(
                        sellerId = id,
                        shopName = shopName,
                        sellerEmail = email,
                        sellerPhoneNumber = phone,
                        sellerAddress = address,
                        panNumber = pan
                    )
                    viewModel.updateProfile(id, updatedSeller) { success, msg ->
                        loading = false
                        if (success) {
                            onBack() // Navigate back to profile
                        } else {
                            errorMessage = msg
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !loading,
            colors = ButtonDefaults.buttonColors(containerColor = MainColor)
        ) {
            Text(text = if (loading) "Saving..." else "Save")
        }
    }
}

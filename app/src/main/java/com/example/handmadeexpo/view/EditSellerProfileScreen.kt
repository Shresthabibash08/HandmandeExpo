package com.example.handmadeexpo.view

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.handmadeexpo.model.SellerModel
import com.example.handmadeexpo.ui.theme.MainColor
import com.example.handmadeexpo.viewmodel.SellerViewModel

@Composable
fun EditSellerProfileScreen(
    viewModel: SellerViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val seller by viewModel.seller.observeAsState()

    var fullName by remember { mutableStateOf("") }
    var shopName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var pan by remember { mutableStateOf("") }

    var loading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(seller) {
        seller?.let {
            fullName = it.fullName
            shopName = it.shopName
            email = it.sellerEmail
            phone = it.sellerPhoneNumber
            address = it.sellerAddress
            pan = it.panNumber
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .verticalScroll(rememberScrollState())
    ) {

        OutlinedTextField(
            value = fullName,
            onValueChange = { fullName = it },
            label = { Text("Full Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = shopName,
            onValueChange = { shopName = it },
            label = { Text("Shop Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // ✅ READ-ONLY EMAIL FIELD
        OutlinedTextField(
            value = email,
            onValueChange = { /* Do nothing - read only */ },
            label = { Text("Email (Cannot be changed)") },
            modifier = Modifier.fillMaxWidth(),
            enabled = false,  // ✅ Disabled
            colors = OutlinedTextFieldDefaults.colors(
                disabledTextColor = Color.Black,
                disabledBorderColor = Color.Gray,
                disabledLabelColor = Color.Gray,
                disabledLeadingIconColor = Color.Gray,
                disabledTrailingIconColor = Color.Gray
            ),
            trailingIcon = {
                Icon(
                    Icons.Default.Info,
                    contentDescription = "Cannot change email",
                    tint = Color.Gray
                )
            }
        )

        // ✅ INFO MESSAGE
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.Start
        ) {
            Icon(
                Icons.Default.Info,
                contentDescription = null,
                tint = Color(0xFFE65100),
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Email cannot be changed for security reasons",
                color = Color.Gray,
                fontSize = 12.sp
            )
        }

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
                seller?.let { currentSeller ->
                    loading = true
                    val updatedSeller = SellerModel(
                        sellerId = currentSeller.sellerId,
                        fullName = fullName,
                        shopName = shopName,
                        sellerEmail = email,  // ✅ Email stays the same
                        sellerPhoneNumber = phone,
                        sellerAddress = address,
                        panNumber = pan,
                        role = currentSeller.role,
                        documentType = currentSeller.documentType,
                        documentUrl = currentSeller.documentUrl,
                        verificationStatus = currentSeller.verificationStatus,
                        banned = currentSeller.banned
                    )

                    viewModel.updateProfile(currentSeller.sellerId, updatedSeller) { success, msg ->
                        loading = false
                        if (success) {
                            Toast.makeText(
                                context,
                                "Profile updated successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                            onBack()
                        } else {
                            errorMessage = msg
                            Toast.makeText(
                                context,
                                msg ?: "Failed to update profile",
                                Toast.LENGTH_SHORT
                            ).show()
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
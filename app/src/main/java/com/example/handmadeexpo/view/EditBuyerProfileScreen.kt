package com.example.handmadeexpo.view

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.handmadeexpo.model.BuyerModel
import com.example.handmadeexpo.ui.theme.MainColor
import com.example.handmadeexpo.viewmodel.BuyerViewModel

@Composable
fun EditBuyerProfileScreen(
    viewModel: BuyerViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val buyer by viewModel.buyer.observeAsState()

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }

    var loading by remember { mutableStateOf(false) }

    LaunchedEffect(buyer) {
        buyer?.let {
            name = it.buyerName
            email = it.buyerEmail
            phone = it.buyerPhoneNumber
            address = it.buyerAddress
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .verticalScroll(rememberScrollState())
    ) {

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
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
                        buyerEmail = email,  // ✅ Email stays the same
                        buyerPhoneNumber = phone,
                        buyerAddress = address,
                        role = buyer?.role ?: "buyer",
                        banned = buyer?.banned ?: false
                    )
                    viewModel.updateProfile(id, updatedBuyer) { success, _ ->
                        loading = false
                        if (success) {
                            Toast.makeText(
                                context,
                                "Profile updated successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                            onBack()
                        } else {
                            Toast.makeText(
                                context,
                                "Failed to update profile",
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
            Text(if (loading) "Saving..." else "Save")
        }
    }
}
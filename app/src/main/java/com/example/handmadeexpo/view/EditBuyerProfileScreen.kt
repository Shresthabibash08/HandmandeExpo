package com.example.handmadeexpo.view

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.example.handmadeexpo.utils.AdminEmailValidator

@Composable
fun EditBuyerProfileScreen(
    viewModel: BuyerViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val buyer by viewModel.buyer.observeAsState()

    var name by remember { mutableStateOf(buyer?.buyerName ?: "") }
    var email by remember { mutableStateOf(buyer?.buyerEmail ?: "") }
    var phone by remember { mutableStateOf(buyer?.buyerPhoneNumber ?: "") }
    var address by remember { mutableStateOf(buyer?.buyerAddress ?: "") }

    var loading by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var isEmailDuplicate by remember { mutableStateOf(false) }

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

        OutlinedTextField(
            value = email,
            onValueChange = { newEmail ->
                email = newEmail
                // First check if it's admin email
                if (AdminEmailValidator.isReservedEmail(newEmail)) {
                    emailError = AdminEmailValidator.getReservedEmailError()
                    isEmailDuplicate = true
                } else if (newEmail.isNotBlank() && newEmail != buyer?.buyerEmail) {
                    // Only check database if email changed
                    AdminEmailValidator.isBuyerEmailExists(newEmail) { exists ->
                        isEmailDuplicate = exists
                        emailError = if (exists) {
                            AdminEmailValidator.getDuplicateEmailError()
                        } else {
                            null
                        }
                    }
                } else if (newEmail == buyer?.buyerEmail) {
                    emailError = null
                    isEmailDuplicate = false
                }
            },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            isError = emailError != null
        )

        if (emailError != null) {
            Text(
                text = emailError!!,
                color = Color.Red,
                fontSize = 12.sp,
                modifier = Modifier.padding(vertical = 4.dp)
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
                // Show toast if email is duplicate or reserved
                if (isEmailDuplicate || AdminEmailValidator.isReservedEmail(email)) {
                    Toast.makeText(
                        context,
                        "Cannot save: Email already exists or is reserved",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@Button
                }

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
            enabled = !loading && !isEmailDuplicate && !AdminEmailValidator.isReservedEmail(email),
            colors = ButtonDefaults.buttonColors(containerColor = MainColor)
        ) {
            Text(if (loading) "Saving..." else "Save")
        }
    }
}
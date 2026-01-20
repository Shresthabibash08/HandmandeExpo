package com.example.handmadeexpo.view

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.handmadeexpo.model.SellerModel
import com.example.handmadeexpo.ui.theme.MainColor
import com.example.handmadeexpo.viewmodel.SellerViewModel
import com.example.handmadeexpo.utils.AdminEmailValidator

@Composable
fun EditSellerProfileScreen(
    viewModel: SellerViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val seller by viewModel.seller.observeAsState()
    var shopName by remember { mutableStateOf(seller?.shopName ?: "") }
    var email by remember { mutableStateOf(seller?.sellerEmail ?: "") }
    var phone by remember { mutableStateOf(seller?.sellerPhoneNumber ?: "") }
    var address by remember { mutableStateOf(seller?.sellerAddress ?: "") }
    var pan by remember { mutableStateOf(seller?.panNumber ?: "") }

    var loading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var isEmailDuplicate by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .verticalScroll(rememberScrollState())
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
            onValueChange = { newEmail ->
                email = newEmail
                // First check if it's admin email
                if (AdminEmailValidator.isReservedEmail(newEmail)) {
                    emailError = AdminEmailValidator.getReservedEmailError()
                    isEmailDuplicate = true
                } else if (newEmail.isNotBlank() && newEmail != seller?.sellerEmail) {
                    // Only check database if email changed
                    AdminEmailValidator.isSellerEmailExists(newEmail) { exists ->
                        isEmailDuplicate = exists
                        emailError = if (exists) {
                            AdminEmailValidator.getDuplicateEmailError()
                        } else {
                            null
                        }
                    }
                } else if (newEmail == seller?.sellerEmail) {
                    emailError = null
                    isEmailDuplicate = false
                }
            },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
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
                // Show toast if email is duplicate or reserved
                if (isEmailDuplicate || AdminEmailValidator.isReservedEmail(email)) {
                    Toast.makeText(
                        context,
                        "Cannot save: Email already exists or is reserved",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@Button
                }

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
                            Toast.makeText(
                                context,
                                "Profile updated successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                            onBack() // Navigate back to profile
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
            enabled = !loading && !isEmailDuplicate && !AdminEmailValidator.isReservedEmail(email),
            colors = ButtonDefaults.buttonColors(containerColor = MainColor)
        ) {
            Text(text = if (loading) "Saving..." else "Save")
        }
    }
}
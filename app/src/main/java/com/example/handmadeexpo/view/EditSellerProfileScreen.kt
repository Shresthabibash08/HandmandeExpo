package com.example.handmadeexpo.view

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
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
            .background(Color(0xFFF5F7FA))
    ) {
        // Modern Header
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(4.dp, RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                // Back Button Row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color(0xFFF5F5F5), CircleShape)
                    ) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF212121)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        "Back",
                        fontSize = 14.sp,
                        color = Color(0xFF757575)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Title Row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(Color(0xFFFF9800).copy(alpha = 0.15f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Store,
                            contentDescription = null,
                            tint = Color(0xFFFF9800),
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            "Edit Shop Profile",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF212121)
                        )
                        Text(
                            "Update your shop information",
                            fontSize = 13.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
        }

        // Scrollable Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Main Form Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(2.dp, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    // Info Banner
                    Surface(
                        color = Color(0xFFFF9800).copy(alpha = 0.1f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Info,
                                contentDescription = null,
                                tint = Color(0xFFFF9800),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                "Keep your shop details accurate for better customer trust",
                                fontSize = 13.sp,
                                color = Color(0xFF424242)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Shop Name Field
                    Text(
                        "Shop Name",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF424242)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = shopName,
                        onValueChange = { shopName = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Your shop name") },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Store,
                                contentDescription = null,
                                tint = Color(0xFF9E9E9E)
                            )
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFFF9800),
                            unfocusedBorderColor = Color(0xFFE0E0E0)
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Email Field
                    Text(
                        "Email Address",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF424242)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = email,
                        onValueChange = { newEmail ->
                            email = newEmail
                            if (AdminEmailValidator.isReservedEmail(newEmail)) {
                                emailError = AdminEmailValidator.getReservedEmailError()
                                isEmailDuplicate = true
                            } else if (newEmail.isNotBlank() && newEmail != seller?.sellerEmail) {
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
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("shop@email.com") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        leadingIcon = {
                            Icon(
                                Icons.Default.Email,
                                contentDescription = null,
                                tint = if (emailError != null) Color(0xFFF44336) else Color(0xFF9E9E9E)
                            )
                        },
                        trailingIcon = {
                            if (emailError != null) {
                                Icon(
                                    Icons.Default.Error,
                                    contentDescription = "Error",
                                    tint = Color(0xFFF44336)
                                )
                            } else if (email.isNotBlank() && email == seller?.sellerEmail) {
                                Icon(
                                    Icons.Default.CheckCircle,
                                    contentDescription = "Valid",
                                    tint = Color(0xFF4CAF50)
                                )
                            }
                        },
                        isError = emailError != null,
                        supportingText = {
                            if (emailError != null) {
                                Text(
                                    emailError!!,
                                    color = Color(0xFFF44336),
                                    fontSize = 12.sp
                                )
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFFF9800),
                            unfocusedBorderColor = Color(0xFFE0E0E0),
                            errorBorderColor = Color(0xFFF44336)
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Phone Field
                    Text(
                        "Phone Number",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF424242)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("10-digit contact number") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        leadingIcon = {
                            Icon(
                                Icons.Default.Phone,
                                contentDescription = null,
                                tint = Color(0xFF9E9E9E)
                            )
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFFF9800),
                            unfocusedBorderColor = Color(0xFFE0E0E0)
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Address Field
                    Text(
                        "Shop Address",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF424242)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Street, Area, City") },
                        leadingIcon = {
                            Icon(
                                Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = Color(0xFF9E9E9E)
                            )
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFFF9800),
                            unfocusedBorderColor = Color(0xFFE0E0E0)
                        ),
                        minLines = 2,
                        maxLines = 3
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // PAN Number Field
                    Text(
                        "PAN Number",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF424242)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = pan,
                        onValueChange = { pan = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Business PAN number") },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Badge,
                                contentDescription = null,
                                tint = Color(0xFF9E9E9E)
                            )
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFFF9800),
                            unfocusedBorderColor = Color(0xFFE0E0E0)
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Warning for Email Issues
                    if (isEmailDuplicate || AdminEmailValidator.isReservedEmail(email)) {
                        Surface(
                            color = Color(0xFFFFF3E0),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = Color(0xFFFF9800),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    "Cannot save: Email is already in use or reserved",
                                    fontSize = 12.sp,
                                    color = Color(0xFFE65100),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // Error Message
                    if (errorMessage != null) {
                        Surface(
                            color = Color(0xFFFFEBEE),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Error,
                                    contentDescription = null,
                                    tint = Color(0xFFF44336),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    errorMessage!!,
                                    fontSize = 12.sp,
                                    color = Color(0xFFC62828),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // Save Button
                    Button(
                        onClick = {
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
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        enabled = !loading && !isEmailDuplicate && !AdminEmailValidator.isReservedEmail(email),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFF9800),
                            disabledContainerColor = Color(0xFFE0E0E0)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        if (loading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                Icons.Default.Save,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Save Changes",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
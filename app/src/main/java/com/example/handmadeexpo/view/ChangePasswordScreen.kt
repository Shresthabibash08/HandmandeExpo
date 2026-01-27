package com.example.handmadeexpo.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.handmadeexpo.ui.theme.MainColor
import com.example.handmadeexpo.viewmodel.BuyerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordScreen(
    viewModel: BuyerViewModel,
    onBackClick: () -> Unit,
    onPasswordChanged: () -> Unit
) {
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var currentPasswordVisible by remember { mutableStateOf(false) }
    var newPasswordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    var currentPasswordError by remember { mutableStateOf("") }
    var newPasswordError by remember { mutableStateOf("") }
    var confirmPasswordError by remember { mutableStateOf("") }

    var isLoading by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    // Modern Success Dialog
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { },
            icon = {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(Color(0xFF4CAF50).copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(28.dp)
                    )
                }
            },
            title = {
                Text(
                    "Password Changed!",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color(0xFF212121)
                )
            },
            text = {
                Text(
                    "Your password has been updated successfully. Please use your new password for future logins.",
                    fontSize = 14.sp,
                    color = Color(0xFF616161)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSuccessDialog = false
                        onPasswordChanged()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Continue", fontWeight = FontWeight.Bold)
                }
            },
            shape = RoundedCornerShape(20.dp),
            containerColor = Color.White
        )
    }

    // Modern Error Dialog
    if (showErrorDialog) {
        AlertDialog(
            onDismissRequest = { showErrorDialog = false },
            icon = {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(Color(0xFFF44336).copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Error,
                        contentDescription = null,
                        tint = Color(0xFFF44336),
                        modifier = Modifier.size(28.dp)
                    )
                }
            },
            title = {
                Text(
                    "Error",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color(0xFF212121)
                )
            },
            text = {
                Text(
                    errorMessage,
                    fontSize = 14.sp,
                    color = Color(0xFF616161)
                )
            },
            confirmButton = {
                Button(
                    onClick = { showErrorDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("OK", fontWeight = FontWeight.Bold)
                }
            },
            shape = RoundedCornerShape(20.dp),
            containerColor = Color.White
        )
    }

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
                        onClick = onBackClick,
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color(0xFFF5F5F5), CircleShape)
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
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
                            .background(MainColor.copy(alpha = 0.15f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Lock,
                            contentDescription = null,
                            tint = MainColor,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            "Change Password",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF212121)
                        )
                        Text(
                            "Update your account security",
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
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
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
                        color = MainColor.copy(alpha = 0.1f),
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
                                tint = MainColor,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                "Enter your current password and choose a new secure password",
                                fontSize = 13.sp,
                                color = Color(0xFF424242)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Current Password Field
                    Text(
                        "Current Password",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF424242)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = currentPassword,
                        onValueChange = {
                            currentPassword = it
                            currentPasswordError = ""
                        },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Enter current password") },
                        visualTransformation = if (currentPasswordVisible)
                            VisualTransformation.None
                        else
                            PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        trailingIcon = {
                            IconButton(onClick = { currentPasswordVisible = !currentPasswordVisible }) {
                                Icon(
                                    imageVector = if (currentPasswordVisible)
                                        Icons.Filled.Visibility
                                    else
                                        Icons.Filled.VisibilityOff,
                                    contentDescription = "Toggle password visibility",
                                    tint = Color(0xFF9E9E9E)
                                )
                            }
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Lock,
                                contentDescription = null,
                                tint = if (currentPasswordError.isNotEmpty()) Color(0xFFF44336) else Color(0xFF9E9E9E)
                            )
                        },
                        isError = currentPasswordError.isNotEmpty(),
                        supportingText = {
                            if (currentPasswordError.isNotEmpty()) {
                                Text(currentPasswordError, color = Color(0xFFF44336))
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MainColor,
                            unfocusedBorderColor = Color(0xFFE0E0E0)
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // New Password Field
                    Text(
                        "New Password",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF424242)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newPassword,
                        onValueChange = {
                            newPassword = it
                            newPasswordError = ""
                        },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Enter new password") },
                        visualTransformation = if (newPasswordVisible)
                            VisualTransformation.None
                        else
                            PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        trailingIcon = {
                            IconButton(onClick = { newPasswordVisible = !newPasswordVisible }) {
                                Icon(
                                    imageVector = if (newPasswordVisible)
                                        Icons.Filled.Visibility
                                    else
                                        Icons.Filled.VisibilityOff,
                                    contentDescription = "Toggle password visibility",
                                    tint = Color(0xFF9E9E9E)
                                )
                            }
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.LockOpen,
                                contentDescription = null,
                                tint = if (newPasswordError.isNotEmpty()) Color(0xFFF44336) else Color(0xFF9E9E9E)
                            )
                        },
                        isError = newPasswordError.isNotEmpty(),
                        supportingText = {
                            if (newPasswordError.isNotEmpty()) {
                                Text(newPasswordError, color = Color(0xFFF44336))
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MainColor,
                            unfocusedBorderColor = Color(0xFFE0E0E0)
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Confirm Password Field
                    Text(
                        "Confirm New Password",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF424242)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = {
                            confirmPassword = it
                            confirmPasswordError = ""
                        },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Re-enter new password") },
                        visualTransformation = if (confirmPasswordVisible)
                            VisualTransformation.None
                        else
                            PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        trailingIcon = {
                            IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                                Icon(
                                    imageVector = if (confirmPasswordVisible)
                                        Icons.Filled.Visibility
                                    else
                                        Icons.Filled.VisibilityOff,
                                    contentDescription = "Toggle password visibility",
                                    tint = Color(0xFF9E9E9E)
                                )
                            }
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = if (confirmPasswordError.isNotEmpty()) Color(0xFFF44336) else Color(0xFF9E9E9E)
                            )
                        },
                        isError = confirmPasswordError.isNotEmpty(),
                        supportingText = {
                            if (confirmPasswordError.isNotEmpty()) {
                                Text(confirmPasswordError, color = Color(0xFFF44336))
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MainColor,
                            unfocusedBorderColor = Color(0xFFE0E0E0)
                        )
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Password Requirements Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Security,
                                    contentDescription = null,
                                    tint = Color(0xFF757575),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "Password Requirements",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF424242)
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))

                            RequirementItem(
                                text = "At least 6 characters",
                                isMet = newPassword.length >= 6
                            )
                            RequirementItem(
                                text = "Different from current password",
                                isMet = newPassword.isNotEmpty() && newPassword != currentPassword
                            )
                            RequirementItem(
                                text = "Passwords match",
                                isMet = confirmPassword.isNotEmpty() && confirmPassword == newPassword
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Change Password Button
                    Button(
                        onClick = {
                            // Validate inputs
                            var isValid = true

                            if (currentPassword.isEmpty()) {
                                currentPasswordError = "Current password is required"
                                isValid = false
                            }

                            if (newPassword.isEmpty()) {
                                newPasswordError = "New password is required"
                                isValid = false
                            } else if (newPassword.length < 6) {
                                newPasswordError = "Password must be at least 6 characters"
                                isValid = false
                            } else if (newPassword == currentPassword) {
                                newPasswordError = "New password must be different from current password"
                                isValid = false
                            }

                            if (confirmPassword.isEmpty()) {
                                confirmPasswordError = "Please confirm your new password"
                                isValid = false
                            } else if (confirmPassword != newPassword) {
                                confirmPasswordError = "Passwords do not match"
                                isValid = false
                            }

                            if (isValid) {
                                isLoading = true
                                viewModel.changePassword(
                                    currentPassword = currentPassword,
                                    newPassword = newPassword
                                ) { success, message ->
                                    isLoading = false
                                    if (success) {
                                        showSuccessDialog = true
                                    } else {
                                        errorMessage = message
                                        showErrorDialog = true
                                    }
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MainColor),
                        enabled = !isLoading,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                Icons.Default.LockReset,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Change Password",
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

@Composable
private fun RequirementItem(text: String, isMet: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            if (isMet) Icons.Default.CheckCircle else Icons.Default.Circle,
            contentDescription = null,
            tint = if (isMet) Color(0xFF4CAF50) else Color(0xFFBDBDBD),
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text,
            fontSize = 12.sp,
            color = if (isMet) Color(0xFF4CAF50) else Color(0xFF757575)
        )
    }
}
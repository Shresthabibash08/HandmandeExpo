package com.example.handmadeexpo.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Blue
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.handmadeexpo.R
import com.example.handmadeexpo.model.SellerModel
import com.example.handmadeexpo.repo.SellerRepoImpl
import com.example.handmadeexpo.ui.theme.MainColor
import com.example.handmadeexpo.ui.theme.Offwhite12
import com.example.handmadeexpo.viewmodel.SellerViewModel
import com.example.handmadeexpo.utils.AdminEmailValidator

class SellerRegistration : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SellerRegisterScreen()
        }
    }
}

@Composable
fun SellerRegisterScreen() {
    val context = LocalContext.current
    val activity = context as Activity
    val viewModel = remember { SellerViewModel(SellerRepoImpl()) }

    var fullName by remember { mutableStateOf("") }
    var shopName by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var panNumber by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var passwordVisibility by remember { mutableStateOf(false) }
    var confirmPasswordVisibility by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf<String?>(null) }

    Scaffold { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(R.drawable.finalbackground),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .imePadding(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(30.dp))

                Image(
                    painter = painterResource(R.drawable.finallogo),
                    contentDescription = "Logo",
                    modifier = Modifier.size(100.dp).clip(CircleShape),
                    contentScale = ContentScale.Crop
                )

                Text(
                    "Join As Artisan",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = MainColor,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(top = 10.dp)
                )

                Text(
                    "Start selling your crafts to the world.",
                    fontSize = 16.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                CustomTextField("Full Name", fullName) { fullName = it }
                Spacer(modifier = Modifier.height(12.dp))
                CustomTextField("Shop Name", shopName) { shopName = it }
                Spacer(modifier = Modifier.height(12.dp))
                CustomTextField("Address", address) { address = it }
                Spacer(modifier = Modifier.height(12.dp))
                CustomTextField("PAN Number", panNumber) { panNumber = it }
                Spacer(modifier = Modifier.height(12.dp))

                CustomTextField("Email", email) {
                    email = it
                    if (AdminEmailValidator.isReservedEmail(it)) {
                        emailError = AdminEmailValidator.getReservedEmailError()
                    } else if (it.isNotBlank()) {
                        AdminEmailValidator.isSellerEmailExists(it) { exists ->
                            emailError = if (exists) {
                                AdminEmailValidator.getDuplicateEmailError()
                            } else {
                                null
                            }
                        }
                    } else {
                        emailError = null
                    }
                }

                if (emailError != null) {
                    Text(
                        text = emailError!!,
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
                CustomTextField("Phone Number", phoneNumber) { phoneNumber = it }
                Spacer(modifier = Modifier.height(12.dp))

                PasswordTextField(
                    label = "Password",
                    value = password,
                    isVisible = passwordVisibility,
                    onVisibilityChange = { passwordVisibility = !passwordVisibility },
                    onValueChange = { password = it }
                )
                Spacer(modifier = Modifier.height(12.dp))
                PasswordTextField(
                    label = "Confirm Password",
                    value = confirmPassword,
                    isVisible = confirmPasswordVisibility,
                    onVisibilityChange = { confirmPasswordVisibility = !confirmPasswordVisibility },
                    onValueChange = { confirmPassword = it }
                )

                Spacer(modifier = Modifier.height(30.dp))

                Button(
                    onClick = {
                        when {
                            fullName.isBlank() -> Toast.makeText(context, "Full name is required", Toast.LENGTH_SHORT).show()
                            shopName.isBlank() -> Toast.makeText(context, "Shop name is required", Toast.LENGTH_SHORT).show()
                            address.isBlank() -> Toast.makeText(context, "Address is required", Toast.LENGTH_SHORT).show()
                            panNumber.isBlank() -> Toast.makeText(context, "PAN number is required", Toast.LENGTH_SHORT).show()
                            !panNumber.matches(Regex("^[0-9]+$")) -> Toast.makeText(context, "PAN number must contain only digits", Toast.LENGTH_SHORT).show()
                            email.isBlank() -> Toast.makeText(context, "Email is required", Toast.LENGTH_SHORT).show()
                            AdminEmailValidator.isReservedEmail(email) -> Toast.makeText(context, AdminEmailValidator.getReservedEmailError(), Toast.LENGTH_SHORT).show()
                            emailError != null -> Toast.makeText(context, emailError!!, Toast.LENGTH_SHORT).show()
                            phoneNumber.isBlank() -> Toast.makeText(context, "Phone number is required", Toast.LENGTH_SHORT).show()
                            !phoneNumber.matches(Regex("^[0-9]{10}$")) -> Toast.makeText(context, "Phone number must be 10 digits", Toast.LENGTH_SHORT).show()
                            password.isBlank() -> Toast.makeText(context, "Password is required", Toast.LENGTH_SHORT).show()
                            password != confirmPassword -> Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()

                            else -> {
                                isLoading = true

                                // ✅ STEP 1: Register with Firebase Auth
                                viewModel.register(email, password) { success, msg, sellerId ->
                                    if (success) {
                                        // ✅ STEP 2: Save to Database
                                        val sellerModel = SellerModel(
                                            sellerId = sellerId,
                                            fullName = fullName,
                                            shopName = shopName,
                                            sellerAddress = address,
                                            sellerEmail = email,
                                            sellerPhoneNumber = phoneNumber,
                                            panNumber = panNumber,
                                            verificationStatus = "Unverified",
                                            documentType = "",      // ✅ Will be filled in verification
                                            documentUrl = "",       // ✅ Will be filled in verification
                                            role = "seller",
                                            banned = false
                                        )

                                        viewModel.addSellerToDatabase(sellerId, sellerModel) { dbSuccess, dbMsg ->
                                            isLoading = false

                                            if (dbSuccess) {
                                                // ✅ SUCCESS - Navigate to verification
                                                Toast.makeText(
                                                    context,
                                                    "Account created! Please verify your identity.",
                                                    Toast.LENGTH_SHORT
                                                ).show()

                                                val intent = Intent(context, SellerVerificationActivity::class.java)
                                                intent.putExtra("sellerId", sellerId)  // ✅ Pass seller ID
                                                context.startActivity(intent)
                                                activity.finish()
                                            } else {
                                                // ❌ Database save failed
                                                Toast.makeText(
                                                    context,
                                                    "Registration failed: $dbMsg",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                            }
                                        }
                                    } else {
                                        // ❌ Auth registration failed
                                        isLoading = false
                                        Toast.makeText(
                                            context,
                                            "Registration failed: $msg",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(60.dp).padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MainColor),
                    enabled = !isLoading && !AdminEmailValidator.isReservedEmail(email)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Proceed to Verify Identity", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(painter = painterResource(R.drawable.baseline_arrow_forward_24), contentDescription = null)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Already have an account ?", fontSize = 16.sp, color = MainColor)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Sign In",
                        color = Blue,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable {
                            val intent = Intent(context, SignInActivity::class.java)
                            context.startActivity(intent)
                        }
                    )
                }
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun CustomTextField(label: String, value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MainColor,
            unfocusedBorderColor = MainColor,
            focusedLabelColor = MainColor,
            focusedContainerColor = Offwhite12,
            unfocusedContainerColor = Offwhite12
        )
    )
}

@Composable
fun PasswordTextField(
    label: String, value: String, isVisible: Boolean,
    onVisibilityChange: () -> Unit, onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        visualTransformation = if (isVisible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            IconButton(onClick = onVisibilityChange) {
                Icon(
                    painter = painterResource(if (isVisible) R.drawable.baseline_visibility_off_24 else R.drawable.baseline_visibility_24),
                    contentDescription = null
                )
            }
        },
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MainColor,
            unfocusedBorderColor = MainColor,
            focusedLabelColor = MainColor,
            focusedContainerColor = Offwhite12,
            unfocusedContainerColor = Offwhite12
        )
    )
}
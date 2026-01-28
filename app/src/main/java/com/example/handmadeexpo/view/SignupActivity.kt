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
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.handmadeexpo.R
import com.example.handmadeexpo.model.BuyerModel
import com.example.handmadeexpo.repo.BuyerRepoImpl
import com.example.handmadeexpo.ui.theme.MainColor
import com.example.handmadeexpo.ui.theme.Offwhite12
import com.example.handmadeexpo.viewmodel.BuyerViewModel
import com.example.handmadeexpo.utils.AdminEmailValidator

class SignupActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SignupBody()
        }
    }
}

@Composable
fun SignupBody() {
    val buyerViewModel = remember { BuyerViewModel(BuyerRepoImpl()) }
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var terms by remember { mutableStateOf(false) }
    var visibility by remember { mutableStateOf(false) }
    var confirmVisibility by remember { mutableStateOf(false) }
    var address by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val activity = context as? Activity

    Scaffold { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            // Background Image
            Image(
                painter = painterResource(R.drawable.img_1),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .imePadding()
            ) {
                Spacer(modifier = Modifier.height(30.dp))

                // Logo Section
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(R.drawable.img_2),
                        contentDescription = "Logo",
                        modifier = Modifier
                            .clip(CircleShape)
                            .size(150.dp)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        "Create Your Account",
                        fontSize = 28.sp,
                        color = MainColor,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Input Fields
                AppOutlinedTextField(
                    label = "Full Name",
                    value = fullName,
                    onValueChange = { fullName = it }
                )

                AppOutlinedTextField(
                    label = "Email",
                    value = email,
                    onValueChange = { newEmail ->
                        email = newEmail
                        // First check if it's admin email
                        if (AdminEmailValidator.isReservedEmail(newEmail)) {
                            emailError = AdminEmailValidator.getReservedEmailError()
                        } else if (newEmail.isNotBlank()) {
                            // Then check if email exists in database
                            AdminEmailValidator.isBuyerEmailExists(newEmail) { exists ->
                                emailError = if (exists) {
                                    AdminEmailValidator.getDuplicateEmailError()
                                } else {
                                    null
                                }
                            }
                        } else {
                            emailError = null
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )

                if (emailError != null) {
                    Text(
                        text = emailError!!,
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                }

                AppOutlinedTextField(
                    label = "Phone Number",
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                )

                AppOutlinedTextField(
                    label = "Address",
                    value = address,
                    onValueChange = { address = it }
                )

                PasswordTextField(
                    label = "Password",
                    value = password,
                    onValueChange = { password = it },
                    isPasswordVisible = visibility,
                    onTogglePasswordVisibility = { visibility = !visibility }
                )

                PasswordTextField(
                    label = "Confirm Password",
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    isPasswordVisible = confirmVisibility,
                    onTogglePasswordVisibility = { confirmVisibility = !confirmVisibility }
                )

                // Terms and Conditions Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = terms,
                        onCheckedChange = { terms = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = MainColor,
                            checkmarkColor = Color.White
                        )
                    )
                    Text("I agree to Terms & Conditions", fontSize = 14.sp)
                }

                // Sign Up Button
                Button(
                    onClick = {
                        when {
                            fullName.isBlank() -> Toast.makeText(context, "Full name required", Toast.LENGTH_SHORT).show()
                            email.isBlank() -> Toast.makeText(context, "Email required", Toast.LENGTH_SHORT).show()
                            AdminEmailValidator.isReservedEmail(email) -> Toast.makeText(context, AdminEmailValidator.getReservedEmailError(), Toast.LENGTH_SHORT).show()
                            phoneNumber.length != 10 -> Toast.makeText(context, "Enter valid 10-digit phone", Toast.LENGTH_SHORT).show()
                            password.length < 6 -> Toast.makeText(context, "Password too short", Toast.LENGTH_SHORT).show()
                            password != confirmPassword -> Toast.makeText(context, "Passwords mismatch", Toast.LENGTH_SHORT).show()
                            !terms -> Toast.makeText(context, "Accept terms to continue", Toast.LENGTH_SHORT).show()
                            else -> {
                                buyerViewModel.register(email, password) { success, msg, userId ->
                                    if (success && userId != null) {
                                        // ✅ FIXED: Correct parameter order
                                        val model = BuyerModel(
                                            buyerId = userId,
                                            buyerName = fullName,
                                            buyerEmail = email,
                                            buyerPhoneNumber = phoneNumber,  // ✅ Phone goes to phoneNumber
                                            buyerAddress = address,          // ✅ Address goes to address
                                            role = "buyer",
                                            banned = false
                                        )
                                        buyerViewModel.addBuyerToDatabase(userId, model) { dbSuccess, dbMsg ->
                                            if (dbSuccess) {
                                                context.startActivity(Intent(context, SignInActivity::class.java))
                                                activity?.finish()
                                            }
                                            Toast.makeText(context, dbMsg, Toast.LENGTH_SHORT).show()
                                        }
                                    } else {
                                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MainColor),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(90.dp)
                        .padding(horizontal = 20.dp, vertical = 15.dp)
                ) {
                    Text("Sign Up", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }

                // Sign In Redirection
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 30.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Already have an account ? ", color = Color.Black)
                    Text(
                        text = "Sign In",
                        color = Blue,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable {
                            context.startActivity(Intent(context, SignInActivity::class.java))
                            activity?.finish()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun AppOutlinedTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        keyboardOptions = keyboardOptions,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Offwhite12,
            unfocusedContainerColor = Offwhite12,
            focusedIndicatorColor = MainColor,
            unfocusedIndicatorColor = Color.LightGray,
            focusedLabelColor = MainColor,
            cursorColor = MainColor
        )
    )
}

@Composable
fun PasswordTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    isPasswordVisible: Boolean,
    onTogglePasswordVisibility: () -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            IconButton(onClick = onTogglePasswordVisibility) {
                Icon(
                    painter = painterResource(
                        if (isPasswordVisible) R.drawable.baseline_visibility_off_24
                        else R.drawable.baseline_visibility_24
                    ),
                    contentDescription = null
                )
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Offwhite12,
            unfocusedContainerColor = Offwhite12,
            focusedIndicatorColor = MainColor,
            unfocusedIndicatorColor = Color.LightGray,
            focusedLabelColor = MainColor,
            cursorColor = MainColor
        )
    )
}

@Preview(showBackground = true)
@Composable
fun SignupPreview() {
    SignupBody()
}
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
import androidx.compose.ui.graphics.Color.Companion.Blue
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
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
import com.example.handmadeexpo.ui.theme.White12
import com.example.handmadeexpo.viewmodel.SellerViewModel

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
    var address by remember { mutableStateOf("") }
    var shopName by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var passwordVisibility by remember { mutableStateOf(false) }
    var confirmPasswordVisibility by remember { mutableStateOf(false) }
    var confirmPassword by remember { mutableStateOf("") }
    var panNumber by remember { mutableStateOf("") }
    val context = LocalContext.current
    val activity = context as Activity
    var sellerViewModel = remember{ SellerViewModel(SellerRepoImpl()) }

    Scaffold { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            // Background Image
            Image(
                painter = painterResource(R.drawable.finalbackground),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Content Column
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .imePadding()
                ,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Image(
                    painter = painterResource(R.drawable.finallogo),
                    contentDescription = null,
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )

                Text(
                    "Join As Artisan",
                    fontSize = 28.sp,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                    color = MainColor,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    "Start selling your crafts to the world.",
                    fontSize = 16.sp,
                    color = androidx.compose.ui.graphics.Color.Gray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Text Fields
                CustomTextField("Shop Name", shopName) { shopName = it }
                Spacer(modifier = Modifier.height(15.dp))
                CustomTextField("Address", address) { address = it }
                Spacer(modifier = Modifier.height(15.dp))
                CustomTextField("PAN Number", panNumber) { panNumber = it }
                Spacer(modifier = Modifier.height(15.dp))
                CustomTextField("Email", email) { email = it }
                Spacer(modifier = Modifier.height(15.dp))
                CustomTextField("Phone Number", phoneNumber) { phoneNumber = it }
                Spacer(modifier = Modifier.height(15.dp))
                PasswordTextField(
                    label = "Password",
                    value = password,
                    isVisible = passwordVisibility,
                    onVisibilityChange = { passwordVisibility = !passwordVisibility },
                    onValueChange = { password = it }
                )
                Spacer(modifier = Modifier.height(15.dp))
                PasswordTextField(
                    label = "Confirm Password",
                    value = confirmPassword,
                    isVisible = confirmPasswordVisibility,
                    onVisibilityChange = { confirmPasswordVisibility = !confirmPasswordVisibility },
                    onValueChange = { confirmPassword = it }
                )

                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = {

                        when {
                            shopName.isBlank() ->
                                Toast.makeText(context, "Shop name is required", Toast.LENGTH_SHORT).show()

                            address.isBlank() ->
                                Toast.makeText(context, "Address is required", Toast.LENGTH_SHORT).show()

                            panNumber.isBlank() ->
                                Toast.makeText(context, "PAN number is required", Toast.LENGTH_SHORT).show()

                            !panNumber.matches(Regex("^[0-9]+$")) ->
                                Toast.makeText(context, "PAN number must contain only digits", Toast.LENGTH_SHORT).show()

                            email.isBlank() ->
                                Toast.makeText(context, "Email is required", Toast.LENGTH_SHORT).show()

                            phoneNumber.isBlank() ->
                                Toast.makeText(context, "Phone number is required", Toast.LENGTH_SHORT).show()

                            !phoneNumber.matches(Regex("^[0-9]{10}$")) ->
                                Toast.makeText(context, "Phone number must be exactly 10 digits", Toast.LENGTH_SHORT).show()

                            password.isBlank() ->
                                Toast.makeText(context, "Password is required", Toast.LENGTH_SHORT).show()

                            confirmPassword.isBlank() ->
                                Toast.makeText(context, "Confirm password is required", Toast.LENGTH_SHORT).show()

                            password != confirmPassword ->
                                Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()

                            else -> {
                                // ✅ All validations passed (UNCHANGED)
                                sellerViewModel.register(email, password) { success, msg, sellerId ->
                                    if (success) {
                                        val sellerModel = SellerModel(
                                            sellerId = sellerId,
                                            shopName = shopName,
                                            sellerAddress = address,
                                            sellerEmail = email,
                                            sellerPhoneNumber = phoneNumber,
                                            panNumber = panNumber
                                        )

                                        sellerViewModel.addSellerToDatabase(
                                            sellerId,
                                            sellerModel
                                        ) { dbSuccess, dbMsg ->
                                            Toast.makeText(context, dbMsg, Toast.LENGTH_SHORT).show()
                                            if (dbSuccess) activity.finish()
                                        }
                                    } else {
                                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MainColor),
                    shape = RoundedCornerShape(12.dp),
                    elevation = ButtonDefaults.buttonElevation(6.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp)
                        .padding(horizontal = 16.dp)
                ) {
                    Text("Register", fontSize = 18.sp, color = androidx.compose.ui.graphics.Color.White)
                }

                Spacer(modifier = Modifier.height(10.dp))
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 50.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text("Already have an account?", fontSize = 16.sp, color = MainColor)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Sign In",
                        color = Blue,
                        fontSize = 16.sp,
                        modifier = Modifier
                            .clickable {
                                val intent = Intent(context, SignInActivity::class.java)
                                activity.startActivity(intent)
                            }
                    )
                }
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
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MainColor,
            unfocusedBorderColor = MainColor,
            focusedLabelColor = MainColor,
            cursorColor = MainColor,
            focusedContainerColor = Offwhite12,
            unfocusedContainerColor = Offwhite12
        )
    )
}

@Composable
fun PasswordTextField(
    label: String,
    value: String,
    isVisible: Boolean,
    onVisibilityChange: () -> Unit,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        visualTransformation = if (isVisible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            IconButton(onClick = onVisibilityChange) {
                Icon(
                    painter = painterResource(
                        if (isVisible) R.drawable.baseline_visibility_off_24
                        else R.drawable.baseline_visibility_24
                    ),
                    contentDescription = null
                )
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MainColor,
            unfocusedBorderColor = MainColor,
            focusedLabelColor = White12,
            cursorColor = MainColor,
            focusedContainerColor = Offwhite12,
            unfocusedContainerColor = Offwhite12

        )
    )
}
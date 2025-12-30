package com.example.handmadeexpo.view

import android.content.Intent
import android.os.Bundle
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.handmadeexpo.R
import com.example.handmadeexpo.ui.theme.MainColor
import com.example.handmadeexpo.ui.theme.Offwhite12
import com.example.handmadeexpo.ui.theme.White12

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

    // --- Form State ---
    var name by remember { mutableStateOf("") }
    var shopname by remember { mutableStateOf("") }
    var pannumber by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var contact by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmpassword by remember { mutableStateOf("") }

    // --- Visibility State ---
    var passwordVisibility by remember { mutableStateOf(false) }
    var confirmPasswordVisibility by remember { mutableStateOf(false) }

    Scaffold { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            // 1. Background Image
            Image(
                painter = painterResource(R.drawable.finalbackground),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // 2. Scrollable Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .imePadding(), // Handles keyboard overlap
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // --- Logo Section ---
                Spacer(modifier = Modifier.height(30.dp))
                Image(
                    painter = painterResource(R.drawable.finallogo),
                    contentDescription = null,
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape),
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                // --- Form Fields ---
                CustomTextField("Full Name", name) { name = it }
                Spacer(modifier = Modifier.height(12.dp))
                CustomTextField("Shop Name", shopname) { shopname = it }
                Spacer(modifier = Modifier.height(12.dp))
                CustomTextField("PAN Number", pannumber) { pannumber = it }
                Spacer(modifier = Modifier.height(12.dp))
                CustomTextField("Email", email) { email = it }
                Spacer(modifier = Modifier.height(12.dp))
                CustomTextField("Phone Number", contact) { contact = it }
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
                    value = confirmpassword,
                    isVisible = confirmPasswordVisibility,
                    onVisibilityChange = { confirmPasswordVisibility = !confirmPasswordVisibility },
                    onValueChange = { confirmpassword = it }
                )

                Spacer(modifier = Modifier.height(30.dp))

                // --- NEW "Proceed to Verification" CARD ---
                Card(
                    onClick = {
                        // Navigation Logic
                        val intent = Intent(context, SellerVerificationActivity::class.java)
                        context.startActivity(intent)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MainColor),
                    elevation = CardDefaults.cardElevation(6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Proceed to Verify Identity",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(12.dp))

                        // Icon using Drawable Resource
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_arrow_forward_24), // Ensure this file exists!
                            contentDescription = "Next",
                            tint = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // --- Footer ---
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Already have an account?", fontSize = 16.sp, color = MainColor)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Sign In",
                        color = Blue,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable {
                            // Navigation to Login if needed
                            // context.startActivity(Intent(context, SignInActivity::class.java))
                        }
                    )
                }
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

// --- Helper Components ---

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

@Preview(showBackground = true)
@Composable
fun SellerRegPreview() {
    SellerRegisterScreen()
}
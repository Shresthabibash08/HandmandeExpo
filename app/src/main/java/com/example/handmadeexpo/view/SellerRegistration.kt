package com.example.handmadeexpo.view

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
import androidx.compose.ui.graphics.Color.Companion.Blue
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
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
    var name by remember { mutableStateOf("") }
    var shopname by remember { mutableStateOf("") }
    var pannumber by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var contact by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmpassword by remember { mutableStateOf("") }
    var passwordVisibility by remember { mutableStateOf(false) }
    var confirmPasswordVisibility by remember { mutableStateOf(false) }

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
                CustomTextField("Full Name", name) { name = it }
                Spacer(modifier = Modifier.height(15.dp))
                CustomTextField("Shop Name", shopname) { shopname = it }
                Spacer(modifier = Modifier.height(15.dp))
                CustomTextField("PAN Number", pannumber) { pannumber = it }
                Spacer(modifier = Modifier.height(15.dp))
                CustomTextField("Email", email) { email = it }
                Spacer(modifier = Modifier.height(15.dp))
                CustomTextField("Phone Number", contact) { contact = it }
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
                    value = confirmpassword,
                    isVisible = confirmPasswordVisibility,
                    onVisibilityChange = { confirmPasswordVisibility = !confirmPasswordVisibility },
                    onValueChange = { confirmpassword = it }
                )

                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = { /* Register action */ },
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
                            .clickable { }
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

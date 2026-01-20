package com.example.handmadeexpo.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.Blue
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.handmadeexpo.R
import com.example.handmadeexpo.repo.BuyerRepoImpl
import com.example.handmadeexpo.ui.theme.Blue1
import com.example.handmadeexpo.ui.theme.MainColor
import com.example.handmadeexpo.viewmodel.BuyerViewModel

// Admin credentials (change these to your desired strong credentials)
object AdminCredentials {
    const val ADMIN_EMAIL = "admin@handmadeexpo.com"
    const val ADMIN_PASSWORD = "Handmade@Expo2024#Secure"
}

class SignInActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SignInBody()
        }
    }
}

@Composable
fun SignInBody() {
    val buyerViewModel = remember { BuyerViewModel(BuyerRepoImpl()) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var visibility by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val activity = context as? Activity

    Scaffold { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(R.drawable.img_1),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                item {
                    Spacer(modifier = Modifier.height(50.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Image(
                            painter = painterResource(R.drawable.logo),
                            contentDescription = "Logo",
                            modifier = Modifier
                                .size(177.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    }
                    Text(
                        "Handmade Expo",
                        modifier = Modifier.fillMaxWidth(),
                        style = TextStyle(
                            color = Black,
                            fontWeight = FontWeight.ExtraBold,
                            textAlign = TextAlign.Center,
                            fontSize = 32.sp
                        )
                    )

                    Text(
                        "Welcome Back!",
                        modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                        style = TextStyle(
                            color = Black,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center,
                            fontSize = 20.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 15.dp),
                        placeholder = { Text("Email/Phone") },
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = Blue1,
                            unfocusedIndicatorColor = Color.Transparent
                        )
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        shape = RoundedCornerShape(12.dp),
                        visualTransformation = if (!visibility) PasswordVisualTransformation() else VisualTransformation.None,
                        trailingIcon = {
                            IconButton(onClick = { visibility = !visibility }) {
                                Icon(
                                    painter = painterResource(
                                        if (visibility) R.drawable.baseline_visibility_off_24
                                        else R.drawable.baseline_visibility_24
                                    ),
                                    contentDescription = null
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 15.dp),
                        placeholder = { Text("*********") },
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = Blue1,
                            unfocusedIndicatorColor = Color.Transparent
                        )
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 15.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Text(
                            "Forgot password?", fontSize = 16.sp,
                            modifier = Modifier.clickable {
                                val intent = Intent(context, ForgetPasswordActivity::class.java)
                                activity?.startActivity(intent)
                            }
                        )
                    }

                    Button(
                        onClick = {
                            if (email.isBlank() || password.isBlank()) {
                                Toast.makeText(context, "Email and password required", Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            // Check if admin credentials
                            if (email == AdminCredentials.ADMIN_EMAIL && password == AdminCredentials.ADMIN_PASSWORD) {
                                val intent = Intent(context, AdminDashboardActivity::class.java)
                                context.startActivity(intent)
                                activity?.finish()
                                return@Button
                            }

                            // Regular buyer/seller login
                            buyerViewModel.login(email, password) { success, msg ->
                                if (success) {
                                    val userId = buyerViewModel.getCurrentUser()?.uid
                                    if (userId != null) {
                                        buyerViewModel.checkUserRole(userId) { role ->
                                            when (role) {
                                                "buyer" -> {
                                                    val intent = Intent(context, DashboardActivity::class.java)
                                                    intent.putExtra("userId", userId)
                                                    context.startActivity(intent)
                                                    activity?.finish()
                                                }
                                                "seller" -> {
                                                    val intent = Intent(context, SellerDashboard::class.java)
                                                    intent.putExtra("userId", userId)
                                                    context.startActivity(intent)
                                                    activity?.finish()
                                                }
                                                else -> {
                                                    Toast.makeText(context, "User role not defined", Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                        }
                                    } else {
                                        Toast.makeText(context, "User not found", Toast.LENGTH_SHORT).show()
                                    }
                                } else {
                                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(95.dp)
                            .padding(horizontal = 20.dp, vertical = 20.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MainColor)
                    ) {
                        Text("Sign In", fontSize = 15.sp)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth().padding(start = 30.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Text("Don't have an account? Register as buyer", fontSize = 16.sp, color = Black)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Sign Up",
                            color = Blue,
                            fontSize = 16.sp,
                            modifier = Modifier.clickable {
                                val intent = Intent(context, SignupActivity::class.java)
                                activity?.startActivity(intent)
                                activity?.finish()
                            }
                        )
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(start = 30.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Text("Don't have an account? Register as seller", fontSize = 16.sp, color = Black)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Sign Up",
                            color = Blue,
                            fontSize = 16.sp,
                            modifier = Modifier.clickable {
                                val intent = Intent(context, SellerRegistration::class.java)
                                activity?.startActivity(intent)
                                activity?.finish()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SignInPreview() {
    SignInBody()
}
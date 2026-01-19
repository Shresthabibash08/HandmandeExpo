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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.Blue
import androidx.compose.ui.graphics.Color.Companion.Gray
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

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
            // Background Image
            Image(
                painter = painterResource(R.drawable.img_1),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Content Scroll
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    Spacer(modifier = Modifier.height(60.dp))

                    // 1. Logo Section
                    Image(
                        painter = painterResource(R.drawable.logo),
                        contentDescription = "Logo",
                        modifier = Modifier
                            .size(150.dp)
                            .clip(CircleShape)
                            .padding(bottom = 16.dp),
                        contentScale = ContentScale.Crop
                    )

                    // 2. Headings
                    Text(
                        text = "Handmade Expo",
                        style = TextStyle(
                            color = Black,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 30.sp,
                            textAlign = TextAlign.Center
                        )
                    )

                    Text(
                        text = "Welcome Back!",
                        modifier = Modifier.padding(top = 8.dp, bottom = 32.dp),
                        style = TextStyle(
                            color = Black,
                            fontWeight = FontWeight.Medium,
                            fontSize = 18.sp,
                            textAlign = TextAlign.Center
                        )
                    )

                    // 3. Inputs
                    // Email
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        placeholder = { Text("Email Address") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.Email,
                                contentDescription = "Email Icon",
                                tint = MainColor
                            )
                        },
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = Blue1,
                            unfocusedIndicatorColor = Color.Gray.copy(alpha = 0.5f),
                            focusedContainerColor = Color.White.copy(alpha = 0.9f),
                            unfocusedContainerColor = Color.White.copy(alpha = 0.7f)
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Password
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        visualTransformation = if (!visibility) PasswordVisualTransformation() else VisualTransformation.None,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        placeholder = { Text("Password") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.Lock,
                                contentDescription = "Lock Icon",
                                tint = MainColor
                            )
                        },
                        trailingIcon = {
                            IconButton(onClick = { visibility = !visibility }) {
                                Icon(
                                    painter = painterResource(
                                        if (visibility) R.drawable.baseline_visibility_off_24
                                        else R.drawable.baseline_visibility_24
                                    ),
                                    contentDescription = null,
                                    tint = Color.Gray
                                )
                            }
                        },
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = Blue1,
                            unfocusedIndicatorColor = Color.Gray.copy(alpha = 0.5f),
                            focusedContainerColor = Color.White.copy(alpha = 0.9f),
                            unfocusedContainerColor = Color.White.copy(alpha = 0.7f)
                        )
                    )

                    // Forgot Password
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 12.dp),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        Text(
                            text = "Forgot password?",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Black,
                            modifier = Modifier.clickable {
                                val intent = Intent(context, ForgetPasswordActivity::class.java)
                                activity?.startActivity(intent)
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 4. Sign In Button
                    Button(
                        onClick = {
                            if (email.isBlank() || password.isBlank()) {
                                Toast.makeText(context, "Email and password required", Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            buyerViewModel.login(email, password) { success, msg ->
                                if (success) {
                                    val userId = buyerViewModel.getCurrentUser()?.uid
                                    if (userId != null) {
                                        buyerViewModel.checkUserRole(userId) { role ->
                                            val db = FirebaseDatabase.getInstance()

                                            when (role) {
                                                "buyer" -> {
                                                    // --- NEW: CHECK IF BUYER IS BANNED ---
                                                    db.getReference("Buyer").child(userId).get()
                                                        .addOnSuccessListener { snapshot ->
                                                            val isBanned = snapshot.child("banned").getValue(Boolean::class.java) ?: false

                                                            if (isBanned) {
                                                                // Buyer is BANNED
                                                                FirebaseAuth.getInstance().signOut()
                                                                Toast.makeText(context, "ACCOUNT SUSPENDED: Please contact admin.", Toast.LENGTH_LONG).show()
                                                            } else {
                                                                // Buyer is ACTIVE -> Go to Dashboard
                                                                val intent = Intent(context, DashboardActivity::class.java)
                                                                intent.putExtra("userId", userId)
                                                                context.startActivity(intent)
                                                                activity?.finish()
                                                            }
                                                        }
                                                        .addOnFailureListener {
                                                            Toast.makeText(context, "Error checking verification status", Toast.LENGTH_SHORT).show()
                                                        }
                                                }
                                                "seller" -> {
                                                    // --- EXISTING: CHECK IF SELLER IS BANNED ---
                                                    db.getReference("Seller").child(userId).get()
                                                        .addOnSuccessListener { snapshot ->
                                                            // Check if "banned" is true
                                                            val isBanned = snapshot.child("banned").getValue(Boolean::class.java) ?: false

                                                            if (isBanned) {
                                                                // User is BANNED: Sign out and show error
                                                                FirebaseAuth.getInstance().signOut()
                                                                Toast.makeText(context, "ACCOUNT SUSPENDED: Please contact admin.", Toast.LENGTH_LONG).show()
                                                            } else {
                                                                // User is SAFE: Proceed to dashboard
                                                                val intent = Intent(context, SellerDashboard::class.java)
                                                                intent.putExtra("userId", userId)
                                                                context.startActivity(intent)
                                                                activity?.finish()
                                                            }
                                                        }
                                                        .addOnFailureListener {
                                                            Toast.makeText(context, "Error checking account status", Toast.LENGTH_SHORT).show()
                                                        }
                                                }
                                                "admin" -> {
                                                    // Admin is never banned (usually)
                                                    val intent = Intent(context, AdminDashboardActivity::class.java)
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
                            .height(56.dp)
                            .padding(horizontal = 24.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MainColor),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                    ) {
                        Text(
                            text = "Sign In",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(30.dp))

                    // 5. Divider
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 40.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        HorizontalDivider(modifier = Modifier.weight(1f), color = Gray.copy(alpha = 0.5f))
                        Text(
                            text = "OR",
                            modifier = Modifier.padding(horizontal = 10.dp),
                            fontSize = 12.sp,
                            color = Gray
                        )
                        HorizontalDivider(modifier = Modifier.weight(1f), color = Gray.copy(alpha = 0.5f))
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // 6. Registration Links

                    // Buyer Link
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "New to Handmade Expo? ",
                            fontSize = 14.sp,
                            color = Black
                        )
                        Text(
                            text = "Register as Buyer",
                            color = Blue,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable {
                                val intent = Intent(context, SignupActivity::class.java)
                                activity?.startActivity(intent)
                                activity?.finish()
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Seller Link
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Want to sell your art? ",
                            fontSize = 14.sp,
                            color = Black
                        )
                        Text(
                            text = "Join as Seller",
                            color = Blue,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable {
                                val intent = Intent(context, SellerRegistration::class.java)
                                activity?.startActivity(intent)
                                activity?.finish()
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(50.dp))
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
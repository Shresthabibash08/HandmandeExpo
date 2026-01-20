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
    var isLoading by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val activity = context as? Activity

    Scaffold { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            // Background
            Image(
                painter = painterResource(R.drawable.img_1),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    Spacer(modifier = Modifier.height(60.dp))

                    // Logo
                    Image(
                        painter = painterResource(R.drawable.logo),
                        contentDescription = "Logo",
                        modifier = Modifier
                            .size(150.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )

                    Text(
                        text = "Handmade Expo",
                        modifier = Modifier.padding(top = 16.dp),
                        style = TextStyle(
                            color = Black,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 30.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // Email Field
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        singleLine = true,
                        enabled = !isLoading,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                        shape = RoundedCornerShape(12.dp),
                        leadingIcon = { Icon(Icons.Filled.Email, contentDescription = null, tint = MainColor) }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Password Field
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        singleLine = true,
                        enabled = !isLoading,
                        visualTransformation = if (!visibility) PasswordVisualTransformation() else VisualTransformation.None,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                        shape = RoundedCornerShape(12.dp),
                        leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null, tint = MainColor) },
                        trailingIcon = {
                            IconButton(onClick = { visibility = !visibility }) {
                                Icon(
                                    painter = painterResource(if (visibility) R.drawable.baseline_visibility_off_24 else R.drawable.baseline_visibility_24),
                                    contentDescription = null
                                )
                            }
                        }
                    )

                    // Forgot Password
                    Text(
                        text = "Forgot password?",
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(end = 24.dp, top = 8.dp)
                            .clickable { context.startActivity(Intent(context, ForgetPasswordActivity::class.java)) },
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // Sign In Button
                    Button(
                        onClick = {
                            if (email.isBlank() || password.isBlank()) {
                                Toast.makeText(context, "Fields cannot be empty", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            isLoading = true
                            buyerViewModel.login(email, password) { success, msg ->
                                if (success) {
                                    val userId = buyerViewModel.getCurrentUser()?.uid
                                    if (userId != null) {
                                        handleUserRouting(userId, buyerViewModel, context, activity) {
                                            isLoading = false
                                        }
                                    }
                                } else {
                                    isLoading = false
                                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp).padding(horizontal = 24.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MainColor),
                        enabled = !isLoading
                    ) {
                        if (isLoading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        else Text("Sign In", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Registration Links
                    RegistrationFooter(context)
                }
            }
        }
    }
}

// Helper function to handle the complex logic of routing
private fun handleUserRouting(userId: String, viewModel: BuyerViewModel, context: android.content.Context, activity: Activity?, onComplete: () -> Unit) {
    viewModel.checkUserRole(userId) { role ->
        val db = FirebaseDatabase.getInstance()
        when (role) {
            "buyer" -> {
                db.getReference("Buyer").child(userId).get().addOnSuccessListener { snapshot ->
                    val isBanned = snapshot.child("banned").getValue(Boolean::class.java) ?: false
                    if (isBanned) {
                        FirebaseAuth.getInstance().signOut()
                        Toast.makeText(context, "ACCOUNT SUSPENDED", Toast.LENGTH_LONG).show()
                    } else {
                        context.startActivity(Intent(context, DashboardActivity::class.java))
                        activity?.finish()
                    }
                    onComplete()
                }
            }
            "seller" -> {
                db.getReference("Seller").child(userId).get().addOnSuccessListener { snapshot ->
                    val isBanned = snapshot.child("banned").getValue(Boolean::class.java) ?: false
                    val status = snapshot.child("verificationStatus").getValue(String::class.java) ?: "Unverified"

                    if (isBanned) {
                        FirebaseAuth.getInstance().signOut()
                        Toast.makeText(context, "ACCOUNT SUSPENDED", Toast.LENGTH_LONG).show()
                    } else if (status == "Verified") {
                        context.startActivity(Intent(context, SellerDashboard::class.java))
                        activity?.finish()
                    } else {
                        FirebaseAuth.getInstance().signOut()
                        Toast.makeText(context, "Status: $status. Please wait for admin approval.", Toast.LENGTH_LONG).show()
                    }
                    onComplete()
                }
            }
            "admin" -> {
                context.startActivity(Intent(context, AdminDashboardActivity::class.java))
                activity?.finish()
                onComplete()
            }
            else -> {
                Toast.makeText(context, "Role not found", Toast.LENGTH_SHORT).show()
                onComplete()
            }
        }
    }
}

@Composable
fun RegistrationFooter(context: android.content.Context) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row {
            Text("New to Handmade Expo? ", fontSize = 14.sp)
            Text("Register as Buyer", color = Blue, fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { context.startActivity(Intent(context, SignupActivity::class.java)) })
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row {
            Text("Want to sell? ", fontSize = 14.sp)
            Text("Join as Seller", color = Blue, fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { context.startActivity(Intent(context, SellerRegistration::class.java)) })
        }
    }
}

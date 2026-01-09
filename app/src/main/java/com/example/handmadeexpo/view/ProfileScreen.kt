package com.example.handmadeexpo.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.handmadeexpo.R
import com.example.handmadeexpo.ui.theme.MainColor
import com.example.handmadeexpo.viewmodel.BuyerViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun BuyerProfileScreen(
    viewModel: BuyerViewModel,
    onEditClick: () -> Unit,
    onChangePasswordClick: () -> Unit,
    onLogoutSuccess: () -> Unit
) {
    val buyer by viewModel.buyer.observeAsState()
    val loading by viewModel.loading.observeAsState(false)
    val buyerId = FirebaseAuth.getInstance().currentUser?.uid

    // Logout and Dialog States
    var showLogoutDialog by remember { mutableStateOf(false) }
    var isLoggingOut by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        buyerId?.let {
            viewModel.getBuyerDetailsById(it)
        }
    }

    // Logout Confirmation Dialog
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Logout") },
            text = { Text("Are you sure you want to logout?") },
            confirmButton = {
                Button(
                    onClick = {
                        isLoggingOut = true
                        viewModel.logout { success, _ ->
                            isLoggingOut = false
                            showLogoutDialog = false
                            if (success) {
                                onLogoutSuccess()
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MainColor),
                    enabled = !isLoggingOut
                ) {
                    if (isLoggingOut) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    } else {
                        Text("Logout")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Background Image
        Image(
            painter = painterResource(R.drawable.bg10),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        when {
            loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MainColor)
                }
            }

            buyer != null -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    Text(
                        text = "Your Profile",
                        style = TextStyle(
                            fontSize = 35.sp,
                            fontWeight = FontWeight.Bold,
                            color = MainColor
                        ),
                        modifier = Modifier.padding(20.dp)
                    )

                    Image(
                        painter = painterResource(R.drawable.profilephoto),
                        contentDescription = null,
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                    )

                    Text(
                        text = buyer?.buyerName ?: "",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(8.dp)
                    )

                    Button(
                        onClick = onEditClick,
                        colors = ButtonDefaults.buttonColors(containerColor = MainColor)
                    ) {
                        Text("Edit Profile")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Profile Details Card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            BuyerProfileRow("Email", buyer?.buyerEmail ?: "")
                            BuyerProfileRow("Phone", buyer?.buyerPhoneNumber ?: "")
                            BuyerProfileRow("Address", buyer?.buyerAddress ?: "")
                        }
                    }

                    // Action Buttons Card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            OutlinedButton(
                                onClick = onChangePasswordClick,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = MainColor)
                            ) {
                                Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Change Password", fontWeight = FontWeight.Medium, fontSize = 16.sp)
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Button(
                                onClick = { showLogoutDialog = true },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
                            ) {
                                Icon(Icons.Default.ExitToApp, contentDescription = null, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Logout", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(40.dp))
                }
            }

            else -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Buyer profile not found", color = Color.Gray)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { showLogoutDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = MainColor)
                        ) {
                            Text("Logout")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BuyerProfileRow(title: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 6.dp)) {
        Text(title, fontWeight = FontWeight.Bold)
        Text(value, color = Color.DarkGray)
        HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray)
    }
}
package com.example.handmadeexpo.view

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
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
    val buyerId = FirebaseAuth.getInstance().currentUser?.uid  // ✅ Fixed typo

    var showLogoutDialog by remember { mutableStateOf(false) }
    var isLoggingOut by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        buyerId?.let {
            viewModel.getBuyerDetailsById(it)
        }
    }

    // Debug logging
    LaunchedEffect(buyer) {
        buyer?.let {
            Log.d("ProfileScreen", "========== BUYER DATA ==========")
            Log.d("ProfileScreen", "Name: ${it.buyerName}")
            Log.d("ProfileScreen", "Email: ${it.buyerEmail}")
            Log.d("ProfileScreen", "Phone: ${it.buyerPhoneNumber}")
            Log.d("ProfileScreen", "Address: ${it.buyerAddress}")
            Log.d("ProfileScreen", "================================")
        }
    }

    // Modern Logout Dialog
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            icon = {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(Color(0xFFF44336).copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.ExitToApp,
                        contentDescription = null,
                        tint = Color(0xFFF44336),
                        modifier = Modifier.size(28.dp)
                    )
                }
            },
            title = {
                Text(
                    "Confirm Logout",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color(0xFF212121)
                )
            },
            text = {
                Text(
                    "Are you sure you want to logout from your account?",
                    fontSize = 14.sp,
                    color = Color(0xFF616161)
                )
            },
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
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336)),
                    enabled = !isLoggingOut,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (isLoggingOut) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Logout", fontWeight = FontWeight.Bold)
                    }
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showLogoutDialog = false },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF757575))
                ) {
                    Text("Cancel", fontWeight = FontWeight.Medium)
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
        when {
            loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = MainColor, strokeWidth = 3.dp)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Loading profile...", color = Color.Gray, fontSize = 14.sp)
                    }
                }
            }

            buyer != null -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    // Profile Header Card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(4.dp, RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(120.dp)
                                    .shadow(8.dp, CircleShape)
                            ) {
                                Image(
                                    painter = painterResource(R.drawable.profilephoto),
                                    contentDescription = "Profile Picture",
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(CircleShape)
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = buyer?.buyerName ?: "No Name",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF212121)
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Surface(
                                color = MainColor.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(20.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.Person,
                                        contentDescription = null,
                                        tint = MainColor,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        "Buyer Account",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = MainColor
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            Button(
                                onClick = onEditClick,
                                modifier = Modifier
                                    .fillMaxWidth(0.6f)
                                    .height(48.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MainColor),
                                shape = RoundedCornerShape(24.dp)
                            ) {
                                Icon(Icons.Default.Edit, null, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Edit Profile", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Personal Information Section
                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        Text(
                            "Personal Information",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF212121),
                            modifier = Modifier.padding(vertical = 8.dp)
                        )

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(2.dp, RoundedCornerShape(16.dp)),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                // Email
                                ModernProfileRow(
                                    icon = Icons.Default.Email,
                                    iconColor = Color(0xFF1E88E5),
                                    title = "Email",
                                    value = buyer?.buyerEmail ?: "Not provided"
                                )
                                HorizontalDivider(
                                    modifier = Modifier.padding(vertical = 12.dp),
                                    color = Color(0xFFEEEEEE)
                                )
                                // Phone - using buyerPhoneNumber
                                ModernProfileRow(
                                    icon = Icons.Default.Phone,
                                    iconColor = Color(0xFF4CAF50),
                                    title = "Phone",
                                    value = buyer?.buyerPhoneNumber ?: "Not provided"
                                )
                                HorizontalDivider(
                                    modifier = Modifier.padding(vertical = 12.dp),
                                    color = Color(0xFFEEEEEE)
                                )
                                // Address - using buyerAddress
                                ModernProfileRow(
                                    icon = Icons.Default.LocationOn,
                                    iconColor = Color(0xFFF44336),
                                    title = "Address",
                                    value = buyer?.buyerAddress ?: "Not provided"
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Account Settings Section
                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        Text(
                            "Account Settings",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF212121),
                            modifier = Modifier.padding(vertical = 8.dp)
                        )

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(2.dp, RoundedCornerShape(16.dp)),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                OutlinedButton(
                                    onClick = onChangePasswordClick,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(56.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MainColor)
                                ) {
                                    Icon(Icons.Default.Lock, null, modifier = Modifier.size(22.dp))
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text("Change Password", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Button(
                                    onClick = { showLogoutDialog = true },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(56.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336))
                                ) {
                                    Icon(Icons.Default.ExitToApp, null, modifier = Modifier.size(22.dp))
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text("Logout", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }

            else -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.PersonOff,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = Color.Gray.copy(alpha = 0.3f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Profile not found", fontSize = 18.sp, fontWeight = FontWeight.Medium, color = Color.Gray)
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = { showLogoutDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = MainColor),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.ExitToApp, null, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Logout", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ModernProfileRow(
    icon: ImageVector,
    iconColor: Color,
    title: String,
    value: String
) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(iconColor.copy(alpha = 0.15f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 12.sp, color = Color(0xFF9E9E9E), fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                value.ifEmpty { "Not provided" },
                fontSize = 15.sp,
                color = Color(0xFF212121),
                fontWeight = FontWeight.Normal
            )
        }
    }
}
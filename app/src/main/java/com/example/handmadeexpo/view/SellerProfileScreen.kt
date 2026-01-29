package com.example.handmadeexpo.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.handmadeexpo.R
import com.example.handmadeexpo.viewmodel.SellerViewModel

@Composable
fun SellerProfileScreen(
    sellerId: String,
    onEditProfileClick: () -> Unit,
    onChangePasswordClick: () -> Unit,
    onLogoutSuccess: () -> Unit,
    viewModel: SellerViewModel
) {
    val context = LocalContext.current
    val seller by viewModel.seller.observeAsState()
    val loading by viewModel.loading.observeAsState(initial = true)

    var showFullDocument by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    var isLoggingOut by remember { mutableStateOf(false) }

    LaunchedEffect(sellerId) {
        if (sellerId.isNotEmpty()) {
            viewModel.getSellerDetailsById(sellerId)
        }
    }

    // Modern Logout Dialog (matching buyer's design)
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
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFF757575)
                    )
                ) {
                    Text("Cancel", fontWeight = FontWeight.Medium)
                }
            },
            shape = RoundedCornerShape(20.dp),
            containerColor = Color.White
        )
    }

    // --- ROOT BOX FOR BACKGROUND ---
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // 1. BACKGROUND IMAGE
        Image(
            painter = painterResource(id = R.drawable.bg7),
            contentDescription = "Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // 2. MAIN CONTENT
        Column(
            modifier = Modifier
                .fillMaxSize()
            // Removed solid background color so image shows
        ) {
            when {
                loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(
                                color = Color(0xFF4CAF50),
                                strokeWidth = 3.dp
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Loading profile...", color = Color.Gray, fontSize = 14.sp)
                        }
                    }
                }

                seller != null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        // Profile Header Card (matching buyer's design)
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
                                // Profile Picture
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

                                // Shop Name
                                Text(
                                    text = seller?.shopName ?: "Shop Name",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF212121)
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                // Seller Name (subtitle)
                                Text(
                                    text = seller?.fullName ?: "",
                                    fontSize = 14.sp,
                                    color = Color(0xFF757575)
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                // Edit Profile Button
                                OutlinedButton(
                                    onClick = onEditProfileClick,
                                    shape = RoundedCornerShape(24.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = Color(0xFF4CAF50)
                                    ),
                                    border = BorderStroke(1.5.dp, Color(0xFF4CAF50))
                                ) {
                                    Icon(
                                        Icons.Default.Edit,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        "Edit Profile",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Personal Details Section
                        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                            Text(
                                "Personal Details",
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
                                    SellerProfileRow(
                                        icon = Icons.Default.Email,
                                        iconColor = Color(0xFF1E88E5),
                                        title = "Email",
                                        value = seller?.sellerEmail ?: ""
                                    )
                                    Divider(
                                        modifier = Modifier.padding(vertical = 12.dp),
                                        color = Color(0xFFEEEEEE)
                                    )
                                    SellerProfileRow(
                                        icon = Icons.Default.Phone,
                                        iconColor = Color(0xFF4CAF50),
                                        title = "Phone",
                                        value = seller?.sellerPhoneNumber ?: ""
                                    )
                                    Divider(
                                        modifier = Modifier.padding(vertical = 12.dp),
                                        color = Color(0xFFEEEEEE)
                                    )
                                    SellerProfileRow(
                                        icon = Icons.Default.LocationOn,
                                        iconColor = Color(0xFFF44336),
                                        title = "Address",
                                        value = seller?.sellerAddress ?: ""
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Business Details Section
                        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                            Text(
                                "Business Details",
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
                                    SellerProfileRow(
                                        icon = Icons.Default.Store,
                                        iconColor = Color(0xFF9C27B0),
                                        title = "Shop Name",
                                        value = seller?.shopName ?: ""
                                    )
                                    Divider(
                                        modifier = Modifier.padding(vertical = 12.dp),
                                        color = Color(0xFFEEEEEE)
                                    )
                                    SellerProfileRow(
                                        icon = Icons.Default.Badge,
                                        iconColor = Color(0xFFFF9800),
                                        title = "PAN Number",
                                        value = seller?.panNumber ?: ""
                                    )

                                    // Document section
                                    if (!seller?.documentUrl.isNullOrEmpty()) {
                                        Divider(
                                            modifier = Modifier.padding(vertical = 12.dp),
                                            color = Color(0xFFEEEEEE)
                                        )
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.Top
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(40.dp)
                                                    .background(Color(0xFF2196F3).copy(alpha = 0.15f), CircleShape),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    Icons.Default.Description,
                                                    contentDescription = null,
                                                    tint = Color(0xFF2196F3),
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }

                                            Spacer(modifier = Modifier.width(16.dp))

                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    "Business Document",
                                                    fontSize = 12.sp,
                                                    color = Color(0xFF9E9E9E),
                                                    fontWeight = FontWeight.Medium
                                                )
                                                Spacer(modifier = Modifier.height(8.dp))
                                                OutlinedButton(
                                                    onClick = { showFullDocument = true },
                                                    shape = RoundedCornerShape(8.dp),
                                                    colors = ButtonDefaults.outlinedButtonColors(
                                                        contentColor = Color(0xFF2196F3)
                                                    ),
                                                    modifier = Modifier.height(36.dp)
                                                ) {
                                                    Icon(
                                                        Icons.Default.Visibility,
                                                        contentDescription = null,
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Text("View Document", fontSize = 13.sp)
                                                }
                                            }
                                        }
                                    }
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
                                    // Change Password Button
                                    OutlinedButton(
                                        onClick = onChangePasswordClick,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(56.dp),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.outlinedButtonColors(
                                            contentColor = Color(0xFF4CAF50)
                                        )
                                    ) {
                                        Icon(
                                            Icons.Default.Lock,
                                            contentDescription = null,
                                            modifier = Modifier.size(22.dp)
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(
                                            "Change Password",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))

                                    // Logout Button
                                    Button(
                                        onClick = { showLogoutDialog = true },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(56.dp),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(0xFFF44336)
                                        )
                                    ) {
                                        Icon(
                                            Icons.Default.ExitToApp,
                                            contentDescription = null,
                                            modifier = Modifier.size(22.dp)
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(
                                            "Logout",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }

                else -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.PersonOff,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = Color.Gray.copy(alpha = 0.3f)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                "Profile not found",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.Gray
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            Button(
                                onClick = { showLogoutDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
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

        // Full Screen Document Viewer
        if (showFullDocument && !seller?.documentUrl.isNullOrEmpty()) {
            Dialog(
                onDismissRequest = { showFullDocument = false },
                properties = DialogProperties(usePlatformDefaultWidth = false)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.95f))
                        .clickable { showFullDocument = false },
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(seller!!.documentUrl)
                            .build(),
                        contentDescription = "Full Screen Document",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentScale = ContentScale.Fit
                    )

                    // Close Button
                    IconButton(
                        onClick = { showFullDocument = false },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(16.dp)
                            .size(48.dp)
                            .background(Color.White.copy(alpha = 0.2f), CircleShape)
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    // Close Hint
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.TouchApp, null, tint = Color.White.copy(0.7f))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Tap anywhere to close",
                            color = Color.White.copy(0.7f),
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SellerProfileRow(
    icon: ImageVector,
    iconColor: Color,
    title: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(iconColor.copy(alpha = 0.15f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                title,
                fontSize = 12.sp,
                color = Color(0xFF9E9E9E),
                fontWeight = FontWeight.Medium
            )
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
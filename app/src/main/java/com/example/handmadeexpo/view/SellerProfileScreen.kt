package com.example.handmadeexpo.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.handmadeexpo.R
import com.example.handmadeexpo.ui.theme.MainColor
import com.example.handmadeexpo.viewmodel.SellerViewModel

@Composable
fun SellerProfileScreen(
    sellerId: String,
    onEditProfileClick: () -> Unit,
    viewModel: SellerViewModel
) {
    val context = LocalContext.current
    
    // Observe state from ViewModel
    val seller by viewModel.seller.observeAsState()
    val loading by viewModel.loading.observeAsState(initial = true)
    
    // State for Full Screen Document Viewer
    var showFullDocument by remember { mutableStateOf(false) }

    // Fetch Data automatically whenever the sellerId changes or screen launches
    LaunchedEffect(sellerId) {
        if (sellerId.isNotEmpty()) {
            viewModel.getSellerDetailsById(sellerId)
        }
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
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MainColor)
                }
            }

            seller != null -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(10.dp))

                    // Title
                    Text(
                        text = "Your Profile",
                        style = TextStyle(
                            fontSize = 35.sp,
                            fontWeight = FontWeight.Bold,
                            color = MainColor
                        ),
                        modifier = Modifier.padding(20.dp)
                    )

                    // Profile Picture
                    Image(
                        painter = painterResource(R.drawable.profilephoto),
                        contentDescription = "Profile Photo",
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(Color.White),
                        contentScale = ContentScale.Crop
                    )

                    // Shop Name
                    Text(
                        text = seller?.shopName ?: "Unknown Shop",
                        style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Medium),
                        modifier = Modifier.padding(10.dp)
                    )

                    // Edit Button
                    Button(
                        onClick = onEditProfileClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MainColor,
                            contentColor = Color.White
                        ),
                        modifier = Modifier
                            .width(140.dp)
                            .height(40.dp)
                    ) {
                        Text("Edit Profile")
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Details Card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 10.dp),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            ProfileRow("Full Name", seller?.fullName ?: "N/A")
                            ProfileRow("Email", seller?.sellerEmail ?: "N/A")
                            ProfileRow("Phone", seller?.sellerPhoneNumber ?: "N/A")
                            ProfileRow("Address", if (seller?.sellerAddress.isNullOrEmpty()) "Not set" else seller!!.sellerAddress)
                            ProfileRow("PAN Number", seller?.panNumber ?: "N/A")

                            // Verification Document Section
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Verification Document (Tap to view)",
                                fontWeight = FontWeight.Bold,
                                color = MainColor
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            Card(
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp)
                                    .clickable { if (!seller?.documentUrl.isNullOrEmpty()) showFullDocument = true },
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F0F0))
                            ) {
                                if (!seller?.documentUrl.isNullOrEmpty()) {
                                    AsyncImage(
                                        model = ImageRequest.Builder(context)
                                            .data(seller!!.documentUrl)
                                            .crossfade(true)
                                            .build(),
                                        contentDescription = "Uploaded Doc",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop,
                                        error = painterResource(R.drawable.baseline_cloud_upload_24)
                                    )
                                } else {
                                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                        Text("No Document Uploaded", color = Color.Gray)
                                    }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(50.dp))
                }
            }

            else -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Seller profile not found", color = Color.Gray)
                }
            }
        }

        // Full Screen Image Dialog Logic
        if (showFullDocument && !seller?.documentUrl.isNullOrEmpty()) {
            Dialog(
                onDismissRequest = { showFullDocument = false },
                properties = DialogProperties(usePlatformDefaultWidth = false)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.9f))
                        .clickable { showFullDocument = false },
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(seller!!.documentUrl)
                            .build(),
                        contentDescription = "Full Screen Document",
                        modifier = Modifier.fillMaxWidth(),
                        contentScale = ContentScale.Fit
                    )
                    Text(
                        text = "Tap anywhere to close",
                        color = Color.White,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(30.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ProfileRow(title: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(text = title, fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 14.sp)
        Text(text = value, color = Color.DarkGray, fontSize = 16.sp)
        HorizontalDivider(modifier = Modifier.padding(top = 8.dp), color = Color.LightGray, thickness = 0.5.dp)
    }
}
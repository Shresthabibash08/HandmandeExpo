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
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.handmadeexpo.R
import com.example.handmadeexpo.repo.SellerRepoImpl
import com.example.handmadeexpo.ui.theme.MainColor
import com.example.handmadeexpo.viewmodel.SellerViewModel
import com.example.handmadeexpo.viewmodel.SellerViewModelFactory

@Composable
fun SellerProfileScreen() {
    // 1. Initialize Context
    val context = LocalContext.current

    // 2. Initialize ViewModel using Factory
    val viewModel: SellerViewModel = viewModel(
        factory = SellerViewModelFactory(SellerRepoImpl())
    )

    // 3. Observe Data
    val seller by viewModel.seller.observeAsState()
    val loading by viewModel.loading.observeAsState(initial = true)

    // 4. State for Full Screen Image
    var showFullDocument by remember { mutableStateOf(false) }

    // 5. Fetch Data Automatically
    LaunchedEffect(Unit) {
        val currentUser = viewModel.getCurrentUser()
        if (currentUser != null) {
            viewModel.getSellerDetailsById(currentUser.uid)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Background image
        Image(
            painter = painterResource(R.drawable.bg10),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // --- LOADING STATE ---
        if (loading == true) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MainColor)
            }
        } else {
            // Main Content (Scrollable)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.padding(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
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

                        // Profile Picture (Placeholder)
                        Image(
                            painter = painterResource(R.drawable.profilephoto),
                            contentDescription = null,
                            modifier = Modifier
                                .height(120.dp)
                                .width(120.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )

                        // Username (Shop Name)
                        Text(
                            text = "@${seller?.shopName ?: "username"}",
                            style = TextStyle(fontSize = 20.sp),
                            modifier = Modifier.padding(10.dp)
                        )

                        // Edit Button
                        Button(
                            onClick = { },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MainColor,
                                contentColor = Color.White
                            ),
                            modifier = Modifier.width(120.dp).height(35.dp)
                        ) {
                            Text(text = "Edit Profile")
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // --- DETAILS CARD ---
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(8.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {

                                // Dynamic Rows
                                ProfileRow("Full Name", seller?.fullName ?: "N/A")
                                ProfileRow("Email", seller?.sellerEmail ?: "N/A")
                                ProfileRow("Phone", seller?.sellerPhoneNumber ?: "N/A")
                                ProfileRow("Address", seller?.sellerAddress?.ifEmpty { "Not set" } ?: "Not set")
                                ProfileRow("Pan Number", seller?.panNumber ?: "N/A")

                                // --- VERIFICATION IMAGE SECTION ---
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "Verification Document (Tap to view)",
                                    fontWeight = FontWeight.Bold,
                                    color = MainColor
                                )
                                Spacer(modifier = Modifier.height(8.dp))

                                // Document Image Container
                                Card(
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(180.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F0F0))
                                ) {
                                    if (!seller?.documentUrl.isNullOrEmpty()) {
                                        AsyncImage(
                                            model = ImageRequest.Builder(context)
                                                .data(seller!!.documentUrl)
                                                .crossfade(true)
                                                .build(),
                                            contentDescription = "Uploaded Doc",
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .clickable { showFullDocument = true }, // Make clickable
                                            contentScale = ContentScale.Crop,
                                            error = painterResource(R.drawable.baseline_cloud_upload_24)
                                        )
                                    } else {
                                        Box(
                                            modifier = Modifier.fillMaxSize(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text("No Document Uploaded", color = Color.Gray)
                                        }
                                    }
                                }

                                // Status Removed Here
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(50.dp))
            }
        }

        // --- FULL SCREEN IMAGE DIALOG ---
        if (showFullDocument && !seller?.documentUrl.isNullOrEmpty()) {
            Dialog(
                onDismissRequest = { showFullDocument = false },
                properties = DialogProperties(usePlatformDefaultWidth = false)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black)
                        .clickable { showFullDocument = false }, // Click background to close
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(seller!!.documentUrl)
                            .crossfade(true)
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
                            .padding(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ProfileRow(title: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(text = title, fontWeight = FontWeight.Bold, color = Color.Black)
        Text(text = value, color = Color.DarkGray)
        HorizontalDivider(modifier = Modifier.padding(top = 8.dp), color = Color.LightGray)
    }
}
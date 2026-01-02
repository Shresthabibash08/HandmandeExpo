package com.example.handmadeexpo.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.handmadeexpo.viewmodel.BuyerProfileViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun BuyerProfileScreen(
    viewModel: BuyerProfileViewModel
) {

    val buyer by viewModel.buyerProfile.observeAsState()
    val loading by viewModel.loading.observeAsState(false)

    val buyerId = FirebaseAuth.getInstance().currentUser?.uid

    LaunchedEffect(Unit) {
        buyerId?.let {
            viewModel.getBuyerProfileById(it)
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
                    modifier = Modifier.fillMaxSize()
                ) {

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {

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
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )

                            Text(
                                text = buyer!!.fullName,
                                style = TextStyle(fontSize = 20.sp),
                                modifier = Modifier.padding(10.dp)
                            )

                            Button(
                                onClick = { /* Navigate to Edit Profile */ },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MainColor,
                                    contentColor = Color.White
                                ),
                                modifier = Modifier
                                    .width(120.dp)
                                    .height(35.dp)
                            ) {
                                Text(text = "Edit Profile")
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                shape = RoundedCornerShape(16.dp),
                                elevation = CardDefaults.cardElevation(8.dp)
                            ) {
                                Column(modifier = Modifier.padding(20.dp)) {

                                    BuyerProfileRow(
                                        title = "Email",
                                        value = buyer!!.buyerEmail
                                    )

                                    BuyerProfileRow(
                                        title = "Phone",
                                        value = buyer!!.buyerPhoneNumber
                                    )

                                    BuyerProfileRow(
                                        title = "Address",
                                        value = buyer!!.buyerAddress
                                    )
                                }
                            }
                        }
                    }
                }
            }

            else -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Buyer profile not found")
                }
            }
        }
    }
}

@Composable
fun BuyerProfileRow(title: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(text = title, fontWeight = FontWeight.Bold)
        Text(text = value, color = Color.DarkGray)
        Divider(modifier = Modifier.padding(top = 8.dp))
    }
}

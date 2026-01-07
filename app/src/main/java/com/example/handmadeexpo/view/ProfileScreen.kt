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
import com.example.handmadeexpo.viewmodel.BuyerViewModel
import com.google.firebase.auth.FirebaseAuth
import com.example.handmadeexpo.ui.theme.PurpleGrey40

@Composable
fun BuyerProfileScreen(
    viewModel: BuyerViewModel,
    onEditClick: () -> Unit
) {

    val buyer by viewModel.buyer.observeAsState()
    val loading by viewModel.loading.observeAsState(false)

    val buyerId = FirebaseAuth.getInstance().currentUser?.uid



    LaunchedEffect(Unit) {
        buyerId?.let {
            viewModel.getBuyerDetailsById(it)
        }
    }



    Box(modifier = Modifier.fillMaxSize()) {


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
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Spacer(modifier = Modifier.height(20.dp))
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

                    Image(
                        painter = painterResource(R.drawable.profilephoto),
                        contentDescription = null,
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                    )

                    Text(
                        buyer!!.buyerName,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(8.dp)
                    )

                    Button(
                        onClick = onEditClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MainColor
                        )
                    ) {
                        Text("Edit Profile")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            BuyerProfileRow("Email", buyer!!.buyerEmail)
                            BuyerProfileRow("Phone", buyer!!.buyerPhoneNumber)
                            BuyerProfileRow("Address", buyer!!.buyerAddress)
                        }
                    }
                }
            }

            else -> {
                Text("Buyer profile not found")
            }
        }
    }
}

@Composable
fun BuyerProfileRow(title: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 6.dp)) {
        Text(title, fontWeight = FontWeight.Bold)
        Text(value, color = Color.DarkGray)
        Divider()
    }
}




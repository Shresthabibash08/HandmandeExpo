package com.example.handmadeexpo.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.handmadeexpo.ui.theme.Purple80

@Composable
fun SellerProfileScreen(sellerId: String){
    Box(modifier = Modifier.fillMaxSize()) {
        // Background image
        Image(
            painter = painterResource(R.drawable.bg10), // Replace with your image
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Column(
            modifier = Modifier
                .fillMaxSize()

        ) {

            Spacer(modifier = Modifier
                .padding(10.dp))

            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Column(verticalArrangement = Arrangement.Center,
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
                            .height(120.dp)
                            .width(120.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop

                    )
                    Text(text = "@username123", style = TextStyle(
                        fontSize = 20.sp),
                        modifier = Modifier.padding(10.dp)
                    )

                    Button(
                        onClick = { },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MainColor,   // Background color
                            contentColor = Color.White           // Text/Icon color
                        ),
                        modifier = Modifier
                            .width(120.dp)   // Button width
                            .height(35.dp)   // Button height
                    ) {
                        Text(text = "Edit Profile")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Details Card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(all = 20.dp), // 10dp padding on all sides
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(8.dp)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            ProfileRow("Email", "username123@gmail.com")
                            ProfileRow("Phone", "984000000")
                            ProfileRow("Address", "Kathmandu, Nepal")
                            ProfileRow("Pan Number", "123456")

                        }
                    }
                }

            }

        }
    }
}

@Composable
fun ProfileRow(title: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(text = title, fontWeight = FontWeight.Bold)
        Text(text = value, color = Color.DarkGray)
        Divider(modifier = Modifier.padding(top = 8.dp))
    }
}

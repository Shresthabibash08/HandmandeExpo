package com.example.handmadeexpo.view

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.handmadeexpo.R

import com.example.handmadeexpo.ui.theme.Gray
import com.example.handmadeexpo.ui.theme.MainColor
import com.example.handmadeexpo.ui.theme.TextBlack


class SellerVerificationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SellerVerificationUI(onBackClick = { finish() })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellerVerificationUI(onBackClick: () -> Unit = {}) {

    val context = LocalContext.current

    // UI State for Radio Buttons
    var selectedDocType by remember { mutableStateOf("National ID") }
    val docTypes = listOf("National ID", "Passport", "Driving License")

    Scaffold(

        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Verify Identity",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MainColor
                )
            )
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)

            ) {
                Button(
                    onClick = {
                        Toast.makeText(context, "Verification Submitted!", Toast.LENGTH_SHORT).show()

                        // Navigate to Seller Dashboard

                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MainColor)
                ) {
                    Text("Submit for Verification", fontSize = 18.sp, color = Color.White)
                }
            }
        }
    ) { paddingValues ->

        // --- ADDED BOX WRAPPER FOR BACKGROUND IMAGE ---
        Box(modifier = Modifier.fillMaxSize()) {

            // 1. The Background Image
            Image(
                painter = painterResource(R.drawable.bg),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // 2. The Main Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                // --- Header Text ---
                Text(
                    text = "Document Details",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextBlack
                )

                Text(
                    text = "Select a document type and upload a clear image.",
                    fontSize = 14.sp,
                    color = Gray,
                    modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
                )

                // --- Document Type Selector Card ---
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        docTypes.forEach { type ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedDocType = type }
                                    .padding(vertical = 8.dp, horizontal = 8.dp)
                            ) {
                                RadioButton(
                                    selected = (type == selectedDocType),
                                    onClick = { selectedDocType = type },
                                    colors = RadioButtonDefaults.colors(selectedColor = MainColor)
                                )
                                Text(
                                    text = type,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.padding(start = 8.dp),
                                    color = TextBlack
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // --- Upload Area Header ---
                Text(
                    text = "Upload Image",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextBlack
                )

                Spacer(modifier = Modifier.height(12.dp))

                // --- Upload Box Card ---
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(2.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clickable {
                            Toast.makeText(context, "Image Picker coming soon", Toast.LENGTH_SHORT).show()
                        }
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_cloud_upload_24),
                                contentDescription = "Upload",
                                modifier = Modifier.size(48.dp),
                                tint = MainColor
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Tap to upload file", fontWeight = FontWeight.Medium, color = TextBlack)
                            Text("JPG or PNG", fontSize = 12.sp, color = Color.Gray)
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SellerVerificationActivityPreview() {
    SellerVerificationUI()
}
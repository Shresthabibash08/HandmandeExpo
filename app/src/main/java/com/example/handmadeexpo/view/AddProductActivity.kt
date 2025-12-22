package com.example.handmadeexpo.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.handmadeexpo.ui.theme.borderGray
import com.example.handmadeexpo.ui.theme.cream
import com.example.handmadeexpo.ui.theme.green1
import com.example.handmadeexpo.ui.theme.lightGreen

class AddProductActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AddProductBody()
        }
    }
}

@Composable
fun AddProductBody() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(cream)
    ) {



        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(green1),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Add Product",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Column(
            modifier = Modifier
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(lightGreen, RoundedCornerShape(16.dp))
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .border(
                            1.dp,
                            borderGray,
                            RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Add, null, tint = green1)
                        Text("Upload Image", fontSize = 12.sp)
                    }
                }

                repeat(3) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(Color.White, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.ArrowForward,
                            null,
                            tint = Color.LightGray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text("Product Name", fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = "",
                onValueChange = {},
                placeholder = { Text("Enter product name") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))


            Text("Price (in NPR)", fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = "",
                onValueChange = {},
                placeholder = { Text("Enter price") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))


            Text("Category", fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = "Choose category",
                onValueChange = {},
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    Icon(
                        Icons.Default.ArrowDropDown,
                        null,
                        tint = green1
                    )
                },
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("Description", fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = "",
                onValueChange = {},
                placeholder = { Text("Describe your product") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("Stock Quantity", fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = "",
                onValueChange = {},
                placeholder = { Text("Enter stock quantity") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = green1
                )
            ) {
                Text(
                    "Add Product",
                    color = Color.White,
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Preview
@Composable
fun AddProductPreview(){
    AddProductBody()
}


package com.example.handmadeexpo.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.handmadeexpo.view.ui.theme.HandmadeExpoTheme

class SellerProfile : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SellerProfieBody()
        }
    }
}

@Composable
fun SellerProfieBody(){
    var email by remember{ mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }
    var dob by remember { mutableStateOf("") }
    Scaffold { padding ->
        Column(modifier=Modifier.fillMaxSize()
            .padding(padding)
            .background(color=Color.Green)
            ){
            Spacer(modifier=Modifier.height(20.dp))
            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center){
//                Image( painter= painterResource(),
//                    contentDescription = null,
//                    modifier=Modifier.height(60.dp).width(60.dp).clip(CircleShape),
//                    contentScale = ContentScale.Crop
//
//                )
                Text("Seller Profile",style= TextStyle(
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold),
                    color = Color.White)
            }
            Spacer(modifier=Modifier.height(40.dp))
            ProfileInputField(
                label = "Full Name",
                value = fullName,
                placeholder = "Bibash Shrestha",
                onValueChange = { fullName = it }
            )
            Spacer(modifier=Modifier.height(20.dp))
           ProfileInputField(label="Email",
               value=email,
               placeholder="bibash@gmail.com",
               onValueChange={email =it})
            Spacer(modifier=Modifier.height(20.dp))
            ProfileInputField(label="Date Of Birth",
                value=dob,
                placeholder="2025-10-11",
                onValueChange={dob = it})
        }

    }

}

@Composable
fun ProfileInputField(
    label: String,
    value: String,
    placeholder: String,
    onValueChange: (String) -> Unit
) {
    Column {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 30.dp),
            style = TextStyle(fontSize = 20.sp)
        )

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp)
                .padding(horizontal = 20.dp),
            placeholder = { Text(placeholder) },
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Blue,
                unfocusedIndicatorColor = Color.Transparent,
                focusedContainerColor = Color.Blue
            )
        )
    }
}

@Composable
@Preview
fun SellerPreview(){
    SellerProfieBody()
}

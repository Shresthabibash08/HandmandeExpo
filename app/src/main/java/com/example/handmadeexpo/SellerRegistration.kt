package com.example.handmadeexpo


import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.handmadeexpo.ui.theme.Blue
import com.example.handmadeexpo.ui.theme.Cream
import com.example.handmadeexpo.ui.theme.Green
import com.example.handmadeexpo.ui.theme.HandmadeExpoTheme
import com.example.handmadeexpo.ui.theme.Purple80
import com.example.handmadeexpo.ui.theme.White

class SellerRegistration : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SellerRegisterScreen()
        }
    }
}

@Composable
fun SellerRegisterScreen(){
    var name by remember { mutableStateOf("") }
    var shopname by remember { mutableStateOf("") }
    var contact by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var passwordvisibility by remember { mutableStateOf(false) }
    var confirmPasswordvisibility by remember { mutableStateOf(false) }
    var confirmpassword by remember { mutableStateOf("") }
    var pannumber by remember { mutableStateOf("") }

    Scaffold { padding ->
        Column(
            modifier = Modifier.fillMaxSize().background(Cream),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(10.dp))
            Image(
                painter = painterResource(com.example.handmadeexpo.R.drawable.logo),
                contentDescription = null,
                modifier = Modifier.height(100.dp).width(100.dp).clip(CircleShape),
                contentScale = ContentScale.Crop

            )
            Text(
                "Join As Artisan",
                modifier = Modifier.fillMaxWidth(),
                style = TextStyle(
                    color = Green,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    fontSize = 30.sp
                )
            )
            Text(
                "Start selling your crafts to the world.",
                style = TextStyle(
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = 20.dp)

            )
            Spacer(modifier = Modifier.height(20.dp))
            CustomTextField(
                value = name,
                onValueChange = { name = it },
                placeholder = "Full Name"
            )
            Spacer(modifier = Modifier.height(20.dp))
            CustomTextField(
                value = shopname,
                onValueChange = { shopname = it },
                placeholder = "Shop Name"
            )
            Spacer(modifier = Modifier.height(20.dp))
            CustomTextField(
                value = pannumber,
                onValueChange = { pannumber = it },
                placeholder = "PAN NUMBER"
            )
            Spacer(modifier = Modifier.height(20.dp))
            CustomTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = "Email",
                keyboardType = KeyboardType.Email
            )

            Spacer(modifier = Modifier.height(20.dp))
            CustomTextField(
                value = contact,
                onValueChange = { contact = it },
                placeholder = "Phone",
                keyboardType = KeyboardType.Phone
            )

            Spacer(modifier = Modifier.height(20.dp))

            CustomTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = "Password",
                keyboardType = KeyboardType.Password,
                visualTransformation = if (passwordvisibility) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick ={
                        passwordvisibility = !passwordvisibility
                    }) {
                        Icon(
                            painter = if (passwordvisibility)
                                painterResource(R.drawable.baseline_visibility_off_24)
                            else
                                painterResource(
                                    R.drawable.baseline_visibility_24
                                ),
                            contentDescription = null
                        )
                    }

                }
            )
            Spacer(modifier = Modifier.height(20.dp))
            CustomTextField(
                value = confirmpassword,
                onValueChange = { confirmpassword = it },
                placeholder = "Confirm Password",
                keyboardType = KeyboardType.Password,
                visualTransformation = if (confirmPasswordvisibility) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick ={
                        confirmPasswordvisibility = !confirmPasswordvisibility
                    }) {
                        Icon(
                            painter = if (confirmPasswordvisibility)
                                painterResource(R.drawable.baseline_visibility_off_24)
                            else
                                painterResource(
                                    R.drawable.baseline_visibility_24
                                ),
                            contentDescription = null
                        )
                    }

                }
            )
            Spacer(modifier = Modifier.height(20.dp))
            SocialCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .padding(horizontal = 15.dp),
                label = "Upload Citizenship / ID"
            )
            Spacer(modifier = Modifier.height(10.dp))
            Button(
                onClick = {

                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Blue
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 6.dp
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth().height(55.dp)
                    .padding(horizontal = 15.dp),
            ) {
                Text("Register",fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                buildAnnotatedString {
                    append("Already have an account?")

                    withStyle(style = SpanStyle(color = Blue, fontWeight = FontWeight.Bold)){
                        append(" Sign In")
                    }
                },
                fontSize = 16.sp,
                modifier = Modifier.padding(bottom = 30.dp)
            )

        }
    }
}

@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        shape = RoundedCornerShape(15.dp),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        visualTransformation = visualTransformation,
        trailingIcon = trailingIcon,

        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp) // Reduced height to make it more compact
            .padding(horizontal = 15.dp),
        placeholder = {
            Text(
                text = placeholder,
                style = TextStyle(
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
            )
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Blue,
            unfocusedBorderColor = Color.Black,
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent
        )
    )
}
@Composable
fun SocialCard(modifier: Modifier, label: String) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(15.dp),

        border = BorderStroke(1.dp, Blue),

        colors = CardDefaults.outlinedCardColors(
            containerColor = Color.Transparent
        )
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = label,
                style = TextStyle(
                    color = Color.Black,
                    textAlign = TextAlign.Center,
                    fontSize = 16.sp
                )
            )
        }

    }
}

@Preview
@Composable
fun RegisterPreview(){
    SellerRegisterScreen()
}


package com.example.handmadeexpo.view


import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.platform.LocalContext
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
import com.example.handmadeexpo.R
import com.example.handmadeexpo.model.BuyerModel
import com.example.handmadeexpo.repo.BuyerRepoImpl
import com.example.handmadeexpo.ui.theme.Blue12
import com.example.handmadeexpo.ui.theme.Green
import com.example.handmadeexpo.viewmodel.BuyerViewModel

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
fun SellerRegisterScreen() {
    var address by remember { mutableStateOf("") }
    var shopName by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var passwordVisibility by remember { mutableStateOf(false) }
    var confirmPasswordVisibility by remember { mutableStateOf(false) }
    var confirmPassword by remember { mutableStateOf("") }
    var panNumber by remember { mutableStateOf("") }
    val context = LocalContext.current
    val activity = context as Activity
    var buyerViewModel = remember{ BuyerViewModel(BuyerRepoImpl()) }


    Scaffold { padding ->
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Image(
                painter = painterResource(R.drawable.finalbackground),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(10.dp))
                Image(
                    painter = painterResource(R.drawable.finallogo),
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
                    value = shopName,
                    onValueChange = { shopName = it },
                    placeholder = "Shop Name"
                )
                Spacer(modifier = Modifier.height(20.dp))
                CustomTextField(
                    value = address,
                    onValueChange = { address = it },
                    placeholder = "Address"
                )
                Spacer(modifier = Modifier.height(20.dp))
                CustomTextField(
                    value = panNumber,
                    onValueChange = { panNumber = it },
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
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it },
                    placeholder = "Phone",
                    keyboardType = KeyboardType.Phone
                )

                Spacer(modifier = Modifier.height(20.dp))

                CustomTextField(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = "Password",
                    keyboardType = KeyboardType.Password,
                    visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = {
                            passwordVisibility = !passwordVisibility
                        }) {
                            Icon(
                                painter = if (passwordVisibility)
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
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    placeholder = "Confirm Password",
                    keyboardType = KeyboardType.Password,
                    visualTransformation = if (confirmPasswordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = {
                            confirmPasswordVisibility = !confirmPasswordVisibility
                        }) {
                            Icon(
                                painter = if (confirmPasswordVisibility)
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
                        Toast.makeText(context, "Registering...", Toast.LENGTH_SHORT).show()
                        buyerViewModel.register(email,password){ success,msg,buyerId ->
                            if(success){
                                var buyerModel= BuyerModel(
                                    buyerId=buyerId,
                                    buyerName=shopName,
                                    buyerAddress = address,
                                    buyerEmail = email,
                                    buyerPhoneNumber = phoneNumber,

                                )
                                buyerViewModel.addBuyerToDatabase(buyerId,buyerModel){success,msg ->
                                    if(success){
                                        Toast.makeText(context, "Processing...", Toast.LENGTH_SHORT).show()
                                        Toast.makeText(context,msg,Toast.LENGTH_SHORT).show()
                                        activity.finish()
                                    }
                                    else{
                                        Toast.makeText(context,msg,Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        }
                        Toast.makeText(context, "REGISTERED...", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Blue12
                    ),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 6.dp
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth().height(55.dp)
                        .padding(horizontal = 15.dp),
                ) {
                    Text("Register", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    buildAnnotatedString {
                        append("Already have an account?")

                        withStyle(style = SpanStyle(color = Blue12, fontWeight = FontWeight.Bold)) {
                            append(" Sign In")
                        }
                    },
                    fontSize = 16.sp,
                    modifier = Modifier.padding(bottom = 30.dp)
                )
            }
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
            .height(50.dp)
            .padding(horizontal = 15.dp),
        placeholder = {
            Text(
                text = placeholder,
                style = TextStyle(
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )
            )
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Blue12,
            unfocusedBorderColor = Color.Black,
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent
        )
    )
}
@Composable
fun SocialCard(modifier: Modifier, label: String,onClick: () -> Unit = {}) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(15.dp),

        border = BorderStroke(1.dp, Blue12),

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
                text = "Sign In",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Blue12


            )
        }

    }
}


@Preview
@Composable
fun RegisterPreview(){
    SellerRegisterScreen()
}


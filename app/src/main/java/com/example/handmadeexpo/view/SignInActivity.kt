package com.example.handmadeexpo.view
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.handmadeexpo.R
import com.example.handmadeexpo.repo.BuyerRepoImpl
import com.example.handmadeexpo.ui.theme.AquaGreen
import com.example.handmadeexpo.ui.theme.Blue1
import com.example.handmadeexpo.viewmodel.BuyerViewModel

class SignInActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SignInBody()
        }
    }
}

@Composable
fun SignInBody() {

    var buyerViewModel=remember { BuyerViewModel(BuyerRepoImpl()) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var visibility by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val activity = context as? Activity
    Scaffold { padding ->
        Box(
            modifier = Modifier.fillMaxSize()
        ) {

            Image(
                painter = painterResource(R.drawable.bg1),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                item {
                    Spacer(modifier = Modifier.height(60.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .padding(padding),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Image(
                            painter = painterResource(R.drawable.logo),
                            contentDescription = null,
                            modifier = Modifier
                                .size(180.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    }
                    Text(
                        "Welcome Back!",
                        modifier = Modifier.fillMaxWidth(),
                        style = TextStyle(
                            color = Black,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            fontSize = 40.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = { data ->
                            email = data
                        },
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 15.dp),
                        placeholder = {
                            Text("Email/Phone")
                        },
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = Blue1,
                            unfocusedIndicatorColor = Color.Transparent
                        )
                    )
                    Spacer(modifier = Modifier.height(20.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { data ->
                            password = data
                        },

                        shape = RoundedCornerShape(12.dp),
                        visualTransformation = if (!visibility) PasswordVisualTransformation() else VisualTransformation.None,
                        trailingIcon = {
                            IconButton(onClick = {
                                visibility = !visibility
                            }) {
                                Icon(
                                    painter = if (visibility)
                                        painterResource(R.drawable.baseline_visibility_off_24)
                                    else
                                        painterResource(
                                            R.drawable.baseline_visibility_24
                                        ),
                                    contentDescription = null
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 15.dp),
                        placeholder = {
                            Text("*********")
                        },
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = Blue1,
                            unfocusedIndicatorColor = Color.Transparent
                        )
                    )
                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 15.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Text(
                            "Forgot password?",
                            modifier = Modifier.clickable {
                                val intent = Intent(context, ForgetPasswordActivity::class.java)
                                activity?.startActivity(intent)

                            }
                        )
                    }

                    Button(
                        onClick = {
                            buyerViewModel.login(email, password) { success, msg ->
                                if (success) {
                                    val userId = buyerViewModel.getCurrentUser()?.uid

                                    if (userId != null) {
                                        buyerViewModel.checkUserRole(userId) { role ->
                                            when (role) {
                                                "buyer" -> {
                                                    context.startActivity(
                                                        Intent(context, DashboardActivity::class.java)
                                                    )
                                                    activity?.finish()
                                                }

                                                "seller" -> {
                                                    context.startActivity(
                                                        Intent(context, SellerDashboard::class.java)
                                                    )
                                                    activity?.finish()
                                                }

                                                else -> {
                                                    Toast.makeText(
                                                        context,
                                                        "User role not found",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                }
                            }

                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AquaGreen
                        ),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 6.dp
                        ),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .fillMaxWidth().height(95.dp)
                            .padding(horizontal = 20.dp, vertical = 20.dp),
                    ) {
                        Text("Sign In", style = TextStyle(fontSize = 20.sp))
                    }

                    Text(
                        "Don't have an account?", modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        style = TextStyle(fontSize = 16.sp)
                    )
                    Text(
                        "Sign Up", modifier = Modifier.fillMaxWidth()
                            .clickable {
                                val intent = Intent(context, SignupActivity::class.java)
                                activity?.startActivity(intent)
                            },
                        textAlign = TextAlign.Center,
                        style = TextStyle(fontSize = 16.sp, color = Blue1),
                    )
                }
            }
        }
    }
}


@Preview
@Composable
fun SignInPreview() {
    SignInBody()
}
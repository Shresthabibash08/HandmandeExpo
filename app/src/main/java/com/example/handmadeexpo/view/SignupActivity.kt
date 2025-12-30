package com.example.handmadeexpo.view

import android.app.Activity
import android.content.Context
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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
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
import androidx.compose.ui.graphics.Color.Companion.Blue
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.handmadeexpo.R
import com.example.handmadeexpo.model.BuyerModel
import com.example.handmadeexpo.repo.BuyerRepoImpl
import com.example.handmadeexpo.ui.theme.Green12
import com.example.handmadeexpo.ui.theme.LightGreen12
import com.example.handmadeexpo.ui.theme.MainColor
import com.example.handmadeexpo.ui.theme.Offwhite12
import com.example.handmadeexpo.ui.theme.White12
import com.example.handmadeexpo.viewmodel.BuyerViewModel

class SignupActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SingUpBody()
        }
    }
}

@Composable
fun SingUpBody(){
    var buyerViewModel=remember { BuyerViewModel(BuyerRepoImpl()) }
    var fullname by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var terms by remember { mutableStateOf(false) }
    var visibility by remember { mutableStateOf(false) }
    var confirmVisibility by remember { mutableStateOf(false) }
    var adress by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }

    val context=LocalContext.current
    val activity=context as Activity

    Scaffold {
            padding->
        Box(modifier = Modifier.fillMaxSize()
            .wrapContentSize()
//            .verticalScroll(rememberScrollState())
        ) {
            Image(
                painter = painterResource(R.drawable.img_1),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop

            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())

                    .imePadding()// ✅ VERTICAL SCROLL ADDED
            ) {
                Spacer(modifier = Modifier.padding(top = 30.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(R.drawable.img_2),
                        contentDescription = null,
                        modifier = Modifier
                            .clip(CircleShape)
                            .size(150.dp)
                    )
                }
//                Spacer(modifier = Modifier.padding(vertical = 10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        "Create Your Account",
                        fontSize = 28.sp,
                        color = MainColor,
                        fontWeight = FontWeight.Bold
                    )
                }
                AppOutlinedTextField(
                    label = "Full Name",
                    value = fullname,
                    onValueChange = { fullname = it }
                )
                Spacer(modifier = Modifier.padding(vertical = 2.dp))
                AppOutlinedTextField(
                    label = "Email",
                    value = email,
                    onValueChange = { email = it }
                )
                AppOutlinedTextField(
                    label = "Phone Number",
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it }
                )
                Spacer(modifier = Modifier.padding(vertical = 2.dp))
                AppOutlinedTextField(
                    label = "Address",
                    value = adress,
                    onValueChange = { adress = it }
                )

                Spacer(modifier = Modifier.padding(vertical = 2.dp))
                PasswordTextField(
                    label = "Password",
                    value = password,
                    onValueChange = { password = it },
                    isPasswordVisible = visibility,
                    onTogglePasswordVisibility = { visibility = !visibility }
                )
                Spacer(modifier = Modifier.padding(vertical = 2.dp))
                PasswordTextField(
                    label = "Confirm Password",
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    isPasswordVisible = confirmVisibility,
                    onTogglePasswordVisibility = { confirmVisibility = !confirmVisibility }
                )
                Spacer(modifier = Modifier.padding(vertical = 2.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp,), // smaller start padding
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start // align children to start
                ) {
                    Checkbox(
                        checked = terms,
                        onCheckedChange = { data ->
                            terms = data
                        },
                        colors = CheckboxDefaults.colors(
                            checkedColor = MainColor,
                            checkmarkColor = Color.Companion.White
                        )
                    )
                    Text("I agree to terms & Conditions")
                }
                Button(
                    onClick = {

                        when {
                            fullname.isBlank() ->
                                Toast.makeText(context, "Full name is required", Toast.LENGTH_SHORT).show()

                            email.isBlank() ->
                                Toast.makeText(context, "Email is required", Toast.LENGTH_SHORT).show()

                            phoneNumber.isBlank() ->
                                Toast.makeText(context, "Phone number is required", Toast.LENGTH_SHORT).show()

                            adress.isBlank() ->
                                Toast.makeText(context, "Address is required", Toast.LENGTH_SHORT).show()

                            password.isBlank() ->
                                Toast.makeText(context, "Password is required", Toast.LENGTH_SHORT).show()

                            confirmPassword.isBlank() ->
                                Toast.makeText(context, "Confirm password is required", Toast.LENGTH_SHORT).show()

                            password != confirmPassword ->
                                Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()

                            !terms ->
                                Toast.makeText(context, "Please agree to terms & conditions", Toast.LENGTH_SHORT).show()

                            else -> {
                                // ✅ Only now call Firebase
                                buyerViewModel.register(email, password) { success, msg, buyerId ->
                                    if (success) {
                                        val buyerModel = BuyerModel(
                                            buyerId = buyerId,
                                            buyerName = fullname,
                                            buyerEmail = email,
                                            buyerAddress = adress,
                                            buyerPhoneNumber = phoneNumber
                                        )

                                        buyerViewModel.addBuyerToDatabase(buyerId, buyerModel) { dbSuccess, dbMsg ->
                                            Toast.makeText(context, dbMsg, Toast.LENGTH_SHORT).show()
                                            if (dbSuccess) activity.finish()
                                        }
                                    } else {
                                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        }
                    }
                    ,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MainColor
                    ),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 6.dp
                    ),
                    shape = RoundedCornerShape(20.dp),
                    modifier =Modifier
                        .fillMaxWidth().height(95.dp)
                        .padding(horizontal = 20.dp, vertical = 20.dp),
                ) {
                    Text("Sign Up", style = TextStyle(fontSize = 15.sp),
                        modifier = Modifier.clickable{

//                            if(){
//                                val intent = Intent(context, SignInActivity::class.java)
//                                activity.startActivity(intent)
//                                activity.finish()
//                            }

                        })
                }

                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 30.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text("Already have an account?", fontSize = 16.sp, color =Color.Black)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Sign In",
                        color = Blue,
                        fontSize = 16.sp,
                        modifier = Modifier
                            .clickable {val intent = Intent(context, SignInActivity::class.java)
                                activity?.startActivity(intent)
                                activity.finish()
                            }
                    )
                }
            }
        }
    }
}



@Composable
fun AppOutlinedTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Offwhite12,
            unfocusedContainerColor = Offwhite12,
            focusedIndicatorColor = MainColor,
            unfocusedIndicatorColor = MainColor,
            focusedLabelColor = MainColor,
            cursorColor = MainColor
        )
    )
}

@Composable
fun PasswordTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    isPasswordVisible: Boolean,
    onTogglePasswordVisibility: () -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        visualTransformation = if (!isPasswordVisible) PasswordVisualTransformation() else VisualTransformation.None,
        trailingIcon = {
            IconButton(onClick = onTogglePasswordVisibility) {
                Icon(
                    painter = if (isPasswordVisible)
                        painterResource(id = R.drawable.baseline_visibility_off_24)
                    else
                        painterResource(id = R.drawable.baseline_visibility_24),
                    contentDescription = "Toggle password visibility"
                )
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        placeholder = { Text("*********") },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Offwhite12,
            unfocusedContainerColor = Offwhite12,
            focusedIndicatorColor = MainColor,
            unfocusedIndicatorColor = MainColor,
            focusedLabelColor = White12,
            cursorColor = MainColor
        )
    )
}

@Preview
@Composable
fun SingUpPreview(){
    SingUpBody()
}
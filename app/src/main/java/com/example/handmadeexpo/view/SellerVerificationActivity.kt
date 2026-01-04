package com.example.handmadeexpo.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Blue
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.handmadeexpo.R
import com.example.handmadeexpo.repo.SellerRepoImpl
import com.example.handmadeexpo.ui.theme.MainColor
import com.example.handmadeexpo.ui.theme.TextBlack
import com.example.handmadeexpo.viewmodel.SellerViewModel
import com.example.handmadeexpo.viewmodel.SellerViewModelFactory

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

    // 1. Initialize ViewModel with Factory to prevent crashes
    val viewModel: SellerViewModel = viewModel(
        factory = SellerViewModelFactory(SellerRepoImpl())
    )

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var selectedDocType by remember { mutableStateOf("National ID") }
    val docTypes = listOf("National ID", "Passport", "Driving License")
    var isLoading by remember { mutableStateOf(false) }

    // 2. Permission Logic
    val permissionToRequest = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_IMAGES
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }

    val imageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            imageLauncher.launch("image/*")
        } else {
            Toast.makeText(context, "Permission required to upload photos", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Verify Identity", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MainColor)
            )
        },
        bottomBar = {
            Box(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                Button(
                    onClick = {
                        val currentUser = viewModel.getCurrentUser()
                        if (imageUri != null && currentUser != null) {
                            isLoading = true
                            viewModel.uploadImage(context, imageUri!!) { success, imageUrl ->
                                if (success) {
                                    val updates = mapOf(
                                        "documentType" to selectedDocType,
                                        "documentUrl" to imageUrl,
                                        "verificationStatus" to "Pending"
                                    )
                                    viewModel.updateProfileFields(currentUser.uid, updates) { profileSuccess, profileMsg ->
                                        isLoading = false
                                        Toast.makeText(context, profileMsg, Toast.LENGTH_SHORT).show()

                                        if (profileSuccess) {
                                            // --- NAVIGATE TO SIGN IN ACTIVITY ---
                                            val intent = Intent(context, SignInActivity::class.java)
                                            // Clear back stack so they can't return to verification
                                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                            context.startActivity(intent)
                                        }
                                    }
                                } else {
                                    isLoading = false
                                    Toast.makeText(context, "Upload Failed: $imageUrl", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } else {
                            Toast.makeText(context, "Please select image", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MainColor),
                    enabled = !isLoading
                ) {
                    if (isLoading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    else Text("Submit for Verification", fontSize = 18.sp, color = Color.White)
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            Image(painterResource(R.drawable.finalbackground), null, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)

            Column(
                Modifier
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text("Document Details", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextBlack)
                Spacer(Modifier.height(12.dp))

                Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(12.dp)) {
                    Column(Modifier.padding(8.dp)) {
                        docTypes.forEach { type ->
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().clickable { selectedDocType = type }.padding(8.dp)) {
                                RadioButton(selected = (type == selectedDocType), onClick = { selectedDocType = type }, colors = RadioButtonDefaults.colors(selectedColor = MainColor))
                                Text(type, modifier = Modifier.padding(start = 8.dp), color = TextBlack)
                            }
                        }
                    }
                }
                Spacer(Modifier.height(24.dp))

                Text("Upload Image", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextBlack)
                Spacer(Modifier.height(12.dp))

                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clickable {
                            val isGranted = ContextCompat.checkSelfPermission(
                                context,
                                permissionToRequest
                            ) == PackageManager.PERMISSION_GRANTED

                            if (isGranted) {
                                imageLauncher.launch("image/*")
                            } else {
                                permissionLauncher.launch(permissionToRequest)
                            }
                        }
                ) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        if (imageUri != null) Image(rememberAsyncImagePainter(imageUri), null, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                        else Icon(painterResource(R.drawable.baseline_cloud_upload_24), null, Modifier.size(48.dp), tint = MainColor)
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))

                // Footer: Navigation to Sign In (if they already have an account)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Already have an account?", fontSize = 16.sp, color = MainColor)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Sign In",
                        color = Blue,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable {
                            val intent = Intent(context, SignInActivity::class.java)
                            context.startActivity(intent)
                        }
                    )
                }
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}
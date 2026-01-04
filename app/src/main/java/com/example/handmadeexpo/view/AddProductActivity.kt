package com.example.handmadeexpo.view

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.handmadeexpo.model.ProductModel
import com.example.handmadeexpo.repo.ProductRepoImpl
import com.example.handmadeexpo.repo.SellerRepoImpl
import com.example.handmadeexpo.ui.theme.*
import com.example.handmadeexpo.utils.ImageUtils
import com.example.handmadeexpo.viewmodel.ProductViewModel
import com.example.handmadeexpo.viewmodel.SellerViewModel

class AddProductActivity : ComponentActivity() {

    lateinit var imageUtils: ImageUtils
    var selectedImageUri by mutableStateOf<Uri?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        imageUtils = ImageUtils(this, this)
        imageUtils.registerLaunchers { uri ->
            selectedImageUri = uri
        }

        setContent {
            AddProductBody(
                selectedImageUri = selectedImageUri,
                onPickImage = { imageUtils.launchImagePicker() }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductBody(
    selectedImageUri: Uri?,
    onPickImage: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    var selectedCategory by remember { mutableStateOf("Choose category") }
    var pProductName by remember { mutableStateOf("") }
    var pPrice by remember { mutableStateOf("") }
    var dDescription by remember { mutableStateOf("") }
    var sStockQuantity by remember { mutableStateOf("") }

    var isLoading by remember { mutableStateOf(false) } // Loading state

    val context = LocalContext.current
    val activity = context as? Activity

    val repo = remember { ProductRepoImpl() }
    val viewModel = remember { ProductViewModel(repo) }
    val sRepo = remember { SellerRepoImpl() }
    val sViewModel = remember { SellerViewModel(sRepo) }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(MainColor),
                title = {
                    Text(
                        text = "Add Product",
                        color = White12,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            )
        },
        containerColor = cream
    ) { paddingValues ->

        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
                    .imePadding()
            ) {

                // IMAGE SECTION
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(lightGreen, RoundedCornerShape(16.dp))
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(110.dp)
                            .border(1.dp, borderGray, RoundedCornerShape(12.dp))
                            .clickable { onPickImage() },
                        contentAlignment = Alignment.Center
                    ) {
                        if (selectedImageUri != null) {
                            AsyncImage(
                                model = selectedImageUri,
                                contentDescription = "Selected Image",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.Add, null, tint = green1)
                                Text("Upload Image", fontSize = 12.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // PRODUCT NAME
                Text("Product Name", fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = pProductName,
                    onValueChange = { pProductName = it },
                    placeholder = { Text("Enter product name") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // PRICE
                Text("Price (in NPR)", fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = pPrice,
                    onValueChange = { pPrice = it },
                    placeholder = { Text("Enter price") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // CATEGORY
                Text("Category", fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(6.dp))

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = selectedCategory,
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                        },
                        shape = RoundedCornerShape(12.dp)
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        listOf(
                            "Statue",
                            "Wooden Mask",
                            "Singing Bowl",
                            "Painting",
                            "Thanka",
                            "Wall Decor",
                            "Others"
                        ).forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category) },
                                onClick = {
                                    selectedCategory = category
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // DESCRIPTION
                Text("Description", fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = dDescription,
                    onValueChange = { dDescription = it },
                    placeholder = { Text("Describe your product") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // STOCK
                Text("Stock Quantity", fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = sStockQuantity,
                    onValueChange = { sStockQuantity = it },
                    placeholder = { Text("Enter stock quantity") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // ADD PRODUCT BUTTON
                Button(
                    onClick = {
                        // Validation
                        when {
                            selectedImageUri == null -> {
                                Toast.makeText(context, "Please select an image", Toast.LENGTH_SHORT).show()
                            }
                            pProductName.isBlank() -> {
                                Toast.makeText(context, "Please enter product name", Toast.LENGTH_SHORT).show()
                            }
                            pPrice.isBlank() -> {
                                Toast.makeText(context, "Please enter price", Toast.LENGTH_SHORT).show()
                            }
                            pPrice.toDoubleOrNull() == null -> {
                                Toast.makeText(context, "Price must be a number", Toast.LENGTH_SHORT).show()
                            }
                            selectedCategory == "Choose category" -> {
                                Toast.makeText(context, "Please select a category", Toast.LENGTH_SHORT).show()
                            }
                            dDescription.isBlank() -> {
                                Toast.makeText(context, "Please enter description", Toast.LENGTH_SHORT).show()
                            }
                            sStockQuantity.isBlank() -> {
                                Toast.makeText(context, "Please enter stock quantity", Toast.LENGTH_SHORT).show()
                            }
                            sStockQuantity.toIntOrNull() == null -> {
                                Toast.makeText(context, "Stock quantity must be a number", Toast.LENGTH_SHORT).show()
                            }
                            else -> {
                                // All validations passed
                                isLoading = true
                                viewModel.uploadImage(context, selectedImageUri) { imageUrl ->
                                    if (imageUrl != null) {
                                        val currentUserId = sViewModel.getCurrentUser()?.uid ?: ""
                                        val product = ProductModel(
                                            productId = "",
                                            name = pProductName,
                                            price = pPrice.toDouble(),
                                            description = dDescription,
                                            image = imageUrl,
                                            categoryId = selectedCategory,
                                            stock = sStockQuantity.toInt(),
                                            sellerId = currentUserId
                                        )

                                        viewModel.addProduct(product) { success, message, _ ->
                                            isLoading = false
                                            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                                            if (success) activity?.finish()
                                        }
                                    } else {
                                        isLoading = false
                                        Log.e("UPLOAD_ERROR", "Image upload failed")
                                        Toast.makeText(context, "Image upload failed", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = green1)
                ) {
                    Text("Add Product", color = Color.White, fontSize = 16.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Loading Overlay
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = green1)
                }
            }
        }
    }
}

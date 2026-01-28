package com.example.handmadeexpo.view

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.handmadeexpo.model.ProductModel
import com.example.handmadeexpo.repo.ProductRepoImpl
import com.example.handmadeexpo.repo.SellerRepoImpl
import com.example.handmadeexpo.utils.ImageUtils
import com.example.handmadeexpo.viewmodel.ProductViewModel
import com.example.handmadeexpo.viewmodel.SellerViewModel

// --- Modern Design Constants ---
private val AppBackground = Color(0xFFF5F7FA)
private val PrimaryGreen = Color(0xFF4CAF50)
private val TextDark = Color(0xFF212121)
private val TextGray = Color(0xFF757575)

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
                onPickImage = { imageUtils.launchImagePicker() },
                onBackClick = { finish() }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductBody(
    selectedImageUri: Uri?,
    onPickImage: () -> Unit,
    onBackClick: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    // Form State
    var pProductName by remember { mutableStateOf("") }
    var pPrice by remember { mutableStateOf("") }
    var dDescription by remember { mutableStateOf("") }
    var sStockQuantity by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Choose category") }

    var isLoading by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val activity = context as? Activity

    // Dependencies
    val repo = remember { ProductRepoImpl() }
    val viewModel = remember { ProductViewModel(repo) }
    val sRepo = remember { SellerRepoImpl() }
    val sViewModel = remember { SellerViewModel(sRepo) }

    Scaffold(
        containerColor = AppBackground,
        topBar = {
            // We leave this empty because we have a custom header, but Scaffold expects it
        }
    ) { paddingValues -> // The error was here because this wasn't used

        Box(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues) // FIX: Applied paddingValues here
        ) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 50.dp) // Extra padding for bottom
            ) {
                // --- 1. Header Section (Overlapping) ---
                Box(modifier = Modifier.fillMaxWidth()) {
                    // Green Gradient Banner
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(PrimaryGreen, Color(0xFF43A047))
                                ),
                                shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
                            )
                    ) {
                        // Header Content
                        Column(modifier = Modifier.padding(top = 48.dp, start = 16.dp, end = 16.dp)) {
                            // Back Button & Title
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(onClick = onBackClick) {
                                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                                }
                                Text(
                                    "Add New Product",
                                    color = Color.White,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // Floating Image Picker
                    Box(
                        modifier = Modifier
                            .padding(top = 110.dp) // Overlap the header
                            .align(Alignment.TopCenter)
                    ) {
                        Card(
                            modifier = Modifier
                                .size(140.dp)
                                .clickable { onPickImage() },
                            shape = RoundedCornerShape(24.dp),
                            elevation = CardDefaults.cardElevation(8.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                                if (selectedImageUri != null) {
                                    AsyncImage(
                                        model = selectedImageUri,
                                        contentDescription = "Selected Image",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                    // Edit Overlay
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(Color.Black.copy(alpha = 0.2f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.Edit, null, tint = Color.White)
                                    }
                                } else {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(
                                            Icons.Default.AddPhotoAlternate,
                                            null,
                                            tint = PrimaryGreen,
                                            modifier = Modifier.size(40.dp)
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            "Upload Photo",
                                            fontSize = 12.sp,
                                            color = TextGray,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }
                        }
                    }
                } // End Header Box

                Spacer(modifier = Modifier.height(24.dp))

                // --- 2. Form Content ---
                Column(modifier = Modifier.padding(horizontal = 20.dp)) {

                    Text(
                        "Product Details",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Form Container Card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(2.dp, RoundedCornerShape(16.dp)),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {

                            // Name
                            ModernTextField(
                                value = pProductName,
                                onValueChange = { pProductName = it },
                                label = "Product Name",
                                icon = Icons.Default.ShoppingBag
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Category Dropdown
                            Box(modifier = Modifier.fillMaxWidth()) {
                                ExposedDropdownMenuBox(
                                    expanded = expanded,
                                    onExpandedChange = { expanded = !expanded }
                                ) {
                                    OutlinedTextField(
                                        value = selectedCategory,
                                        onValueChange = {},
                                        readOnly = true,
                                        label = { Text("Category") },
                                        leadingIcon = { Icon(Icons.Default.Category, null, tint = PrimaryGreen) },
                                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = PrimaryGreen,
                                            focusedLabelColor = PrimaryGreen,
                                            unfocusedLabelColor = TextGray
                                        )
                                    )

                                    ExposedDropdownMenu(
                                        expanded = expanded,
                                        onDismissRequest = { expanded = false },
                                        modifier = Modifier.background(Color.White)
                                    ) {
                                        listOf("Statue", "Wooden Mask", "Singing Bowl", "Painting", "Thanka", "Wall Decor", "Others").forEach { category ->
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
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Row for Price and Stock
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Box(modifier = Modifier.weight(1f)) {
                                    ModernTextField(
                                        value = pPrice,
                                        onValueChange = { pPrice = it },
                                        label = "Price (Rs)",
                                        icon = Icons.Default.AttachMoney,
                                        keyboardType = KeyboardType.Number
                                    )
                                }
                                Box(modifier = Modifier.weight(1f)) {
                                    ModernTextField(
                                        value = sStockQuantity,
                                        onValueChange = { sStockQuantity = it },
                                        label = "Stock",
                                        icon = Icons.Default.Inventory,
                                        keyboardType = KeyboardType.Number
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Description
                            ModernTextField(
                                value = dDescription,
                                onValueChange = { dDescription = it },
                                label = "Description",
                                icon = Icons.Default.Description,
                                singleLine = false,
                                minLines = 3
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // --- 3. Submit Button ---
                    Button(
                        onClick = {
                            // Validation Logic
                            when {
                                selectedImageUri == null -> Toast.makeText(context, "Please select an image", Toast.LENGTH_SHORT).show()
                                pProductName.isBlank() -> Toast.makeText(context, "Enter product name", Toast.LENGTH_SHORT).show()
                                pPrice.isBlank() -> Toast.makeText(context, "Enter price", Toast.LENGTH_SHORT).show()
                                pPrice.toDoubleOrNull() == null -> Toast.makeText(context, "Price must be a number", Toast.LENGTH_SHORT).show()
                                selectedCategory == "Choose category" -> Toast.makeText(context, "Select a category", Toast.LENGTH_SHORT).show()
                                dDescription.isBlank() -> Toast.makeText(context, "Enter description", Toast.LENGTH_SHORT).show()
                                sStockQuantity.isBlank() -> Toast.makeText(context, "Enter stock quantity", Toast.LENGTH_SHORT).show()
                                sStockQuantity.toIntOrNull() == null -> Toast.makeText(context, "Stock must be a number", Toast.LENGTH_SHORT).show()
                                else -> {
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
                                            Toast.makeText(context, "Image upload failed", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .shadow(4.dp, RoundedCornerShape(12.dp)),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
                    ) {
                        Text(
                            "Publish Product",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            // Loading Overlay
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f))
                        .clickable(enabled = false) {}, // Block clicks
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(color = PrimaryGreen)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Uploading product...", color = TextDark)
                        }
                    }
                }
            }
        }
    }
}

// --- Helper Composable for Text Fields ---
@Composable
fun ModernTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    keyboardType: KeyboardType = KeyboardType.Text,
    singleLine: Boolean = true,
    minLines: Int = 1
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = { Text(label) },
        leadingIcon = { Icon(icon, null, tint = PrimaryGreen) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        singleLine = singleLine,
        minLines = minLines,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = PrimaryGreen,
            focusedLabelColor = PrimaryGreen,
            unfocusedLabelColor = TextGray,
            cursorColor = PrimaryGreen
        )
    )
}
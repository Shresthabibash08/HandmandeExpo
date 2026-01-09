package com.example.handmadeexpo.view

import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.handmadeexpo.R
import com.example.handmadeexpo.model.ProductModel
import com.example.handmadeexpo.repo.ProductRepoImpl
import com.example.handmadeexpo.ui.theme.Blue1
import com.example.handmadeexpo.ui.theme.MainColor
import com.example.handmadeexpo.viewmodel.ProductViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryScreen(sellerId: String) {
    val context = LocalContext.current
    val productViewModel: ProductViewModel = remember { ProductViewModel(ProductRepoImpl()) }

    val sellerProducts by productViewModel.sellerProducts.observeAsState(initial = emptyList())
    val selectedProduct by productViewModel.products.observeAsState()

    var showDialog by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }

    // --- Total Value ---
    val totalAmount by remember(sellerProducts) {
        derivedStateOf { sellerProducts.sumOf { it.price } }
    }

    // --- Refresh trigger ---
    var refreshTrigger by remember { mutableStateOf(0) }

    // --- Fetch seller products ---
    LaunchedEffect(sellerId, refreshTrigger) {
        isLoading = true
        productViewModel.getProductsBySeller(sellerId)
        delay(300) // smooth loading
        isLoading = false
    }

    // --- Launcher for AddProductActivity ---
    val addProductLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        // Refresh product list after returning from AddProductActivity
        refreshTrigger++
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Background
        Image(
            painter = painterResource(id = R.drawable.bg7),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Box(modifier = Modifier.fillMaxSize().background(Color.White.copy(alpha = 0.4f)))

        Scaffold(
            containerColor = Color.Transparent,
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    onClick = {
                        val intent = Intent(context, AddProductActivity::class.java)
                        addProductLauncher.launch(intent)
                    },
                    containerColor = MainColor,
                    contentColor = Color.White,
                    shape = CircleShape,
                    icon = { Icon(Icons.Default.Add, null) },
                    text = { Text("Add Item") }
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                Text(
                    text = "Inventory Manager",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 25.sp,
                    color = MainColor,
                    modifier = Modifier.padding(start = 20.dp, top = 16.dp)
                )

                InventoryHeaderCard(sellerProducts.size, totalAmount)

                Box(modifier = Modifier.fillMaxSize()) {
                    if (!isLoading) {
                        if (sellerProducts.isNotEmpty()) {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(bottom = 100.dp)
                            ) {
                                items(sellerProducts, key = { it.productId }) { product ->
                                    ProductItemRow(
                                        product = product,
                                        onEdit = {
                                            productViewModel.getProductById(product.productId)
                                            showDialog = true
                                        },
                                        onDelete = {
                                            productViewModel.deleteProduct(product.productId) { success, msg ->
                                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                                if (success) {
                                                    refreshTrigger++ // refresh list after deletion
                                                }
                                            }
                                        }
                                    )
                                }
                            }
                        } else {
                            EmptyInventoryView()
                        }
                    }
                }
            }

            // --- Loading Overlay ---
            AnimatedVisibility(
                visible = isLoading,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Box(Modifier.fillMaxSize().background(Color.White), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Blue1)
                }
            }

            // --- Edit Dialog ---
            if (showDialog && selectedProduct != null) {
                EditProductDialog(
                    product = selectedProduct!!,
                    productViewModel = productViewModel,
                    onDismiss = { showDialog = false; refreshTrigger++ } // refresh after edit
                )
            }
        }
    }
}


@Composable
fun InventoryHeaderCard(count: Int, total: Double) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MainColor),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text("Total Asset Value", color = Color.White.copy(0.7f), fontSize = 14.sp)
            Text(
                "Rs. ${String.format("%,.2f", total)}",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(8.dp))
            Surface(color = Color.White.copy(0.2f), shape = RoundedCornerShape(8.dp)) {
                Text("$count Active Products", modifier = Modifier.padding(8.dp, 4.dp), color = Color.White, fontSize = 12.sp)
            }
        }
    }
}


@Composable
fun ProductItemRow(product: ProductModel, onEdit: () -> Unit, onDelete: () -> Unit) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = product.image,
                contentDescription = null,
                modifier = Modifier.size(80.dp).clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(product.name, fontWeight = FontWeight.Bold, maxLines = 1, fontSize = 15.sp)
                Text("Rs. ${product.price}", color = MainColor, fontWeight = FontWeight.SemiBold)
                Text(
                    "Stock: ${product.stock}",
                    color = if (product.stock < 5) Color.Red else Color.Gray,
                    fontSize = 12.sp
                )
            }
            Row {
                IconButton(onClick = onEdit) { Icon(Icons.Default.Edit, "Edit", tint = Color.Gray) }
                IconButton(onClick = { showDeleteDialog = true }) {
                    Icon(Icons.Default.Delete, "Delete", tint = Color(0xFFEF5350))
                }
            }
        }
    }

    // --- Delete Confirmation Dialog ---
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Confirm Delete") },
            text = { Text("Are you sure you want to delete \"${product.name}\"?") },
            confirmButton = {
                Button(
                    onClick = {
                        onDelete()
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MainColor)
                ) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel", color = Color.Black)
                }
            }
        )
    }
}


@Composable
fun EmptyInventoryView() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(id = R.drawable.baseline_inventory_24),
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = Color.LightGray
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "No products found", color = Color.Gray)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProductDialog(
    product: ProductModel,
    productViewModel: ProductViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    var name by remember { mutableStateOf(product.name) }
    var price by remember { mutableStateOf(product.price.toString()) }
    var desc by remember { mutableStateOf(product.description) }
    var image by remember { mutableStateOf(product.image) }
    var isUploading by remember { mutableStateOf(false) }

    val imageLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            isUploading = true
            productViewModel.uploadImage(context, it) { url ->
                url?.let { uploadedUrl ->
                    image = uploadedUrl
                }
                isUploading = false
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    if (isUploading) {
                        Toast.makeText(context, "Image is uploading. Wait...", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    val updated = product.copy(
                        name = name,
                        price = price.toDoubleOrNull() ?: 0.0,
                        description = desc,
                        image = image
                    )
                    productViewModel.updateProduct(product.productId, updated) { success, msg ->
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                        if (success) onDismiss()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = MainColor)
            ) { Text("Update") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel", color = Color.Black) } },
        title = { Text("Edit Product", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .fillMaxWidth()
            ) {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    AsyncImage(
                        model = image,
                        contentDescription = null,
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(Color.LightGray),
                        contentScale = ContentScale.Crop
                    )
                    if (isUploading) {
                        CircularProgressIndicator(modifier = Modifier.size(30.dp), color = MainColor)
                    }
                    IconButton(
                        onClick = { imageLauncher.launch("image/*") },
                        modifier = Modifier.align(Alignment.BottomEnd)
                            .background(Color.White, CircleShape)
                            .size(30.dp)
                    ) {
                        Icon(Icons.Default.Edit, null, modifier = Modifier.size(16.dp))
                    }
                }

                Spacer(Modifier.height(16.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("Price") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text("Description") },
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    )
}

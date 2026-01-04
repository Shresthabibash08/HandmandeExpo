package com.example.handmadeexpo.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.handmadeexpo.viewmodel.AdminViewModel

@Composable
fun AdminProductListScreen(viewModel: AdminViewModel) {
    var productToDelete by remember { mutableStateOf<String?>(null) }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Product Management", fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Spacer(Modifier.height(16.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(viewModel.products) { product ->
                Card(Modifier.fillMaxWidth()) {
                    Row(Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        AsyncImage(
                            model = product.image,
                            contentDescription = null,
                            modifier = Modifier.size(60.dp),
                            contentScale = ContentScale.Crop
                        )
                        Column(Modifier.weight(1f).padding(start = 12.dp)) {
                            Text(product.name, fontWeight = FontWeight.Bold)
                            Text("Rs. ${product.price}", color = Color.Gray)
                        }
                        IconButton(onClick = { productToDelete = product.productId }) {
                            Icon(Icons.Default.Delete, null, tint = Color.Red)
                        }
                    }
                }
            }
        }
    }

    if (productToDelete != null) {
        ProductDeleteDialog(
            onConfirm = {
                viewModel.deleteProduct(productToDelete!!)
                productToDelete = null
            },
            onDismiss = { productToDelete = null }
        )
    }
}

@Composable
fun ProductDeleteDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Confirm Delete") },
        text = { Text("Are you sure you want to delete this product?") },
        confirmButton = {
            Button(onClick = onConfirm, colors = ButtonDefaults.buttonColors(containerColor = Color.Red)) {
                Text("Delete")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
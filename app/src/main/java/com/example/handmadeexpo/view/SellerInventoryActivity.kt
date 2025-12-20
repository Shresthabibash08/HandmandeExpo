package com.example.handmadeexpo.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog


data class SellerProductItem(
    val id: Int,
    var name: String,
    var price: Int,
    var stock: Int,
    var category: String
)

class SellerInventoryActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Surface(modifier = Modifier.fillMaxSize(), color = Color.White) {
                SellerInventoryScreen(onBackClick = { finish() })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellerInventoryScreen(onBackClick: () -> Unit) {
    // --- 1. STATE ---
    val inventory = remember {
        mutableStateListOf(
            SellerProductItem(1, "Dhaka Topi", 500, 45, "Clothing"),
            SellerProductItem(2, "Hemp Bag", 1500, 12, "Accessories"),
            SellerProductItem(3, "Clay Water Pot", 300, 5, "Pottery"),
            SellerProductItem(4, "Yak Wool Blanket", 4500, 8, "Bedding")
        )
    }

    var showDialog by remember { mutableStateOf(false) }
    var productToEdit by remember { mutableStateOf<SellerProductItem?>(null) }

    val totalValue = inventory.sumOf { it.price * it.stock }
    val totalItems = inventory.sumOf { it.stock }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Inventory Manager", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Blue,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                ),
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    productToEdit = null // Reset for "Add" mode
                    showDialog = true
                },
                containerColor = Color.Blue,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Product")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF5F5DC)) // Cream Background
                .padding(16.dp)
        ) {
            // --- 2. SUMMARY CARDS ---
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                InventoryStatCard(
                    title = "Total Asset Value",
                    value = "Rs. $totalValue",
                    color = Color(0xFF36A806),
                    modifier = Modifier.weight(1f)
                )
                InventoryStatCard(
                    title = "Total Stock",
                    value = "$totalItems Units",
                    color = Color(0xFFE65100),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
            Text("Product List", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(10.dp))

            // --- 3. INVENTORY LIST ---
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(inventory, key = { it.id }) { product ->
                    InventoryRow(
                        product = product,
                        onEdit = {
                            productToEdit = product
                            showDialog = true
                        },
                        onDelete = {
                            inventory.remove(product)
                        }
                    )
                }
            }
        }
    }

    // --- 4. POPUP DIALOG ---
    if (showDialog) {
        InventoryEntryDialog(
            productToEdit = productToEdit,
            onDismiss = { showDialog = false },
            onSave = { name, price, stock, cat ->
                if (productToEdit == null) {
                    val newId = (inventory.maxOfOrNull { it.id } ?: 0) + 1
                    inventory.add(SellerProductItem(newId, name, price, stock, cat))
                } else {

                    val index = inventory.indexOf(productToEdit)
                    if (index != -1) {
                        inventory[index] = inventory[index].copy(
                            name = name,
                            price = price,
                            stock = stock,
                            category = cat
                        )
                    }
                }
                showDialog = false
            }
        )
    }
}



@Composable
fun InventoryStatCard(title: String, value: String, color: Color, modifier: Modifier) {
    Card(
        colors = CardDefaults.cardColors(containerColor = color),
        modifier = modifier.height(100.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text(title, fontSize = 14.sp, color = Color.White.copy(alpha = 0.9f))
        }
    }
}

@Composable
fun InventoryRow(
    product: SellerProductItem,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val isLowStock = product.stock < 10

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(product.name, fontWeight = FontWeight.Bold)
                Text("Rs. ${product.price}  •  ${product.category}", fontSize = 12.sp, color = Color.Gray)
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    "Stock: ${product.stock}",
                    fontWeight = FontWeight.Bold,
                    color = if (isLowStock) Color.Red else Color.Black
                )
                if (isLowStock) Text("Low Stock!", fontSize = 10.sp, color = Color.Red)
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Actions
            IconButton(onClick = onEdit, modifier = Modifier.size(24.dp)) {
                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color.Gray)
            }
            Spacer(modifier = Modifier.width(16.dp))
            IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
            }
        }
    }
}

@Composable
fun InventoryEntryDialog(
    productToEdit: SellerProductItem?,
    onDismiss: () -> Unit,
    onSave: (String, Int, Int, String) -> Unit
) {
    var name by remember { mutableStateOf(productToEdit?.name ?: "") }
    var price by remember { mutableStateOf(productToEdit?.price?.toString() ?: "") }
    var stock by remember { mutableStateOf(productToEdit?.stock?.toString() ?: "") }
    var category by remember { mutableStateOf(productToEdit?.category ?: "") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = if (productToEdit == null) "Add New Product" else "Edit Product",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Product Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = price,
                        onValueChange = { if (it.all { c -> c.isDigit() }) price = it },
                        label = { Text("Price") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = stock,
                        onValueChange = { if (it.all { c -> c.isDigit() }) stock = it },
                        label = { Text("Stock") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Category") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                    Button(
                        onClick = {
                            if (name.isNotEmpty()) {
                                onSave(
                                    name,
                                    price.toIntOrNull() ?: 0,
                                    stock.toIntOrNull() ?: 0,
                                    category
                                )
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Blue)
                    ) {
                        Text("Save")
                    }
                }
            }
        }
    }
}
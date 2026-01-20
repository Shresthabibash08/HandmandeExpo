package com.example.handmadeexpo.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.handmadeexpo.model.OrderModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class CheckoutViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()
    private val ordersRef = database.getReference("Orders")
    private val productsRef = database.getReference("products")
    private val buyersRef = database.getReference("Buyer")

    var currentUserName = mutableStateOf("")
        private set

    // NEW: Stock validation state
    var stockValidationError = mutableStateOf<String?>(null)
        private set

    fun fetchUserInfo() {
        val userId = auth.currentUser?.uid ?: return
        buyersRef.child(userId).child("buyerName")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    currentUserName.value = snapshot.getValue(String::class.java) ?: ""
                }
                override fun onCancelled(error: DatabaseError) {}
            })
    }

    // NEW: Validate stock before placing order
    fun validateStock(
        orderItems: List<com.example.handmadeexpo.model.OrderItem>,
        callback: (Boolean, String, Map<String, Int>?) -> Unit
    ) {
        val productIds = orderItems.map { it.productId }
        var checkedCount = 0
        val stockInfo = mutableMapOf<String, Int>() // productId to available stock
        val outOfStockItems = mutableListOf<String>() // product names that are out of stock
        val insufficientStockItems = mutableListOf<Pair<String, Int>>() // product name to available quantity

        productIds.forEach { productId ->
            productsRef.child(productId).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val availableStock = snapshot.child("stock").getValue(Int::class.java) ?: 0
                    val productName = snapshot.child("name").getValue(String::class.java) ?: "Unknown"

                    stockInfo[productId] = availableStock

                    // Find the requested quantity for this product
                    val requestedQty = orderItems.find { it.productId == productId }?.quantity ?: 0

                    // Check if out of stock
                    if (availableStock <= 0) {
                        outOfStockItems.add(productName)
                    }
                    // Check if insufficient stock
                    else if (availableStock < requestedQty) {
                        insufficientStockItems.add(Pair(productName, availableStock))
                    }

                    checkedCount++

                    // When all products are checked
                    if (checkedCount == productIds.size) {
                        when {
                            outOfStockItems.isNotEmpty() -> {
                                val message = "Out of stock: ${outOfStockItems.joinToString(", ")}"
                                stockValidationError.value = message
                                callback(false, message, null)
                            }
                            insufficientStockItems.isNotEmpty() -> {
                                val message = insufficientStockItems.joinToString("\n") {
                                    "${it.first}: Only ${it.second} available"
                                }
                                stockValidationError.value = message
                                callback(false, message, null)
                            }
                            else -> {
                                stockValidationError.value = null
                                callback(true, "Stock validated", stockInfo)
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    checkedCount++
                    if (checkedCount == productIds.size) {
                        callback(false, "Error checking stock: ${error.message}", null)
                    }
                }
            })
        }
    }

    // UPDATED: Place order with stock validation and update
    fun placeOrder(order: OrderModel, callback: (Boolean, String) -> Unit) {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            callback(false, "User not logged in")
            return
        }

        // Step 1: Validate stock first
        validateStock(order.items) { stockValid, message, stockInfo ->
            if (!stockValid) {
                callback(false, message)
                return@validateStock
            }

            // Step 2: Create order ID and prepare order
            val orderId = ordersRef.push().key ?: return@validateStock
            val finalOrder = order.copy(orderId = orderId, buyerId = userId)

            // Step 3: Place order and update stock atomically
            ordersRef.child(orderId).setValue(finalOrder.toMap())
                .addOnSuccessListener {
                    // Step 4: Update stock for each product
                    updateProductStock(order.items) { stockUpdateSuccess, stockMessage ->
                        if (stockUpdateSuccess) {
                            callback(true, "Order placed successfully!")
                        } else {
                            // Order was placed but stock update failed
                            callback(true, "Order placed but stock update incomplete: $stockMessage")
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    callback(false, "Failed to place order: ${exception.message}")
                }
        }
    }

    // NEW: Update stock for all products in the order
    private fun updateProductStock(
        orderItems: List<com.example.handmadeexpo.model.OrderItem>,
        callback: (Boolean, String) -> Unit
    ) {
        var updatedCount = 0
        var hasError = false
        val errorMessages = mutableListOf<String>()

        orderItems.forEach { item ->
            productsRef.child(item.productId).runTransaction(object : Transaction.Handler {
                override fun doTransaction(currentData: MutableData): Transaction.Result {
                    val currentStock = currentData.child("stock").getValue(Int::class.java) ?: 0
                    val currentSold = currentData.child("sold").getValue(Int::class.java) ?: 0

                    // Calculate new values
                    val newStock = currentStock - item.quantity
                    val newSold = currentSold + item.quantity

                    // Update the values
                    currentData.child("stock").value = if (newStock < 0) 0 else newStock
                    currentData.child("sold").value = newSold

                    return Transaction.success(currentData)
                }

                override fun onComplete(
                    error: DatabaseError?,
                    committed: Boolean,
                    snapshot: DataSnapshot?
                ) {
                    updatedCount++

                    if (error != null || !committed) {
                        hasError = true
                        errorMessages.add("${item.productName}: ${error?.message ?: "Update failed"}")
                    }

                    // When all items are processed
                    if (updatedCount == orderItems.size) {
                        if (hasError) {
                            callback(false, errorMessages.joinToString("; "))
                        } else {
                            callback(true, "Stock updated successfully")
                        }
                    }
                }
            })
        }
    }

    // NEW: Check if a single product has sufficient stock
    fun checkProductStock(
        productId: String,
        requestedQuantity: Int,
        callback: (Boolean, String, Int) -> Unit
    ) {
        productsRef.child(productId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val availableStock = snapshot.child("stock").getValue(Int::class.java) ?: 0
                val productName = snapshot.child("name").getValue(String::class.java) ?: "Product"

                when {
                    availableStock <= 0 -> {
                        callback(false, "$productName is out of stock", 0)
                    }
                    availableStock < requestedQuantity -> {
                        callback(false, "Only $availableStock units of $productName available", availableStock)
                    }
                    else -> {
                        callback(true, "Stock available", availableStock)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                callback(false, "Error checking stock: ${error.message}", 0)
            }
        })
    }
}
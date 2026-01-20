package com.example.handmadeexpo.repo

import com.example.handmadeexpo.model.OrderModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DatabaseReference

class OrderRepoImpl : OrderRepo {

    // Matches the style of your BuyerRepoImpl
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    // We store orders under a main "Orders" node
    private val ref: DatabaseReference = database.getReference("Orders")

    override fun placeOrder(order: OrderModel, callback: (Boolean, String) -> Unit) {
        val currentUser = auth.currentUser

        // 1. Check Login
        if (currentUser == null) {
            callback(false, "Error: You are not logged in.")
            return
        }

        // 2. Generate a unique key for the order (like 'push()' in Realtime DB)
        val orderRef = ref.push()
        val orderId = orderRef.key

        if (orderId == null) {
            callback(false, "Failed to generate Order ID")
            return
        }

        // 3. Add ID and UserID to the order object
        val finalOrder = order.copy(
            orderId = orderId,
            buyerId = currentUser.uid
        )

        // 4. Save to Realtime Database
        orderRef.setValue(finalOrder)
            .addOnSuccessListener {
                callback(true, "Order Placed Successfully!")
            }
            .addOnFailureListener { e ->
                callback(false, "Failed: ${e.message}")
            }
    }
}
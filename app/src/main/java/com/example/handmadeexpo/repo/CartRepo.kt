package com.example.handmadeexpo.repo

import com.example.handmadeexpo.model.CartItem
import com.google.firebase.database.FirebaseDatabase

class CartRepo{

    private val database = FirebaseDatabase.getInstance().getReference("carts")

    fun addToCart(cartItem: CartItem, onComplete: (Boolean) -> Unit) {
        val cartRef = database.child(cartItem.userId).child(cartItem.productId)

        cartRef.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                val currentQty = snapshot.child("quantity").getValue(Int::class.java) ?: 1
                cartRef.child("quantity").setValue(currentQty + 1)
                    .addOnCompleteListener { task -> onComplete(task.isSuccessful) }
            } else {
                cartRef.setValue(cartItem).addOnCompleteListener { task ->
                    onComplete(task.isSuccessful)
                }
            }
        }.addOnFailureListener { onComplete(false) }
    }

    fun getCartItems(userId: String, onResult: (List<CartItem>) -> Unit) {
        database.child(userId).get().addOnSuccessListener { snapshot ->
            val cartItems = mutableListOf<CartItem>()
            for (itemSnapshot in snapshot.children) {
                val cartItem = CartItem(
                    productId = itemSnapshot.key ?: "",
                    userId = userId,
                    name = itemSnapshot.child("name").getValue(String::class.java) ?: "",
                    price = itemSnapshot.child("price").getValue(Double::class.java) ?: 0.0,
                    image = itemSnapshot.child("image").getValue(String::class.java) ?: "",
                    quantity = itemSnapshot.child("quantity").getValue(Int::class.java) ?: 1
                )
                cartItems.add(cartItem)
            }
            onResult(cartItems)
        }
    }

    fun removeFromCart(userId: String, productId: String, onComplete: (Boolean) -> Unit) {
        database.child(userId).child(productId).removeValue()
            .addOnCompleteListener { task -> onComplete(task.isSuccessful) }
    }

    fun updateQuantity(userId: String, productId: String, quantity: Int, onComplete: (Boolean) -> Unit) {
        database.child(userId).child(productId).child("quantity").setValue(quantity)
            .addOnCompleteListener { task -> onComplete(task.isSuccessful) }
    }
}

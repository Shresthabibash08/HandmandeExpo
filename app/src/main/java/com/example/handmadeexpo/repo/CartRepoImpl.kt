package com.example.handmadeexpo.repo

import com.example.handmadeexpo.model.CartItem
import com.google.firebase.database.*

class CartRepoImpl : CartRepo {
    private val database = FirebaseDatabase.getInstance().getReference("carts")

    override fun addToCart(cartItem: CartItem, onComplete: (Boolean) -> Unit) {
        val cartRef = database.child(cartItem.userId).child(cartItem.productId)
        cartRef.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                val currentQty = snapshot.child("quantity").getValue(Int::class.java) ?: 1
                cartRef.child("quantity").setValue(currentQty + 1)
                    .addOnCompleteListener { onComplete(it.isSuccessful) }
            } else {
                cartRef.setValue(cartItem).addOnCompleteListener { onComplete(it.isSuccessful) }
            }
        }.addOnFailureListener { onComplete(false) }
    }

    override fun getCartItems(userId: String, onResult: (List<CartItem>) -> Unit) {
        database.child(userId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val items = snapshot.children.mapNotNull { it.getValue(CartItem::class.java) }
                onResult(items)
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    override fun removeFromCart(userId: String, productId: String, onComplete: (Boolean) -> Unit) {
        database.child(userId).child(productId).removeValue()
            .addOnCompleteListener { onComplete(it.isSuccessful) }
    }

    override fun updateQuantity(userId: String, productId: String, quantity: Int, onComplete: (Boolean) -> Unit) {
        database.child(userId).child(productId).child("quantity").setValue(quantity)
            .addOnCompleteListener { onComplete(it.isSuccessful) }
    }
}
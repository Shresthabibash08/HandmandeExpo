package com.example.handmadeexpo.repo

import com.example.handmadeexpo.model.CartItem

interface CartRepo {
    fun addToCart(cartItem: CartItem, onComplete: (Boolean) -> Unit)
    fun getCartItems(userId: String, onResult: (List<CartItem>) -> Unit)
    fun removeFromCart(userId: String, productId: String, onComplete: (Boolean) -> Unit)
    fun updateQuantity(userId: String, productId: String, quantity: Int, onComplete: (Boolean) -> Unit)
}

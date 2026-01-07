package com.example.handmadeexpo.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.handmadeexpo.model.CartItem
import com.example.handmadeexpo.repo.CartRepo

class CartViewModel(private val repository: CartRepo) : ViewModel() {

    private val _cartItems = mutableStateOf<List<CartItem>>(emptyList())
    val cartItems: State<List<CartItem>> = _cartItems

    fun loadCart(userId: String) {
        repository.getCartItems(userId) { items ->
            _cartItems.value = items
        }
    }

    fun addToCart(cartItem: CartItem, onComplete: (Boolean) -> Unit) {
        repository.addToCart(cartItem, onComplete)
    }

    fun removeFromCart(userId: String, productId: String, onComplete: (Boolean) -> Unit) {
        repository.removeFromCart(userId, productId, onComplete)
    }

    fun updateQuantity(userId: String, productId: String, quantity: Int, onComplete: (Boolean) -> Unit) {
        repository.updateQuantity(userId, productId, quantity, onComplete)
    }
}

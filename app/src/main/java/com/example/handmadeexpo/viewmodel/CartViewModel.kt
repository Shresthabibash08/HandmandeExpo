package com.example.handmadeexpo.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.handmadeexpo.model.CartItem
import com.example.handmadeexpo.repo.CartRepo

class CartViewModel(private val repository: CartRepo) : ViewModel() {

    // 1. State for the list of items in the cart
    // Using mutableStateListOf so Compose detects changes to individual items automatically
    var cartItems = mutableStateListOf<CartItem>()
        private set

    // 2. State for the loading screen
    // We use .value in the UI to check this state
    var isLoading = mutableStateOf(false)
        private set

    /**
     * Fetches all cart items for a specific user from the repository.
     * Updates isLoading during the process.
     */
    fun loadCart(userId: String) {
        if (userId.isEmpty()) return

        isLoading.value = true
        repository.getCartItems(userId) { items ->
            cartItems.clear()
            cartItems.addAll(items)
            isLoading.value = false
        }
    }

    /**
     * Adds a product to the cart.
     * If the item exists, the repo handles incrementing the quantity.
     */
    fun addToCart(cartItem: CartItem, onComplete: (Boolean) -> Unit = {}) {
        repository.addToCart(cartItem) { success ->
            onComplete(success)
        }
    }

    /**
     * Removes an item entirely from the Firebase database.
     */
    fun removeFromCart(userId: String, productId: String, onComplete: (Boolean) -> Unit = {}) {
        repository.removeFromCart(userId, productId) { success ->
            onComplete(success)
        }
    }

    /**
     * Updates the quantity of a specific item (e.g., when clicking + or -).
     */
    fun updateQuantity(userId: String, productId: String, quantity: Int, onComplete: (Boolean) -> Unit = {}) {
        if (quantity < 1) return

        repository.updateQuantity(userId, productId, quantity) { success ->
            onComplete(success)
        }
    }
}
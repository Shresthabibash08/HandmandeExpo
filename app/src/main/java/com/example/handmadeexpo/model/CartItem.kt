package com.example.handmadeexpo.model

class CartItem {
    data class CartItem(
        val id: Int,
        val name: String,
        val price: Double,
        val image: Int,    // drawable resource
        var quantity: Int
    )

}
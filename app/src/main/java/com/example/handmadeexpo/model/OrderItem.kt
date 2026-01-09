package com.example.handmadeexpo.model

import java.io.Serializable

data class OrderItem(
    val productId: String = "",
    val productName: String = "",
    val price: Double = 0.0,
    val quantity: Int = 1,
    val imageUrl: String = ""
) : Serializable
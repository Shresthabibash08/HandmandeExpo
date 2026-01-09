package com.example.handmadeexpo.model

data class OrderModel(
    val orderId: String = "",
    val userId: String = "",
    val customerName: String = "",
    val address: String = "",
    val phone: String = "",
    val items: List<OrderItem> = emptyList(),
    val totalPrice: Double = 0.0,
    val paymentMethod: String = "",
    val status: String = "Pending",
    val orderDate: String= ""
)


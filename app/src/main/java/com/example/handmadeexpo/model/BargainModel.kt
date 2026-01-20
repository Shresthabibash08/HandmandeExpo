package com.example.handmadeexpo.model

data class BargainModel(
    val productId: String = "",
    val buyerId: String = "",
    val buyerName: String = "",   // New field
    val sellerId: String = "",
    val sellerName: String = "",  // New field
    val productName: String = "",
    val originalPrice: String = "",
    val offeredPrice: String = "",
    val counterPrice: String = "", // New field for seller's counter offer
    val status: String = "Pending" // "Pending", "Accepted", "Rejected", "Counter"
)
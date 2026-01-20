package com.example.handmadeexpo.model

data class ProductModel(
    var productId: String = "",
    val sellerId: String = "",
    val name: String = "",
    val price: Double = 0.0,
    val stock: Int = 0,
    val sold: Int = 0,
    val image: String = "",
    val description: String = "",
    val categoryId: String = "",
    val totalRating: Int = 0,   // sum of all stars
    val ratingCount: Int = 0,   // number of users who rated
    val verificationStatus: String = "Pending", // NEW: Pending, Verified, Rejected
    val rejectionReason: String = "" // NEW: Admin's reason for rejection
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "productId" to productId,
            "name" to name,
            "price" to price,
            "description" to description,
            "categoryId" to categoryId,
            "image" to image,
            "stock" to stock,
            "sellerId" to sellerId,
            "sold" to sold,
            "totalRating" to totalRating,
            "ratingCount" to ratingCount,
            "verificationStatus" to verificationStatus,
            "rejectionReason" to rejectionReason
        )
    }
}
package com.example.handmadeexpo.model

data class ProductModel(
    var productId: String = "",
    var name: String = "",
    var price: Double = 0.0,
    var description: String = "",
    var categoryId: String = "",
    var image: String = "",
    var stock: Int = 0,
    var sellerId: String = "",
    var createdAt: Long = System.currentTimeMillis()
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
            "createdAt" to createdAt
        )
    }
}

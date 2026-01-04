package com.example.handmadeexpo.model

data class ProductModel(
    var productId: String = "",
    val sellerId: String = "",
    val name: String = "",
    val price: Double = 0.0,
    val stock: Int = 0,
    val sold: Int = 0,
    val image: String = "",
    val description:String="",
    val categoryId:String=""

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
        )
    }
}

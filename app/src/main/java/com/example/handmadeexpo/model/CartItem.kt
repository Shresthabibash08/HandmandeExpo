package com.example.handmadeexpo.model

data class CartItem(
    val productId: String = "",
    val userId: String = "",
    val name: String = "",
    val price: Double = 0.0,
    val image: String = "",
    var quantity: Int = 1
) {
    companion object {
        fun fromProduct(product: ProductModel, userId: String) = CartItem(
            productId = product.productId,
            userId = userId,
            name = product.name,
            price = product.price,
            image = product.image,
            quantity = 1
        )
    }
}
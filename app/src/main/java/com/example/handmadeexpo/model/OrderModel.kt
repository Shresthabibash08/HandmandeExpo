package com.example.handmadeexpo.model

data class OrderModel(
    var orderId: String = "",
    val customerName: String = "",
    val address: String = "",
    val phone: String = "",
    val items: List<OrderItem> = emptyList(),
    val totalPrice: Double = 0.0,
    val paymentMethod: String = "COD",
    val status: String = "Pending",
    val orderDate: String = "",
    val deliveryDate: String = "", // Preferred delivery date
    val buyerId: String = ""
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "orderId" to orderId,
            "customerName" to customerName,
            "address" to address,
            "phone" to phone,
            "items" to items.map { item ->
                mapOf(
                    "productId" to item.productId,
                    "productName" to item.productName,
                    "price" to item.price,
                    "quantity" to item.quantity,
                    "imageUrl" to item.imageUrl
                )
            },
            "totalPrice" to totalPrice,
            "paymentMethod" to paymentMethod,
            "status" to status,
            "orderDate" to orderDate,
            "deliveryDate" to deliveryDate,
            "buyerId" to buyerId
        )
    }
}
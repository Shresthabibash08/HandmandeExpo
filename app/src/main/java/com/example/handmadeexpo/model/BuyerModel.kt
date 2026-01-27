package com.example.handmadeexpo.model

data class BuyerModel(
    val buyerId: String = "",
    val buyerName: String = "",
    val buyerEmail: String = "",
    val buyerPhoneNumber: String = "",
    val buyerAddress: String = "",
    var role: String = "buyer",
    val banned: Boolean = false,
    // Added profile image field
    val profileImage: String = ""
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "buyerId" to buyerId,
            "buyerName" to buyerName,
            "buyerEmail" to buyerEmail,
            "buyerPhoneNumber" to buyerPhoneNumber,
            "buyerAddress" to buyerAddress,
            "role" to role,
            "banned" to banned,
            "profileImage" to profileImage
        )
    }
}
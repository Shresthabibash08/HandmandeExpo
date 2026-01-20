package com.example.handmadeexpo.model

data class BuyerModel(
    val buyerId: String = "",
    val buyerName: String = "",
    val buyerEmail: String = "",
    val buyerPhoneNumber: String = "",
    val buyerAddress: String = "",
    var role: String = "buyer",

    // --- ADD THIS FIELD ---
    val banned: Boolean = false
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "buyerId" to buyerId,
            "buyerName" to buyerName,
            "buyerEmail" to buyerEmail,
            "buyerPhoneNumber" to buyerPhoneNumber,
            "buyerAddress" to buyerAddress,
            "role" to role,

            // --- ADD THIS TO MAP ---
            "banned" to banned
        )
    }
}
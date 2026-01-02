package com.example.handmadeexpo.model

data class BuyerProfileModel(
    val buyerId: String = "",
    val fullName: String = "",
    val buyerEmail: String = "",
    val buyerPhoneNumber: String = "",
    val buyerAddress: String = ""
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "buyerId" to buyerId,
            "fullName" to fullName,
            "buyerEmail" to buyerEmail,
            "buyerPhoneNumber" to buyerPhoneNumber,
            "buyerAddress" to buyerAddress
        )
    }
}

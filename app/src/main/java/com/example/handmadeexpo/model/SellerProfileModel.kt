package com.example.handmadeexpo.model

data class SellerProfileModel(
    val sellerId: String = "",
    val shopName: String = "",
    val sellerEmail: String = "",
    val sellerPhoneNumber: String = "",
    val sellerAddress: String = "",
    val panNumber: String = ""
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "sellerId" to sellerId,
            "shopName" to shopName,
            "sellerEmail" to sellerEmail,
            "sellerPhoneNumber" to sellerPhoneNumber,
            "sellerAddress" to sellerAddress,
            "panNumber" to panNumber
        )
    }
}

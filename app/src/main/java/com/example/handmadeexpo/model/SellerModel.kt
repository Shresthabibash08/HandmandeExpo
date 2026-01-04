package com.example.handmadeexpo.model

data class SellerModel(
    val sellerId: String = "",
    val fullName: String = "",
    val shopName: String = "",
    val sellerEmail: String = "",
    val sellerPhoneNumber: String = "",
    val sellerAddress: String = "",
    val panNumber: String = "",
    val role: String = "seller",
    // --- VERIFICATION FIELDS ---
    val documentType: String = "",
    val documentUrl: String = "",
    val verificationStatus: String = "Unverified"
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "sellerId" to sellerId,
            "fullName" to fullName,
            "shopName" to shopName,
            "sellerEmail" to sellerEmail,
            "sellerPhoneNumber" to sellerPhoneNumber,
            "sellerAddress" to sellerAddress,
            "panNumber" to panNumber,
            "role" to role,
            "documentType" to documentType,
            "documentUrl" to documentUrl,
            "verificationStatus" to verificationStatus
        )
    }
}
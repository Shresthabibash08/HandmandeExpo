package com.example.handmadeexpo.repo

import com.example.handmadeexpo.model.BuyerModel
import com.example.handmadeexpo.model.ProductModel
import com.example.handmadeexpo.model.SellerModel

interface AdminRepo {
    fun getSellers(onResult: (List<SellerModel>) -> Unit)
    fun getBuyers(onResult: (List<BuyerModel>) -> Unit)
    fun deleteUser(id: String, role: String, onComplete: (Boolean) -> Unit)

    // NEW: Verification methods
    fun updateSellerVerification(
        sellerId: String,
        status: String,
        callback: (Boolean, String) -> Unit
    )

    fun updateProductVerification(
        productId: String,
        status: String,
        rejectionReason: String = "",
        callback: (Boolean, String) -> Unit
    )

    fun getPendingSellers(onResult: (List<SellerModel>) -> Unit)
    fun getVerifiedSellers(onResult: (List<SellerModel>) -> Unit)
    fun getRejectedSellers(onResult: (List<SellerModel>) -> Unit)

    fun getPendingProducts(onResult: (List<ProductModel>) -> Unit)
    fun getVerifiedProducts(onResult: (List<ProductModel>) -> Unit)
    fun getRejectedProducts(onResult: (List<ProductModel>) -> Unit)
}
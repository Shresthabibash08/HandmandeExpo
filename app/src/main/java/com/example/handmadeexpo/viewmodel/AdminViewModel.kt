package com.example.handmadeexpo.viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import com.example.handmadeexpo.model.*
import com.example.handmadeexpo.repo.*

class AdminViewModel : ViewModel() {
    private val adminRepo: AdminRepo = AdminImpl()
    private val productRepo: ProductRepo = ProductRepoImpl()

    val sellers = mutableStateListOf<SellerModel>()
    val buyers = mutableStateListOf<BuyerModel>()
    val products = mutableStateListOf<ProductModel>()

    // NEW: Filtered lists for verification
    val pendingSellers = mutableStateListOf<SellerModel>()
    val verifiedSellers = mutableStateListOf<SellerModel>()
    val rejectedSellers = mutableStateListOf<SellerModel>()

    val pendingProducts = mutableStateListOf<ProductModel>()
    val verifiedProducts = mutableStateListOf<ProductModel>()
    val rejectedProducts = mutableStateListOf<ProductModel>()

    var searchQuery by mutableStateOf("")
    var isLoading by mutableStateOf(true)

    init {
        fetchData()
        fetchVerificationData()
    }

    private fun fetchData() {
        adminRepo.getSellers { data -> sellers.clear(); sellers.addAll(data) }
        adminRepo.getBuyers { data -> buyers.clear(); buyers.addAll(data) }
        productRepo.getAllProduct { success, _, data ->
            if (success && data != null) {
                products.clear()
                products.addAll(data)
            }
            isLoading = false
        }
    }

    private fun fetchVerificationData() {
        // Sellers
        adminRepo.getPendingSellers { data ->
            pendingSellers.clear()
            pendingSellers.addAll(data)
        }
        adminRepo.getVerifiedSellers { data ->
            verifiedSellers.clear()
            verifiedSellers.addAll(data)
        }
        adminRepo.getRejectedSellers { data ->
            rejectedSellers.clear()
            rejectedSellers.addAll(data)
        }

        // Products
        adminRepo.getPendingProducts { data ->
            pendingProducts.clear()
            pendingProducts.addAll(data)
        }
        adminRepo.getVerifiedProducts { data ->
            verifiedProducts.clear()
            verifiedProducts.addAll(data)
        }
        adminRepo.getRejectedProducts { data ->
            rejectedProducts.clear()
            rejectedProducts.addAll(data)
        }
    }

    fun deleteProduct(id: String) {
        productRepo.deleteProduct(id) { _, _ -> }
    }

    fun deleteUser(id: String, role: String) {
        adminRepo.deleteUser(id, role) { }
    }

    // NEW: Verification methods
    fun verifyOrRejectSeller(
        sellerId: String,
        status: String, // "Verified" or "Rejected"
        callback: (Boolean, String) -> Unit
    ) {
        adminRepo.updateSellerVerification(sellerId, status, callback)
    }

    fun verifyOrRejectProduct(
        productId: String,
        status: String, // "Verified" or "Rejected"
        rejectionReason: String = "",
        callback: (Boolean, String) -> Unit
    ) {
        adminRepo.updateProductVerification(productId, status, rejectionReason, callback)
    }
}
package com.example.handmadeexpo.viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import com.example.handmadeexpo.model.*
import com.example.handmadeexpo.repo.*
import com.google.firebase.database.FirebaseDatabase

class AdminViewModel : ViewModel() {
    private val adminRepo: AdminRepo = AdminImpl()
    private val productRepo: ProductRepo = ProductRepoImpl()

    // --- State Lists ---
    val sellers = mutableStateListOf<SellerModel>()
    val buyers = mutableStateListOf<BuyerModel>()
    val products = mutableStateListOf<ProductModel>()

    // Filtered lists for verification management
    val pendingSellers = mutableStateListOf<SellerModel>()
    val verifiedSellers = mutableStateListOf<SellerModel>()
    val rejectedSellers = mutableStateListOf<SellerModel>()

    val pendingProducts = mutableStateListOf<ProductModel>()
    val verifiedProducts = mutableStateListOf<ProductModel>()
    val rejectedProducts = mutableStateListOf<ProductModel>()

    var searchQuery by mutableStateOf("")
    var isLoading by mutableStateOf(true)

    init {
        refreshAllData()
    }

    /**
     * Refreshes both general user data and specific verification categorized data
     */
    fun refreshAllData() {
        fetchData()
        fetchVerificationData()
    }

    private fun fetchData() {
        isLoading = true
        // Fetch All Sellers
        adminRepo.getSellers { data ->
            sellers.clear()
            sellers.addAll(data)
        }
        // Fetch All Buyers
        adminRepo.getBuyers { data ->
            buyers.clear()
            buyers.addAll(data)
        }
        // Fetch All Products
        productRepo.getAllProduct { success, _, data ->
            if (success && data != null) {
                products.clear()
                products.addAll(data)
            }
            isLoading = false
        }
    }

    private fun fetchVerificationData() {
        // Categorize Sellers by Status
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

        // Categorize Products by Status
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

    // --- Management Actions ---

    fun deleteProduct(id: String) {
        productRepo.deleteProduct(id) { _, _ -> refreshAllData() }
    }

    fun deleteUser(id: String, role: String) {
        adminRepo.deleteUser(id, role) { refreshAllData() }
    }

    fun unbanUser(id: String, role: String) {
        val db = FirebaseDatabase.getInstance()
        val ref = if (role == "seller") db.getReference("Seller") else db.getReference("Buyer")

        ref.child(id).child("banned").removeValue()
            .addOnSuccessListener {
                refreshAllData() 
            }
    }

    // --- Verification Logic ---

    fun verifyOrRejectSeller(
        sellerId: String,
        status: String, // "Verified" or "Rejected"
        callback: (Boolean, String) -> Unit
    ) {
        adminRepo.updateSellerVerification(sellerId, status) { success, msg ->
            if (success) fetchVerificationData() // Update verification tabs immediately
            callback(success, msg)
        }
    }

    fun verifyOrRejectProduct(
        productId: String,
        status: String, // "Verified" or "Rejected"
        rejectionReason: String = "",
        callback: (Boolean, String) -> Unit
    ) {
        adminRepo.updateProductVerification(productId, status, rejectionReason) { success, msg ->
            if (success) fetchVerificationData() // Update verification tabs immediately
            callback(success, msg)
        }
    }
}
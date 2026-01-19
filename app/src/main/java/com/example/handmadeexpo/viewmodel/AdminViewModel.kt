package com.example.handmadeexpo.viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import com.example.handmadeexpo.model.*
import com.example.handmadeexpo.repo.*
import com.google.firebase.database.FirebaseDatabase

class AdminViewModel : ViewModel() {
    private val adminRepo: AdminRepo = AdminImpl()
    private val productRepo: ProductRepo = ProductRepoImpl()

    // State lists
    val sellers = mutableStateListOf<SellerModel>()
    val buyers = mutableStateListOf<BuyerModel>()
    val products = mutableStateListOf<ProductModel>()

    var isLoading by mutableStateOf(true)

    init { fetchData() }

    fun fetchData() {
        isLoading = true
        // Fetch Sellers
        adminRepo.getSellers { data ->
            sellers.clear()
            sellers.addAll(data)
        }
        // Fetch Buyers
        adminRepo.getBuyers { data ->
            buyers.clear()
            buyers.addAll(data)
        }
        // Fetch Products
        productRepo.getAllProduct { success, _, data ->
            if (success && data != null) {
                products.clear()
                products.addAll(data)
            }
            isLoading = false
        }
    }

    fun deleteProduct(id: String) {
        productRepo.deleteProduct(id) { _, _ -> fetchData() }
    }

    fun deleteUser(id: String, role: String) {
        adminRepo.deleteUser(id, role) { fetchData() }
    }

    // --- UNBAN USER FUNCTION ---
    fun unbanUser(id: String, role: String) {
        val db = FirebaseDatabase.getInstance()
        // Determine correct Firebase node based on role
        val ref = if (role == "seller") db.getReference("Seller") else db.getReference("Buyer")

        // Remove the 'banned' field to unban
        ref.child(id).child("banned").removeValue()
            .addOnSuccessListener {
                fetchData() // Refresh list immediately to update UI
            }
    }
}
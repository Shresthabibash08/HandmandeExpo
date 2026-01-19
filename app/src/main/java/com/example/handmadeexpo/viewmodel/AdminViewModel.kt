package com.example.handmadeexpo.viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import com.example.handmadeexpo.model.*
import com.example.handmadeexpo.repo.*
import com.google.firebase.database.FirebaseDatabase

class AdminViewModel : ViewModel() {
    private val adminRepo: AdminRepo = AdminImpl()
    private val productRepo: ProductRepo = ProductRepoImpl()

    // Your existing lists
    val sellers = mutableStateListOf<SellerModel>()
    val buyers = mutableStateListOf<BuyerModel>()
    val products = mutableStateListOf<ProductModel>()

    var searchQuery by mutableStateOf("")
    var isLoading by mutableStateOf(true)

    init { fetchData() }

    fun fetchData() {
        isLoading = true
        // Assuming your Repo returns a list via callback
        adminRepo.getSellers { data ->
            sellers.clear()
            sellers.addAll(data)
            // If buyers load fast, we can set loading false here or wait for both
        }
        adminRepo.getBuyers { data ->
            buyers.clear()
            buyers.addAll(data)
        }
        productRepo.getAllProduct { success, _, data ->
            if (success && data != null) {
                products.clear()
                products.addAll(data)
            }
            isLoading = false
        }
    }

    fun deleteProduct(id: String) {
        productRepo.deleteProduct(id) { _, _ ->
            fetchData() // Refresh list after delete
        }
    }

    fun deleteUser(id: String, role: String) {
        adminRepo.deleteUser(id, role) {
            fetchData() // Refresh list after delete
        }
    }

    // --- NEW: Unban Function ---
    fun unbanUser(id: String, role: String) {
        val db = FirebaseDatabase.getInstance()
        val ref = if (role == "seller") db.getReference("Seller") else db.getReference("Buyer")

        // Removing the 'banned' flag effectively unbans them
        ref.child(id).child("banned").removeValue()
            .addOnSuccessListener {
                fetchData() // REFRESH the list so they move from "Banned" tab to "Active"
            }
    }
}
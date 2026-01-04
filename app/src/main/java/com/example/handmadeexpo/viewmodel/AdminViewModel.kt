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

    var searchQuery by mutableStateOf("")
    var isLoading by mutableStateOf(true)

    init { fetchData() }

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

    fun deleteProduct(id: String) {
        productRepo.deleteProduct(id) { _, _ -> }
    }

    fun deleteUser(id: String, role: String) {
        adminRepo.deleteUser(id, role) { }
    }
}
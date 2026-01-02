package com.example.handmadeexpo.viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import com.example.handmadeexpo.model.BuyerModel
import com.example.handmadeexpo.model.SellerModel
import com.example.handmadeexpo.repo.AdminRepo
import com.example.handmadeexpo.repo.AdminImpl

class AdminViewModel : ViewModel() {
    private val repository: AdminRepo = AdminImpl()

    val sellers = mutableStateListOf<SellerModel>()
    val buyers = mutableStateListOf<BuyerModel>()

    var searchQuery by mutableStateOf("")

    // Loading States
    var isSellersLoading by mutableStateOf(true)
    var isBuyersLoading by mutableStateOf(true)

    init {
        fetchData()
    }

    private fun fetchData() {
        repository.getSellers { data ->
            sellers.clear()
            sellers.addAll(data)
            isSellersLoading = false
        }
        repository.getBuyers { data ->
            buyers.clear()
            buyers.addAll(data)
            isBuyersLoading = false
        }
    }

    fun deleteUser(id: String, role: String) {
        repository.deleteUser(id, role) { }
    }
}
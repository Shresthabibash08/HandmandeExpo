package com.example.handmadeexpo.viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import com.example.handmadeexpo.model.BuyerModel
import com.example.handmadeexpo.model.SellerModel
import com.example.handmadeexpo.repository.AdminRepo
import com.example.handmadeexpo.repository.AdminImpl

class AdminViewModel : ViewModel() {
    private val repository: AdminRepo = AdminImpl()

    // Observable lists for the UI
    val sellers = mutableStateListOf<SellerModel>()
    val buyers = mutableStateListOf<BuyerModel>()

    // Shared search query across Home and User List
    var searchQuery by mutableStateOf("")
    val isLoading = mutableStateOf(true)

    init {
        fetchData()
    }

    private fun fetchData() {
        repository.getSellers { data ->
            sellers.clear()
            sellers.addAll(data)
            isLoading.value = false
        }
        repository.getBuyers { data ->
            buyers.clear()
            buyers.addAll(data)
            isLoading.value = false
        }
    }

    fun deleteUser(id: String, role: String) {
        repository.deleteUser(id, role) { /* Result handled by real-time listeners */ }
    }
}
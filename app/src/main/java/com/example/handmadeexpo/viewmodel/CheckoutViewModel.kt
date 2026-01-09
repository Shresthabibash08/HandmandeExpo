package com.example.handmadeexpo.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.handmadeexpo.model.OrderModel
import com.example.handmadeexpo.repo.BuyerRepo
import com.example.handmadeexpo.repo.BuyerRepoImpl
import com.example.handmadeexpo.repo.OrderRepo
import com.example.handmadeexpo.repo.OrderRepoImpl
import com.google.firebase.auth.FirebaseAuth

class CheckoutViewModel : ViewModel() {

    private val orderRepo: OrderRepo = OrderRepoImpl()
    private val buyerRepo: BuyerRepo = BuyerRepoImpl()

    // --- STATE ---
    var currentUserName = mutableStateOf("")
    var isLoadingUser = mutableStateOf(true)

    fun fetchUserInfo() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            // Ask Repo for details
            buyerRepo.getBuyerDetailsById(currentUser.uid) { success, message, buyer ->
                if (success && buyer != null) {
                    // STRICTLY get name from Database.
                    // If empty, we do NOT use email.
                    val name = buyer.buyerName

                    if (!name.isNullOrEmpty()) {
                        currentUserName.value = name
                    } else {
                        currentUserName.value = "Name not set"
                    }
                } else {
                    currentUserName.value = "Guest"
                }
                isLoadingUser.value = false
            }
        } else {
            isLoadingUser.value = false
        }
    }

    fun placeOrder(order: OrderModel, callback: (Boolean, String) -> Unit) {
        orderRepo.placeOrder(order, callback)
    }
}
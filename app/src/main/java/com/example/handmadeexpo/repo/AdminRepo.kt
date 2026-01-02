package com.example.handmadeexpo.repo

import com.example.handmadeexpo.model.BuyerModel
import com.example.handmadeexpo.model.SellerModel

interface AdminRepo {
    fun getSellers(onResult: (List<SellerModel>) -> Unit)
    fun getBuyers(onResult: (List<BuyerModel>) -> Unit)
    fun deleteUser(id: String, role: String, onComplete: (Boolean) -> Unit)
}
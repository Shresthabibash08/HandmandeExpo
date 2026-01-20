package com.example.handmadeexpo.repo

import com.example.handmadeexpo.model.BargainModel

interface BargainRepo {
    fun sendBargainRequest(bargain: BargainModel, callback: (Boolean) -> Unit)

    fun updateBargainStatus(
        buyerId: String,
        productId: String,
        status: String,
        counterPrice: String = "",
        sellerName: String = "", // Added to ensure names are synced
        callback: (Boolean) -> Unit
    )
}
package com.example.handmadeexpo.repo

import com.example.handmadeexpo.model.BuyerProfileModel

interface BuyerProfileRepo {

    fun getBuyerProfileById(
        buyerId: String,
        callback: (Boolean, String, BuyerProfileModel?) -> Unit
    )

    fun updateBuyerProfile(
        buyerId: String,
        model: BuyerProfileModel,
        callback: (Boolean, String) -> Unit
    )
}

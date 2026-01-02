package com.example.handmadeexpo.repo

import com.example.handmadeexpo.model.SellerProfileModel

interface SellerProfileRepo {

    fun getSellerProfileById(
        sellerId: String,
        callback: (Boolean, String, SellerProfileModel?) -> Unit
    )

    fun updateSellerProfile(
        sellerId: String,
        model: SellerProfileModel,
        callback: (Boolean, String) -> Unit
    )
}

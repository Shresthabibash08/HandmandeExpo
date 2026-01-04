package com.example.handmadeexpo.repo

import android.content.Context
import android.net.Uri
import com.example.handmadeexpo.model.BuyerModel
import com.example.handmadeexpo.model.SellerModel
import com.google.firebase.auth.FirebaseUser

interface SellerRepo {

    fun login(
        email: String,
        password: String,
        callback: (Boolean, String) -> Unit
    )

    fun forgotPassword(
        email: String,
        callback: (Boolean, String) -> Unit
    )

    fun register(
        email: String,
        password: String,
        callback: (Boolean, String, String) -> Unit
    )

    fun logout(
        callback: (Boolean, String) -> Unit
    )

    fun updateProfile(
        sellerId: String,
        model: SellerModel,
        callback: (Boolean, String) -> Unit
    )

    fun deleteAccount(
        sellerId: String,
        callback: (Boolean, String) -> Unit
    )

    fun getSellerDetailsById(
        sellerId: String,
        callback: (Boolean, String, SellerModel?) -> Unit
    )

    fun addSellerToDatabase(
        sellerId: String,
        sellerModel: SellerModel,
        callback: (Boolean, String) -> Unit
    )

    fun getCurrentUser (): FirebaseUser?

    fun uploadImage(context: Context, imageUri: Uri, callback: (Boolean, String) -> Unit)
}

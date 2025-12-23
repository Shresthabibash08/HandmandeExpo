package com.example.handmadeexpo.repo

import com.example.handmadeexpo.model.BuyerModel
import com.google.firebase.auth.FirebaseUser

interface BuyerRepo {
    fun login(
        email: String,
        password: String,
        callback: (Boolean, String) -> Unit
    )


    fun forgotPassword(email:String,callback:(Boolean,String)->Unit)

    fun register(email:String,password:String,callback:(Boolean,String,String)->Unit)

    fun logout(callback:(Boolean,String)->Unit)

    fun updateProfile(model: BuyerModel,callback:(Boolean,String)->Unit)

    fun deleteAccount(buyerId:String,callback:(Boolean,String)->Unit)

    fun getBuyerDetailsById(buyerId:String,callback:(Boolean,String,BuyerModel?)->Unit)

    fun addBuyerToDatabase(buyerId: String,buyerModel:BuyerModel,callback:(Boolean,String)->Unit)

    fun getCurrentUser (): FirebaseUser?


}
package com.example.handmadeexpo.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.handmadeexpo.model.BuyerModel
import com.example.handmadeexpo.repo.SellerRepo
import com.google.firebase.auth.FirebaseUser

class SellerViewModel (val repo: SellerRepo): ViewModel() {
    fun login(
        email: String,
        password: String,
        callback: (Boolean, String) -> Unit
    ) {
        repo.login(email, password, callback)
    }


    fun forgotPassword(email:String,callback:(Boolean,String)->Unit){
        repo.forgotPassword(email,callback)
    }

    fun register(email:String,password:String,callback:(Boolean,String,String)->Unit){
        repo.register(email,password,callback)
    }

    fun logout(callback:(Boolean,String)->Unit){
        repo.logout(callback)
    }

    fun updateProfile(model: BuyerModel,callback:(Boolean,String)->Unit){
        repo.updateProfile(model,callback)
    }

    fun deleteAccount(buyerId:String,callback:(Boolean,String)->Unit){
        repo.deleteAccount(buyerId,callback)
    }
    private val _buyer= MutableLiveData<BuyerModel?>()
    val buyer: MutableLiveData<BuyerModel?>
        get()= _buyer

    private val _loading=MutableLiveData<Boolean>()
    val loading: MutableLiveData<Boolean>
        get() = loading

    fun getBuyerDetailsById(buyerId:String,callback:(Boolean,String,BuyerModel?)->Unit){
        _loading.postValue(true)
        repo.getBuyerDetailsById(buyerId){ success,msg,data ->
            if(success){
                _loading.postValue(false)
                _buyer.postValue(data)
            }
            else{
                _loading.postValue(false)
                _buyer.postValue(null)
            }
        }
    }

    fun addBuyerToDatabase(buyerId: String,buyerModel:BuyerModel,callback:(Boolean,String)->Unit){
        repo.addBuyerToDatabase(buyerId,buyerModel,callback)
    }

    fun getCurrentUser (): FirebaseUser?{
        return repo.getCurrentUser()
    }
}
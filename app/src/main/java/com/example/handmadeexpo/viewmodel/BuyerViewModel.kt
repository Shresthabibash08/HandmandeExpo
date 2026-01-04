package com.example.handmadeexpo.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.handmadeexpo.model.BuyerModel
import com.example.handmadeexpo.repo.BuyerRepo
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class BuyerViewModel (val repo: BuyerRepo): ViewModel() {
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

    fun updateProfile(buyerId:String,model: BuyerModel,callback:(Boolean,String)->Unit){
        repo.updateProfile(buyerId,model,callback)
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

    fun getBuyerDetailsById(buyerId:String){
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

    fun checkUserRole(
        userId: String,
        callback: (String?) -> Unit
    ){
        repo.getUserRole(userId, callback)
    }


}
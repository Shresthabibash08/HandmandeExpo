package com.example.handmadeexpo.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.handmadeexpo.model.SellerModel
import com.example.handmadeexpo.repo.SellerRepo
import com.google.firebase.auth.FirebaseUser

class SellerViewModel(
    private val repo: SellerRepo
) : ViewModel() {

    fun login(
        email: String,
        password: String,
        callback: (Boolean, String) -> Unit
    ) {
        repo.login(email, password, callback)
    }

    fun forgotPassword(
        email: String,
        callback: (Boolean, String) -> Unit
    ) {
        repo.forgotPassword(email, callback)
    }

    fun register(
        email: String,
        password: String,
        callback: (Boolean, String, String) -> Unit
    ) {
        repo.register(email, password, callback)
    }

    fun logout(callback: (Boolean, String) -> Unit) {
        repo.logout(callback)
    }

    fun getCurrentUser(): FirebaseUser? {
        return repo.getCurrentUser()
    }

    private val _seller = MutableLiveData<SellerModel?>()
    val seller: MutableLiveData<SellerModel?>
        get() = _seller

    private val _loading = MutableLiveData<Boolean>()
    val loading: MutableLiveData<Boolean>
        get() = _loading

    fun getSellerDetailsById(sellerId: String) {
        _loading.postValue(true)
        repo.getSellerDetailsById(sellerId) { success, _, data ->
            _loading.postValue(false)
            _seller.postValue(if (success) data else null)
        }
    }

    fun updateProfile(
        sellerId: String,
        model: SellerModel,
        callback: (Boolean, String) -> Unit
    ) {
        repo.updateProfile(sellerId, model, callback)
    }

    fun deleteAccount(
        sellerId: String,
        callback: (Boolean, String) -> Unit
    ) {
        repo.deleteAccount(sellerId, callback)
    }

    fun addSellerToDatabase(
        sellerId: String,
        sellerModel: SellerModel,
        callback: (Boolean, String) -> Unit
    ) {
        repo.addSellerToDatabase(sellerId, sellerModel, callback)
    }
}

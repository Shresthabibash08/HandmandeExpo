package com.example.handmadeexpo.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.handmadeexpo.model.SellerModel
import com.example.handmadeexpo.repo.SellerRepo
import com.google.firebase.auth.FirebaseUser

class SellerViewModel(val repo: SellerRepo) : ViewModel() {

    // --- LIVE DATA ---
    private val _seller = MutableLiveData<SellerModel?>()
    val seller: MutableLiveData<SellerModel?>
        get() = _seller

    private val _loading = MutableLiveData<Boolean>()
    val loading: MutableLiveData<Boolean>
        get() = _loading // FIXED: Changed from 'loading' to '_loading' to stop the crash

    // --- AUTH METHODS ---
    fun login(email: String, password: String, callback: (Boolean, String) -> Unit) {
        repo.login(email, password, callback)
    }

    fun forgotPassword(email: String, callback: (Boolean, String) -> Unit) {
        repo.forgotPassword(email, callback)
    }

    fun register(email: String, password: String, callback: (Boolean, String, String) -> Unit) {
        repo.register(email, password, callback)
    }

    fun logout(callback: (Boolean, String) -> Unit) {
        repo.logout(callback)
    }

    fun updateProfile(sellerId: String, model: SellerModel, callback: (Boolean, String) -> Unit) {
        repo.updateProfile(sellerId, model, callback)
    }

    fun deleteAccount(sellerId: String, callback: (Boolean, String) -> Unit) {
        repo.deleteAccount(sellerId, callback)
    }

    // --- DATA FETCHING ---
    fun getSellerDetailsById(sellerId: String) {
        _loading.postValue(true)
        repo.getSellerDetailsById(sellerId) { success, msg, data ->
            if (success) {
                _loading.postValue(false)
                _seller.postValue(data)
            } else {
                _loading.postValue(false)
                _seller.postValue(null)
            }
        }
    }

    fun addSellerToDatabase(sellerId: String, sellerModel: SellerModel, callback: (Boolean, String) -> Unit) {
        repo.addSellerToDatabase(sellerId, sellerModel, callback)
    }

    fun getCurrentUser(): FirebaseUser? {
        return repo.getCurrentUser()
    }

    fun uploadImage(context: Context, imageUri: Uri, callback: (Boolean, String) -> Unit) {
        repo.uploadImage(context, imageUri, callback)
    }

    fun updateProfileFields(sellerId: String, updates: Map<String, Any>, callback: (Boolean, String) -> Unit) {
        repo.updateProfileFields(sellerId, updates, callback)
    }
}

// --- FACTORY CLASS (Required to prevent crash during initialization) ---
class SellerViewModelFactory(private val repo: SellerRepo) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SellerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SellerViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
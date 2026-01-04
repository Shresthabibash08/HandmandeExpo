package com.example.handmadeexpo.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.handmadeexpo.model.SellerModel
import com.example.handmadeexpo.repo.SellerRepo
import com.google.firebase.auth.FirebaseUser

class SellerViewModel(private val repo: SellerRepo) : ViewModel() {

    // --- LIVE DATA ---
    private val _seller = MutableLiveData<SellerModel?>()
    val seller: LiveData<SellerModel?>
        get() = _seller

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean>
        get() = _loading // FIXED: Returns _loading to prevent StackOverflow crash

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

    fun getCurrentUser(): FirebaseUser? {
        return repo.getCurrentUser()
    }

    // --- DATA FETCHING & DATABASE ---
    fun getSellerDetailsById(sellerId: String) {
        _loading.postValue(true)
        repo.getSellerDetailsById(sellerId) { success, msg, data ->
            _loading.postValue(false)
            if (success) {
                _seller.postValue(data)
            } else {
                _seller.postValue(null)
            }
        }
    }

    fun addSellerToDatabase(
        sellerId: String,
        sellerModel: SellerModel,
        callback: (Boolean, String) -> Unit
    ) {
        repo.addSellerToDatabase(sellerId, sellerModel, callback)
    }

    // --- PROFILE & MEDIA MANAGEMENT ---
    fun updateProfile(sellerId: String, model: SellerModel, callback: (Boolean, String) -> Unit) {
        repo.updateProfile(sellerId, model, callback)
    }

    fun updateProfileFields(
        sellerId: String,
        updates: Map<String, Any>,
        callback: (Boolean, String) -> Unit
    ) {
        repo.updateProfileFields(sellerId, updates, callback)
    }

    fun uploadImage(context: Context, imageUri: Uri, callback: (Boolean, String) -> Unit) {
        repo.uploadImage(context, imageUri, callback)
    }

    fun deleteAccount(sellerId: String, callback: (Boolean, String) -> Unit) {
        repo.deleteAccount(sellerId, callback)
    }
}

// --- FACTORY CLASS ---
class SellerViewModelFactory(private val repo: SellerRepo) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SellerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SellerViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
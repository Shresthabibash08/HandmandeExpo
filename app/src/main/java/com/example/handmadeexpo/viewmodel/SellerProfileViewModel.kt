package com.example.handmadeexpo.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.handmadeexpo.model.SellerProfileModel
import com.example.handmadeexpo.repo.SellerProfileRepo

class SellerProfileViewModel(
    private val repo: SellerProfileRepo
) : ViewModel() {

    private val _sellerProfile = MutableLiveData<SellerProfileModel?>()
    val sellerProfile: MutableLiveData<SellerProfileModel?>
        get() = _sellerProfile

    private val _loading = MutableLiveData<Boolean>()
    val loading: MutableLiveData<Boolean>
        get() = _loading

    fun getSellerProfileById(sellerId: String) {
        _loading.postValue(true)

        repo.getSellerProfileById(sellerId) { success, _, data ->
            _loading.postValue(false)
            _sellerProfile.postValue(if (success) data else null)
        }
    }

    fun updateSellerProfile(
        sellerId: String,
        model: SellerProfileModel,
        callback: (Boolean, String) -> Unit
    ) {
        repo.updateSellerProfile(sellerId, model, callback)
    }
}

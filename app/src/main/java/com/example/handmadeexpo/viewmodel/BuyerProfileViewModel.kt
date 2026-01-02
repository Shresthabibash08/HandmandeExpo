package com.example.handmadeexpo.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.handmadeexpo.model.BuyerProfileModel
import com.example.handmadeexpo.repo.BuyerProfileRepo

class BuyerProfileViewModel(
    private val repo: BuyerProfileRepo
) : ViewModel() {

    private val _buyerProfile = MutableLiveData<BuyerProfileModel?>()
    val buyerProfile: MutableLiveData<BuyerProfileModel?>
        get() = _buyerProfile

    private val _loading = MutableLiveData<Boolean>()
    val loading: MutableLiveData<Boolean>
        get() = _loading

    fun getBuyerProfileById(buyerId: String) {
        _loading.postValue(true)
        repo.getBuyerProfileById(buyerId) { success, _, data ->
            _loading.postValue(false)
            _buyerProfile.postValue(if (success) data else null)
        }
    }

    fun updateBuyerProfile(
        buyerId: String,
        model: BuyerProfileModel,
        callback: (Boolean, String) -> Unit
    ) {
        repo.updateBuyerProfile(buyerId, model, callback)
    }
}

package com.example.handmadeexpo.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.handmadeexpo.model.BuyerModel
import com.example.handmadeexpo.repo.BuyerRepo
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*

class BuyerViewModel(
    private val repo: BuyerRepo
) : ViewModel() {

    // --- Auth Operations ---
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

    // --- State Management ---
    private val _buyer = MutableLiveData<BuyerModel?>()
    val buyer: MutableLiveData<BuyerModel?> get() = _buyer

    private val _loading = MutableLiveData<Boolean>()
    val loading: MutableLiveData<Boolean> get() = _loading

    // --- Profile Operations ---
    fun getBuyerDetailsById(buyerId: String) {
        _loading.postValue(true)
        repo.getBuyerDetailsById(buyerId) { success, _, data ->
            _loading.postValue(false)
            _buyer.postValue(if (success) data else null)
        }
    }

    fun updateProfile(buyerId: String, model: BuyerModel, callback: (Boolean, String) -> Unit) {
        repo.updateProfile(buyerId, model, callback)
    }

    fun deleteAccount(buyerId: String, callback: (Boolean, String) -> Unit) {
        repo.deleteAccount(buyerId, callback)
    }

    fun addBuyerToDatabase(buyerId: String, buyerModel: BuyerModel, callback: (Boolean, String) -> Unit) {
        repo.addBuyerToDatabase(buyerId, buyerModel, callback)
    }

    // --- Role Check Logic (Moved inside the class) ---
    fun checkUserRole(userId: String, callback: (String?) -> Unit) {
        val db = FirebaseDatabase.getInstance()

        db.getReference("Buyer").child(userId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        callback("buyer")
                    } else {
                        // Check Seller node if not found in Buyer
                        db.getReference("Seller").child(userId)
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    if (snapshot.exists()) {
                                        callback("seller")
                                    } else {
                                        callback(null)
                                    }
                                }
                                override fun onCancelled(error: DatabaseError) {
                                    callback(null)
                                }
                            })
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    callback(null)
                }
            })
    }
    fun changePassword(
        currentPassword: String,
        newPassword: String,
        callback: (Boolean, String) -> Unit
    ) {
        repo.changePassword(currentPassword, newPassword, callback)
    }
}
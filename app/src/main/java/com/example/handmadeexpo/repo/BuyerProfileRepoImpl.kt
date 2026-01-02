package com.example.handmadeexpo.repo

import com.example.handmadeexpo.model.BuyerProfileModel
import com.google.firebase.database.*

class BuyerProfileRepoImpl : BuyerProfileRepo {

    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val ref: DatabaseReference = database.getReference("Buyer")

    override fun getBuyerProfileById(
        buyerId: String,
        callback: (Boolean, String, BuyerProfileModel?) -> Unit
    ) {
        ref.child(buyerId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val buyerProfile = snapshot.getValue(BuyerProfileModel::class.java)
                        callback(true, "Buyer profile fetched successfully", buyerProfile)
                    } else {
                        callback(false, "Buyer profile not found", null)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(false, error.message, null)
                }
            })
    }

    override fun updateBuyerProfile(
        buyerId: String,
        model: BuyerProfileModel,
        callback: (Boolean, String) -> Unit
    ) {
        ref.child(buyerId)
            .updateChildren(model.toMap())
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    callback(true, "Buyer profile updated successfully")
                } else {
                    callback(false, it.exception?.message ?: "Update failed")
                }
            }
    }
}

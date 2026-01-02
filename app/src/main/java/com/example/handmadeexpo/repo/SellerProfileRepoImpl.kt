package com.example.handmadeexpo.repo

import com.example.handmadeexpo.model.SellerProfileModel
import com.google.firebase.database.*

class SellerProfileRepoImpl : SellerProfileRepo {

    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val ref: DatabaseReference = database.getReference("Seller")

    override fun getSellerProfileById(
        sellerId: String,
        callback: (Boolean, String, SellerProfileModel?) -> Unit
    ) {
        ref.child(sellerId)
            .addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val sellerProfile =
                            snapshot.getValue(SellerProfileModel::class.java)
                        callback(
                            true,
                            "Seller profile fetched successfully",
                            sellerProfile
                        )
                    } else {
                        callback(false, "Seller profile not found", null)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(false, error.message, null)
                }
            })
    }

    override fun updateSellerProfile(
        sellerId: String,
        model: SellerProfileModel,
        callback: (Boolean, String) -> Unit
    ) {
        ref.child(sellerId)
            .updateChildren(model.toMap())
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    callback(true, "Seller profile updated successfully")
                } else {
                    callback(false, it.exception?.message ?: "Update failed")
                }
            }
    }
}

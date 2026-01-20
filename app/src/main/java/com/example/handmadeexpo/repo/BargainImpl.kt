package com.example.handmadeexpo.repo

import com.example.handmadeexpo.model.BargainModel
import com.google.firebase.database.FirebaseDatabase

class BargainImpl : BargainRepo {
    private val db = FirebaseDatabase.getInstance().getReference("Bargains")

    override fun sendBargainRequest(bargain: BargainModel, callback: (Boolean) -> Unit) {
        // Saves the bargain under Bargains -> BuyerID -> ProductID
        db.child(bargain.buyerId).child(bargain.productId).setValue(bargain)
            .addOnCompleteListener { callback(it.isSuccessful) }
    }

    override fun updateBargainStatus(
        buyerId: String,
        productId: String,
        status: String,
        counterPrice: String,
        sellerName: String,
        callback: (Boolean) -> Unit
    ) {
        val updates = mutableMapOf<String, Any>("status" to status)

        // Include counterPrice if the status is "Counter"
        if (status == "Counter" && counterPrice.isNotEmpty()) {
            updates["counterPrice"] = counterPrice
        }

        // Include sellerName so the buyer knows who responded
        if (sellerName.isNotEmpty()) {
            updates["sellerName"] = sellerName
        }

        db.child(buyerId).child(productId).updateChildren(updates)
            .addOnCompleteListener { callback(it.isSuccessful) }
    }
}
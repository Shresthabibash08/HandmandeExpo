package com.example.handmadeexpo.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.handmadeexpo.model.BargainModel
import com.example.handmadeexpo.repo.BargainImpl
import com.example.handmadeexpo.repo.BargainRepo
import com.google.firebase.database.*

class BargainViewModel : ViewModel() {
    private val repository: BargainRepo = BargainImpl()

    val sellerBargains = mutableStateListOf<BargainModel>()

    fun fetchBargainsForSeller(sellerId: String) {
        val dbRef = FirebaseDatabase.getInstance().getReference("Bargains")
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                sellerBargains.clear()
                snapshot.children.forEach { buyerNode ->
                    buyerNode.children.forEach { productNode ->
                        val bargain = productNode.getValue(BargainModel::class.java)
                        if (bargain?.sellerId == sellerId &&
                            (bargain.status == "Pending" || bargain.status == "Counter")) {
                            sellerBargains.add(bargain)
                        }
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun fetchBargainsForBuyer(buyerId: String) {
        val dbRef = FirebaseDatabase.getInstance().getReference("Bargains").child(buyerId)
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                sellerBargains.clear()
                snapshot.children.forEach { productNode ->
                    val bargain = productNode.getValue(BargainModel::class.java)
                    if (bargain != null) {
                        sellerBargains.add(bargain)
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun requestBargain(bargain: BargainModel) {
        repository.sendBargainRequest(bargain) { _ -> }
    }

    // UPDATED: Logic to handle Buyer accepting the Seller's price
    fun updateStatus(
        buyerId: String,
        productId: String,
        status: String,
        priceToSet: String = "",
        actorRole: String = "" // "Buyer" or "Seller"
    ) {
        if (actorRole == "Buyer" && status == "Accepted") {
            // Directly update Firebase so offeredPrice = counterPrice
            val dbRef = FirebaseDatabase.getInstance().getReference("Bargains")
                .child(buyerId)
                .child(productId)

            val updates = mapOf(
                "status" to "Accepted",
                "offeredPrice" to priceToSet
            )
            dbRef.updateChildren(updates)
        } else {
            // Seller's usual logic (Accept/Reject/Counter)
            repository.updateBargainStatus(buyerId, productId, status, priceToSet, actorRole) { _ -> }
        }
    }
}
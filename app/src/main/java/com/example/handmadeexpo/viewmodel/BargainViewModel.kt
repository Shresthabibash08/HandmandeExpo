package com.example.handmadeexpo.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.handmadeexpo.model.BargainModel
import com.example.handmadeexpo.repo.BargainImpl
import com.example.handmadeexpo.repo.BargainRepo
import com.google.firebase.database.*

class BargainViewModel : ViewModel() {
    private val repository: BargainRepo = BargainImpl()

    // This list will be observed by the UI
    val sellerBargains = mutableStateListOf<BargainModel>()

    // For the Seller: Fetch all incoming requests for their products
    fun fetchBargainsForSeller(sellerId: String) {
        val dbRef = FirebaseDatabase.getInstance().getReference("Bargains")
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                sellerBargains.clear()
                snapshot.children.forEach { buyerNode ->
                    buyerNode.children.forEach { productNode ->
                        val bargain = productNode.getValue(BargainModel::class.java)
                        // Show to seller if it belongs to them and is actionable
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

    // For the Buyer: Fetch their own bargains to update prices in the Cart
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

    // Buyer sends a new bargain request
    fun requestBargain(bargain: BargainModel) {
        repository.sendBargainRequest(bargain) { success ->
            // You can add a LiveData or State for success/error messages here if needed
        }
    }

    // Seller updates the bargain (Accept, Reject, or Counter)
    fun updateStatus(
        buyerId: String,
        productId: String,
        status: String,
        counterPrice: String = "",
        sellerName: String = ""
    ) {
        repository.updateBargainStatus(buyerId, productId, status, counterPrice, sellerName) { success ->
            // You can add a LiveData or State for success/error messages here if needed
        }
    }
}
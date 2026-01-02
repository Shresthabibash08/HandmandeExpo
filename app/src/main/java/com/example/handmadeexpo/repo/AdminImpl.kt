package com.example.handmadeexpo.repo

import com.example.handmadeexpo.model.BuyerModel
import com.example.handmadeexpo.model.SellerModel
import com.google.firebase.database.*

class AdminImpl : AdminRepo {
    private val database = FirebaseDatabase.getInstance().reference

    override fun getSellers(onResult: (List<SellerModel>) -> Unit) {
        database.child("Seller").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = snapshot.children.mapNotNull { it.getValue(SellerModel::class.java) }
                onResult(list)
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    override fun getBuyers(onResult: (List<BuyerModel>) -> Unit) {
        database.child("Buyer").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = snapshot.children.mapNotNull { it.getValue(BuyerModel::class.java) }
                onResult(list)
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    override fun deleteUser(id: String, role: String, onComplete: (Boolean) -> Unit) {
        val node = if (role.equals("seller", ignoreCase = true)) "Seller" else "Buyer"
        database.child(node).child(id).removeValue()
            .addOnCompleteListener { onComplete(it.isSuccessful) }
    }
}
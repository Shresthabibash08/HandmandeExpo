package com.example.handmadeexpo.repo

import com.example.handmadeexpo.model.BuyerModel
import com.example.handmadeexpo.model.SellerModel
import com.google.firebase.database.*

class AdminImpl : AdminRepo { // This colon is essential!
    private val database = FirebaseDatabase.getInstance()

    override fun getSellers(onResult: (List<SellerModel>) -> Unit) {
        database.getReference("Sellers").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = snapshot.children.mapNotNull { it.getValue(SellerModel::class.java) }
                onResult(list)
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    override fun getBuyers(onResult: (List<BuyerModel>) -> Unit) {
        database.getReference("Buyers").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = snapshot.children.mapNotNull { it.getValue(BuyerModel::class.java) }
                onResult(list)
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    override fun deleteUser(id: String, role: String, onComplete: (Boolean) -> Unit) {
        val node = if (role.equals("seller", ignoreCase = true)) "Sellers" else "Buyers"
        database.getReference(node).child(id).removeValue()
            .addOnCompleteListener { onComplete(it.isSuccessful) }
    }
}
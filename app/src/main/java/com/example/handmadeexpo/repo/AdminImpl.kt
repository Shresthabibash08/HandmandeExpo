package com.example.handmadeexpo.repo

import android.util.Log
import com.example.handmadeexpo.model.BuyerModel
import com.example.handmadeexpo.model.SellerModel
import com.google.firebase.database.*

class AdminImpl : AdminRepo {
    private val database = FirebaseDatabase.getInstance().reference

    override fun getSellers(onResult: (List<SellerModel>) -> Unit) {

        database.child("Seller").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<SellerModel>()

                for (child in snapshot.children) {
                    try {
                        // --- CRASH PROTECTION ---
                        // Safely try to convert the data. If a specific entry is bad (like just text),
                        // it will jump to the catch block instead of crashing the whole app.
                        val seller = child.getValue(SellerModel::class.java)
                        if (seller != null) {
                            list.add(seller)
                        }
                    } catch (e: Exception) {
                        // Log the error so you can see which key is bad in Logcat, but don't crash
                        Log.e("AdminImpl", "Skipping bad Seller data at key: ${child.key}. Error: ${e.message}")
                    }
                }
                onResult(list)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("AdminImpl", "Database error: ${error.message}")
                onResult(emptyList())
            }
        })
    }

    override fun getBuyers(onResult: (List<BuyerModel>) -> Unit) {

        database.child("Buyer").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<BuyerModel>()

                for (child in snapshot.children) {
                    try {
                        // --- CRASH PROTECTION ---
                        val buyer = child.getValue(BuyerModel::class.java)
                        if (buyer != null) {
                            list.add(buyer)
                        }
                    } catch (e: Exception) {
                        Log.e("AdminImpl", "Skipping bad Buyer data at key: ${child.key}. Error: ${e.message}")
                    }
                }
                onResult(list)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("AdminImpl", "Database error: ${error.message}")
                onResult(emptyList())
            }
        })
    }

    override fun deleteUser(id: String, role: String, onComplete: (Boolean) -> Unit) {
        // Logic to ensure we delete from the correct Capitalized node
        val node = if (role.equals("seller", ignoreCase = true)) "Seller" else "Buyer"

        database.child(node).child(id).removeValue()
            .addOnCompleteListener { onComplete(it.isSuccessful) }
    }
}
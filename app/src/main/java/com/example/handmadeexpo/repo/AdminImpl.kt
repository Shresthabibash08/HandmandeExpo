package com.example.handmadeexpo.repo

import android.util.Log
import com.example.handmadeexpo.model.BuyerModel
import com.example.handmadeexpo.model.ProductModel
import com.example.handmadeexpo.model.SellerModel
import com.google.firebase.database.*

class AdminImpl : AdminRepo {
    private val database = FirebaseDatabase.getInstance().reference

    override fun getSellers(onResult: (List<SellerModel>) -> Unit) {
        database.child("Seller").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<SellerModel>()

                for (childSnapshot in snapshot.children) {
                    try {
                        // Combined Crash Protection: Safely parse and log errors
                        val seller = childSnapshot.getValue(SellerModel::class.java)
                        if (seller != null) {
                            list.add(seller)
                        } else {
                            Log.w("AdminImpl", "Null seller at key: ${childSnapshot.key}")
                        }
                    } catch (e: Exception) {
                        Log.e("AdminImpl", "Skipping bad Seller data at key: ${childSnapshot.key}. Error: ${e.message}")
                        Log.e("AdminImpl", "Raw Data: ${childSnapshot.value}")
                    }
                }
                Log.d("AdminImpl", "Successfully loaded ${list.size} sellers")
                onResult(list)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("AdminImpl", "getSellers cancelled: ${error.message}")
                onResult(emptyList())
            }
        })
    }

    override fun getBuyers(onResult: (List<BuyerModel>) -> Unit) {
        database.child("Buyer").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<BuyerModel>()

                for (childSnapshot in snapshot.children) {
                    try {
                        val buyer = childSnapshot.getValue(BuyerModel::class.java)
                        if (buyer != null) {
                            list.add(buyer)
                        } else {
                            Log.w("AdminImpl", "Null buyer at key: ${childSnapshot.key}")
                        }
                    } catch (e: Exception) {
                        Log.e("AdminImpl", "Skipping bad Buyer data at key: ${childSnapshot.key}. Error: ${e.message}")
                    }
                }
                Log.d("AdminImpl", "Successfully loaded ${list.size} buyers")
                onResult(list)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("AdminImpl", "getBuyers cancelled: ${error.message}")
                onResult(emptyList())
            }
        })
    }

    override fun deleteUser(id: String, role: String, onComplete: (Boolean) -> Unit) {
        val node = if (role.equals("seller", ignoreCase = true)) "Seller" else "Buyer"
        database.child(node).child(id).removeValue().addOnCompleteListener {
            onComplete(it.isSuccessful)
        }
    }

    // --- Seller Verification Methods (From Development) ---

    override fun updateSellerVerification(
        sellerId: String,
        status: String,
        callback: (Boolean, String) -> Unit
    ) {
        database.child("Seller").child(sellerId)
            .updateChildren(mapOf("verificationStatus" to status))
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true, "Seller $status successfully")
                } else {
                    callback(false, task.exception?.message ?: "Failed to update seller")
                }
            }
    }

    override fun getPendingSellers(onResult: (List<SellerModel>) -> Unit) = getSellersByStatus("Pending", onResult)
    override fun getVerifiedSellers(onResult: (List<SellerModel>) -> Unit) = getSellersByStatus("Verified", onResult)
    override fun getRejectedSellers(onResult: (List<SellerModel>) -> Unit) = getSellersByStatus("Rejected", onResult)

    private fun getSellersByStatus(status: String, onResult: (List<SellerModel>) -> Unit) {
        database.child("Seller")
            .orderByChild("verificationStatus")
            .equalTo(status)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list = mutableListOf<SellerModel>()
                    for (childSnapshot in snapshot.children) {
                        try {
                            childSnapshot.getValue(SellerModel::class.java)?.let { list.add(it) }
                        } catch (e: Exception) {
                            Log.e("AdminImpl", "Error parsing $status seller: ${e.message}")
                        }
                    }
                    onResult(list)
                }
                override fun onCancelled(error: DatabaseError) {
                    Log.e("AdminImpl", "Query $status sellers cancelled: ${error.message}")
                    onResult(emptyList())
                }
            })
    }

    // --- Product Verification Methods (From Development) ---

    override fun updateProductVerification(
        productId: String,
        status: String,
        rejectionReason: String,
        callback: (Boolean, String) -> Unit
    ) {
        val updates = mutableMapOf<String, Any>("verificationStatus" to status)
        if (status == "Rejected" && rejectionReason.isNotEmpty()) {
            updates["rejectionReason"] = rejectionReason
        }

        database.child("products").child(productId)
            .updateChildren(updates)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true, "Product $status successfully")
                } else {
                    callback(false, task.exception?.message ?: "Failed to update product")
                }
            }
    }

    override fun getPendingProducts(onResult: (List<ProductModel>) -> Unit) = getProductsByStatus("Pending", onResult)
    override fun getVerifiedProducts(onResult: (List<ProductModel>) -> Unit) = getProductsByStatus("Verified", onResult)
    override fun getRejectedProducts(onResult: (List<ProductModel>) -> Unit) = getProductsByStatus("Rejected", onResult)

    private fun getProductsByStatus(status: String, onResult: (List<ProductModel>) -> Unit) {
        database.child("products")
            .orderByChild("verificationStatus")
            .equalTo(status)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list = mutableListOf<ProductModel>()
                    for (childSnapshot in snapshot.children) {
                        try {
                            childSnapshot.getValue(ProductModel::class.java)?.let { list.add(it) }
                        } catch (e: Exception) {
                            Log.e("AdminImpl", "Error parsing $status product: ${e.message}")
                        }
                    }
                    onResult(list)
                }
                override fun onCancelled(error: DatabaseError) {
                    onResult(emptyList())
                }
            })
    }
}
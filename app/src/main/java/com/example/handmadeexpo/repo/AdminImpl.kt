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
                        // Try to parse as SellerModel
                        val seller = childSnapshot.getValue(SellerModel::class.java)
                        if (seller != null) {
                            list.add(seller)
                        } else {
                            Log.w("AdminImpl", "Null seller at: ${childSnapshot.key}")
                        }
                    } catch (e: Exception) {
                        // Log the error but continue processing other sellers
                        Log.e("AdminImpl", "Error parsing seller ${childSnapshot.key}: ${e.message}")
                        Log.e("AdminImpl", "Data: ${childSnapshot.value}")
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
                            Log.w("AdminImpl", "Null buyer at: ${childSnapshot.key}")
                        }
                    } catch (e: Exception) {
                        Log.e("AdminImpl", "Error parsing buyer ${childSnapshot.key}: ${e.message}")
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

    // Seller Verification Methods
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

    override fun getPendingSellers(onResult: (List<SellerModel>) -> Unit) {
        database.child("Seller")
            .orderByChild("verificationStatus")
            .equalTo("Pending")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list = mutableListOf<SellerModel>()

                    for (childSnapshot in snapshot.children) {
                        try {
                            val seller = childSnapshot.getValue(SellerModel::class.java)
                            if (seller != null) {
                                list.add(seller)
                            }
                        } catch (e: Exception) {
                            Log.e("AdminImpl", "Error parsing pending seller: ${e.message}")
                        }
                    }

                    Log.d("AdminImpl", "Pending sellers: ${list.size}")
                    onResult(list)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("AdminImpl", "getPendingSellers cancelled: ${error.message}")
                    onResult(emptyList())
                }
            })
    }

    override fun getVerifiedSellers(onResult: (List<SellerModel>) -> Unit) {
        database.child("Seller")
            .orderByChild("verificationStatus")
            .equalTo("Verified")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list = mutableListOf<SellerModel>()

                    for (childSnapshot in snapshot.children) {
                        try {
                            val seller = childSnapshot.getValue(SellerModel::class.java)
                            if (seller != null) {
                                list.add(seller)
                            }
                        } catch (e: Exception) {
                            Log.e("AdminImpl", "Error parsing verified seller: ${e.message}")
                        }
                    }

                    Log.d("AdminImpl", "Verified sellers: ${list.size}")
                    onResult(list)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("AdminImpl", "getVerifiedSellers cancelled: ${error.message}")
                    onResult(emptyList())
                }
            })
    }

    override fun getRejectedSellers(onResult: (List<SellerModel>) -> Unit) {
        database.child("Seller")
            .orderByChild("verificationStatus")
            .equalTo("Rejected")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list = mutableListOf<SellerModel>()

                    for (childSnapshot in snapshot.children) {
                        try {
                            val seller = childSnapshot.getValue(SellerModel::class.java)
                            if (seller != null) {
                                list.add(seller)
                            }
                        } catch (e: Exception) {
                            Log.e("AdminImpl", "Error parsing rejected seller: ${e.message}")
                        }
                    }

                    Log.d("AdminImpl", "Rejected sellers: ${list.size}")
                    onResult(list)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("AdminImpl", "getRejectedSellers cancelled: ${error.message}")
                    onResult(emptyList())
                }
            })
    }

    // Product Verification Methods
    override fun updateProductVerification(
        productId: String,
        status: String,
        rejectionReason: String,
        callback: (Boolean, String) -> Unit
    ) {
        val updates = mutableMapOf<String, Any>(
            "verificationStatus" to status
        )

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

    override fun getPendingProducts(onResult: (List<ProductModel>) -> Unit) {
        database.child("products")
            .orderByChild("verificationStatus")
            .equalTo("Pending")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list = mutableListOf<ProductModel>()

                    for (childSnapshot in snapshot.children) {
                        try {
                            val product = childSnapshot.getValue(ProductModel::class.java)
                            if (product != null) {
                                list.add(product)
                            }
                        } catch (e: Exception) {
                            Log.e("AdminImpl", "Error parsing pending product: ${e.message}")
                        }
                    }

                    Log.d("AdminImpl", "Pending products: ${list.size}")
                    onResult(list)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("AdminImpl", "getPendingProducts cancelled: ${error.message}")
                    onResult(emptyList())
                }
            })
    }

    override fun getVerifiedProducts(onResult: (List<ProductModel>) -> Unit) {
        database.child("products")
            .orderByChild("verificationStatus")
            .equalTo("Verified")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list = mutableListOf<ProductModel>()

                    for (childSnapshot in snapshot.children) {
                        try {
                            val product = childSnapshot.getValue(ProductModel::class.java)
                            if (product != null) {
                                list.add(product)
                            }
                        } catch (e: Exception) {
                            Log.e("AdminImpl", "Error parsing verified product: ${e.message}")
                        }
                    }

                    Log.d("AdminImpl", "Verified products: ${list.size}")
                    onResult(list)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("AdminImpl", "getVerifiedProducts cancelled: ${error.message}")
                    onResult(emptyList())
                }
            })
    }

    override fun getRejectedProducts(onResult: (List<ProductModel>) -> Unit) {
        database.child("products")
            .orderByChild("verificationStatus")
            .equalTo("Rejected")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list = mutableListOf<ProductModel>()

                    for (childSnapshot in snapshot.children) {
                        try {
                            val product = childSnapshot.getValue(ProductModel::class.java)
                            if (product != null) {
                                list.add(product)
                            }
                        } catch (e: Exception) {
                            Log.e("AdminImpl", "Error parsing rejected product: ${e.message}")
                        }
                    }

                    Log.d("AdminImpl", "Rejected products: ${list.size}")
                    onResult(list)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("AdminImpl", "getRejectedProducts cancelled: ${error.message}")
                    onResult(emptyList())
                }
            })
    }
}
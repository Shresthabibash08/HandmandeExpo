package com.example.handmadeexpo.repo

import com.example.handmadeexpo.model.BuyerModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class BuyerRepoImpl : BuyerRepo {
    val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    val ref: DatabaseReference = database.getReference("Buyer")

    override fun login(
        email: String,
        password: String,
        callback: (Boolean, String) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                callback(true, "Login Successfully")
            } else {
                callback(false, "${it.exception?.message}")
            }
        }
    }

    override fun forgotPassword(
        email: String,
        callback: (Boolean, String) -> Unit
    ) {
        auth.sendPasswordResetEmail(email).addOnCompleteListener {
            if (it.isSuccessful) {
                callback(true, "Reset link is sent to $email")
            } else {
                callback(false, "${it.exception?.message}")
            }
        }
    }

    override fun register(
        email: String,
        password: String,
        callback: (Boolean, String, String) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    val userId = auth.currentUser!!.uid

                    // ✅ SAVE ROLE HERE
                    FirebaseDatabase.getInstance()
                        .getReference("Buyer")
                        .child(userId)
                        .setValue(
                            mapOf(
                                "email" to email,
                                "role" to "buyer"
                            )
                        )

                    callback(true, "Registration Successful", userId)
                } else {
                    callback(false, task.exception?.message ?: "Error", "")
                }
            }
    }

    override fun logout(callback: (Boolean, String) -> Unit) {
        try {
            auth.signOut()
            callback(true, "Logout Successfully")
        } catch (e: Exception) {
            callback(false, e.message.toString())
        }
    }

    override fun updateProfile(
        buyerId: String,
        model: BuyerModel,
        callback: (Boolean, String) -> Unit
    ) {
        ref.child(buyerId).updateChildren(model.toMap()).addOnCompleteListener {
            if (it.isSuccessful) {
                callback(true, "Profile Updated Successfully")
            } else {
                callback(false, "${it.exception?.message}")
            }
        }
    }

    override fun deleteAccount(
        buyerId: String,
        callback: (Boolean, String) -> Unit
    ) {
        ref.child(buyerId).removeValue().addOnCompleteListener {
            if (it.isSuccessful) {
                callback(true, "Account Deleted Successfully")
            } else {
                callback(false, "${it.exception?.message}")
            }
        }
    }

    override fun getBuyerDetailsById(
        buyerId: String,
        callback: (Boolean, String, BuyerModel?) -> Unit
    ) {
        ref.child(buyerId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val buyer = snapshot.getValue(BuyerModel::class.java)
                    if (buyer != null) {
                        callback(true, "Buyer Details Fetched Successfully", buyer)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                callback(false, error.message, null)
            }
        })
    }

    override fun addBuyerToDatabase(
        buyerId: String,
        buyerModel: BuyerModel,
        callback: (Boolean, String) -> Unit
    ) {
        ref.child(buyerId).setValue(buyerModel).addOnCompleteListener {
            if (it.isSuccessful) {
                callback(true, "Registration Successfully")
            } else {
                callback(false, "${it.exception?.message}")
            }
        }
    }

    override fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    override fun getUserRole(
        userId: String,
        callback: (String?) -> Unit
    ) {
        val buyerRef = FirebaseDatabase.getInstance().getReference("Buyer").child(userId).child("role")
        val sellerRef = FirebaseDatabase.getInstance().getReference("Seller").child(userId).child("role")

        buyerRef.get().addOnSuccessListener { buyerSnapshot ->
            val role = buyerSnapshot.getValue(String::class.java)
            if (role != null) {
                callback(role)
            } else {
                sellerRef.get().addOnSuccessListener { sellerSnapshot ->
                    callback(sellerSnapshot.getValue(String::class.java))
                }.addOnFailureListener {
                    callback(null)
                }
            }
        }.addOnFailureListener {
            callback(null)
        }
    }
}

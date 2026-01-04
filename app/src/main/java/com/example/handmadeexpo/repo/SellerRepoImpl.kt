package com.example.handmadeexpo.repo

import com.example.handmadeexpo.model.SellerModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*

class SellerRepoImpl : SellerRepo {

    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val ref: DatabaseReference = database.getReference("Seller")

    override fun login(
        email: String,
        password: String,
        callback: (Boolean, String) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    callback(true, "Login Successfully")
                } else {
                    callback(false, it.exception?.message ?: "Login failed")
                }
            }
    }

    override fun forgotPassword(
        email: String,
        callback: (Boolean, String) -> Unit
    ) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    callback(true, "Reset email sent to $email")
                } else {
                    callback(false, it.exception?.message ?: "Failed")
                }
            }
    }

    override fun register(
        email: String,
        password: String,
        callback: (Boolean, String, String) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    callback(true, "Registration Successful", auth.currentUser?.uid ?: "")
                } else {
                    callback(false, it.exception?.message ?: "Registration failed", "")
                }
            }
    }

    override fun logout(callback: (Boolean, String) -> Unit) {
        try {
            auth.signOut()
            callback(true, "Logout Successfully")
        } catch (e: Exception) {
            callback(false, e.message ?: "Logout failed")
        }
    }

    override fun updateProfile(
        sellerId: String,
        model: SellerModel,
        callback: (Boolean, String) -> Unit
    ) {
        ref.child(sellerId)
            .updateChildren(model.toMap())
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    callback(true, "Profile Updated Successfully")
                } else {
                    callback(false, it.exception?.message ?: "Update failed")
                }
            }
    }

    override fun deleteAccount(
        sellerId: String,
        callback: (Boolean, String) -> Unit
    ) {
        ref.child(sellerId)
            .removeValue()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    callback(true, "Account Deleted Successfully")
                } else {
                    callback(false, it.exception?.message ?: "Delete failed")
                }
            }
    }

    override fun getSellerDetailsById(
        sellerId: String,
        callback: (Boolean, String, SellerModel?) -> Unit
    ) {
        ref.child(sellerId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val seller = snapshot.getValue(SellerModel::class.java)
                        callback(true, "Seller Details Fetched Successfully", seller)
                    } else {
                        callback(false, "Seller not found", null)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(false, error.message, null)
                }
            })
    }

    override fun addSellerToDatabase(
        sellerId: String,
        sellerModel: SellerModel,
        callback: (Boolean, String) -> Unit
    ) {
        ref.child(sellerId)
            .setValue(sellerModel)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    callback(true, "Seller added successfully")
                } else {
                    callback(false, it.exception?.message ?: "Failed")
                }
            }
    }

    override fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }
}

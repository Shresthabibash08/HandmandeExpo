package com.example.handmadeexpo.utils

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

object AdminEmailValidator {
    // Admin credentials
    private const val ADMIN_EMAIL = "admin@handmadeexpo.com"
    private val database = FirebaseDatabase.getInstance().reference

    /**
     * Check if an email is reserved/protected (admin email)
     * @param email - Email to validate
     * @return true if email is the admin email, false otherwise
     */
    fun isReservedEmail(email: String): Boolean {
        return email.trim().lowercase() == ADMIN_EMAIL.lowercase()
    }

    /**
     * Get validation error message for reserved emails
     * @return Generic message that doesn't reveal admin usage
     */
    fun getReservedEmailError(): String {
        return "This email is already registered with an existing account"
    }

    /**
     * Check if email exists in Buyer database
     * @param email - Email to check
     * @param onResult - Callback with boolean result (true if exists, false otherwise)
     */
    fun isBuyerEmailExists(email: String, onResult: (Boolean) -> Unit) {
        database.child("Buyer").orderByChild("buyerEmail").equalTo(email.trim().lowercase())
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    onResult(snapshot.exists())
                }
                override fun onCancelled(error: DatabaseError) {
                    onResult(false)
                }
            })
    }

    /**
     * Check if email exists in Seller database
     * @param email - Email to check
     * @param onResult - Callback with boolean result (true if exists, false otherwise)
     */
    fun isSellerEmailExists(email: String, onResult: (Boolean) -> Unit) {
        database.child("Seller").orderByChild("sellerEmail").equalTo(email.trim().lowercase())
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    onResult(snapshot.exists())
                }
                override fun onCancelled(error: DatabaseError) {
                    onResult(false)
                }
            })
    }

    /**
     * Get error message for duplicate email
     * @return Message indicating email is already in use
     */
    fun getDuplicateEmailError(): String {
        return "This email is already registered with an existing account"
    }
}
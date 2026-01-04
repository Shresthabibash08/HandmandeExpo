package com.example.handmadeexpo.repo

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.OpenableColumns
import com.cloudinary.Cloudinary
import com.cloudinary.utils.ObjectUtils
import com.example.handmadeexpo.model.SellerModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import java.io.InputStream
import java.util.concurrent.Executors

class SellerRepoImpl : SellerRepo {

    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val ref: DatabaseReference = database.getReference("Seller")

    private val cloudinary = Cloudinary(
        mapOf(
            "cloud_name" to "dj3k1ik5u",
            "api_key" to "522111894595947",
            "api_secret" to "aaGkabL6YKN4U3GNOJbLLAGT3wE"
        )
    )

    // --- Authentication Functions ---

    override fun login(email: String, password: String, callback: (Boolean, String) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true, "Login Successfully")
                } else {
                    callback(false, task.exception?.message ?: "Login failed")
                }
            }
    }

    override fun register(email: String, password: String, callback: (Boolean, String, String) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true, "Registration Successful", auth.currentUser?.uid ?: "")
                } else {
                    callback(false, task.exception?.message ?: "Registration failed", "")
                }
            }
    }

    override fun logout(callback: (Boolean, String) -> Unit) {
        try {
            auth.signOut()
            callback(true, "Logged out successfully")
        } catch (e: Exception) {
            callback(false, e.message ?: "Logout failed")
        }
    }

    override fun forgotPassword(email: String, callback: (Boolean, String) -> Unit) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true, "Reset email sent to $email")
                } else {
                    callback(false, task.exception?.message ?: "Failed to send reset email")
                }
            }
    }

    override fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    // --- Database Operations ---

    override fun addSellerToDatabase(sellerId: String, sellerModel: SellerModel, callback: (Boolean, String) -> Unit) {
        ref.child(sellerId).setValue(sellerModel).addOnCompleteListener { task ->
            if (task.isSuccessful) callback(true, "Seller added successfully")
            else callback(false, task.exception?.message ?: "Failed to add seller")
        }
    }

    override fun getSellerDetailsById(sellerId: String, callback: (Boolean, String, SellerModel?) -> Unit) {
        // Using addValueEventListener for real-time updates as seen in the development branch
        ref.child(sellerId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    try {
                        val seller = snapshot.getValue(SellerModel::class.java)
                        if (seller != null) callback(true, "Success", seller)
                        else callback(false, "Data is null", null)
                    } catch (e: Exception) {
                        callback(false, "Error parsing data", null)
                    }
                } else {
                    callback(false, "User not found", null)
                }
            }
            override fun onCancelled(error: DatabaseError) {
                callback(false, error.message, null)
            }
        })
    }

    override fun updateProfile(sellerId: String, model: SellerModel, callback: (Boolean, String) -> Unit) {
        // Uses toMap() for partial updates if available, otherwise sets the whole object
        ref.child(sellerId).updateChildren(model.toMap()).addOnCompleteListener { task ->
            if (task.isSuccessful) callback(true, "Profile Updated Successfully")
            else callback(false, task.exception?.message ?: "Update failed")
        }
    }

    override fun updateProfileFields(sellerId: String, updates: Map<String, Any>, callback: (Boolean, String) -> Unit) {
        ref.child(sellerId).updateChildren(updates).addOnCompleteListener { task ->
            if (task.isSuccessful) callback(true, "Fields Updated")
            else callback(false, task.exception?.message ?: "Update Failed")
        }
    }

    override fun deleteAccount(sellerId: String, callback: (Boolean, String) -> Unit) {
        ref.child(sellerId).removeValue().addOnCompleteListener { task ->
            if (task.isSuccessful) callback(true, "Account Deleted Successfully")
            else callback(false, task.exception?.message ?: "Delete failed")
        }
    }

    // --- Image Upload (Cloudinary) ---

    override fun uploadImage(context: Context, imageUri: Uri, callback: (Boolean, String) -> Unit) {
        val executor = Executors.newSingleThreadExecutor()
        executor.execute {
            try {
                val inputStream: InputStream? = context.contentResolver.openInputStream(imageUri)
                var fileName = getFileNameFromUri(context, imageUri)
                fileName = fileName?.substringBeforeLast(".") ?: "uploaded_image"

                val response = cloudinary.uploader().upload(
                    inputStream, ObjectUtils.asMap(
                        "public_id", fileName,
                        "resource_type", "image"
                    )
                )
                
                val imageUrl = (response["url"] as String?)?.replace("http://", "https://")

                Handler(Looper.getMainLooper()).post {
                    if (imageUrl != null) callback(true, imageUrl)
                    else callback(false, "Upload failed: URL is null")
                }
            } catch (e: Exception) {
                Handler(Looper.getMainLooper()).post {
                    callback(false, e.message ?: "Upload Error")
                }
            }
        }
    }

    private fun getFileNameFromUri(context: Context, uri: Uri): String? {
        var fileName: String? = null
        val cursor: Cursor? = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1) fileName = it.getString(nameIndex)
            }
        }
        return fileName
    }
}
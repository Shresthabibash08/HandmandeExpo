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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.io.InputStream
import java.util.concurrent.Executors

class SellerRepoImpl : SellerRepo {

    val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    val ref = database.getReference("Seller")

    private val cloudinary = Cloudinary(
        mapOf(
            "cloud_name" to "dj3k1ik5u",
            "api_key" to "522111894595947",
            "api_secret" to "aaGkabL6YKN4U3GNOJbLLAGT3wE"
        )
    )

    // --- PARTIAL UPDATE IMPLEMENTATION ---
    override fun updateProfileFields(sellerId: String, updates: Map<String, Any>, callback: (Boolean, String) -> Unit) {
        ref.child(sellerId).updateChildren(updates).addOnCompleteListener {
            if (it.isSuccessful) callback(true, "Profile Updated")
            else callback(false, it.exception?.message ?: "Update Failed")
        }
    }

    // --- CRASH-PROOF GET DETAILS ---
    override fun getSellerDetailsById(sellerId: String, callback: (Boolean, String, SellerModel?) -> Unit) {
        ref.child(sellerId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    try {
                        val seller = snapshot.getValue(SellerModel::class.java)
                        if (seller != null) callback(true, "Success", seller)
                        else callback(false, "Data is null", null)
                    } catch (e: Exception) {
                        e.printStackTrace()
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
                var imageUrl = response["url"] as String?
                imageUrl = imageUrl?.replace("http://", "https://")

                Handler(Looper.getMainLooper()).post {
                    if (imageUrl != null) callback(true, imageUrl)
                    else callback(false, "Upload failed: URL is null")
                }
            } catch (e: Exception) {
                e.printStackTrace()
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

    // --- STANDARD FIREBASE FUNCTIONS ---
    override fun login(email: String, password: String, callback: (Boolean, String) -> Unit) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) callback(true, "Login Successfully")
            else callback(false, "${it.exception?.message}")
        }
    }

    override fun register(email: String, password: String, callback: (Boolean, String, String) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) callback(true, "Registration Successfully", "${auth.currentUser?.uid}")
            else callback(false, "${it.exception?.message}", "")
        }
    }

    override fun addSellerToDatabase(sellerId: String, sellerModel: SellerModel, callback: (Boolean, String) -> Unit) {
        ref.child(sellerId).setValue(sellerModel).addOnCompleteListener {
            if (it.isSuccessful) callback(true, "Registration Successfully")
            else callback(false, "${it.exception?.message}")
        }
    }

    override fun updateProfile(sellerId: String, model: SellerModel, callback: (Boolean, String) -> Unit) {
        ref.child(sellerId).setValue(model).addOnCompleteListener {
            if (it.isSuccessful) callback(true, "Profile Updated")
            else callback(false, "${it.exception?.message}")
        }
    }

    override fun deleteAccount(sellerId: String, callback: (Boolean, String) -> Unit) {
        ref.child(sellerId).removeValue().addOnCompleteListener {
            if (it.isSuccessful) callback(true, "Deleted")
            else callback(false, "${it.exception?.message}")
        }
    }

    override fun forgotPassword(email: String, callback: (Boolean, String) -> Unit) {
        auth.sendPasswordResetEmail(email).addOnCompleteListener {
            if (it.isSuccessful) callback(true, "Email sent")
            else callback(false, "${it.exception?.message}")
        }
    }

    override fun logout(callback: (Boolean, String) -> Unit) {
        auth.signOut()
        callback(true, "Logged out")
    }

    override fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }
}
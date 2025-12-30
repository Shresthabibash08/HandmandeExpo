package com.example.handmadeexpo.repo

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.OpenableColumns
import com.cloudinary.Cloudinary
import com.cloudinary.utils.ObjectUtils
import com.example.handmadeexpo.model.ProductModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.io.InputStream
import java.util.concurrent.Executors

class ProductRepoImpl : ProductRepo {
    var database : FirebaseDatabase = FirebaseDatabase.getInstance()

    var ref : DatabaseReference = database.getReference("products")

    private val cloudinary = Cloudinary(
        mapOf(
            "cloud_name" to "dj3k1ik5u",
            "api_key" to "522111894595947",
            "api_secret" to "aaGkabL6YKN4U3GNOJbLLAGT3wE"
        )
    )

    override fun addProduct(
        model: ProductModel,
        callback: (Boolean, String) -> Unit
    ) {
        var id = ref.push().key.toString()
        model.productId = id

        ref.child(id).setValue(model).addOnCompleteListener {
            if(it.isSuccessful){
                callback(true,"Product added successfully")
            }else{
                callback(false,"${it.exception?.message}")
            }
        }

    }

    override fun updateProduct(
        model: ProductModel,
        callback: (Boolean, String) -> Unit
    ) {
        ref.child(model.productId).updateChildren(model.toMap()).addOnCompleteListener {
            if(it.isSuccessful){
                callback(true,"Product updated successfully")
            }else{
                callback(false,"${it.exception?.message}")
            }
        }
    }

    override fun deleteProduct(
        productID: String,
        callback: (Boolean, String) -> Unit
    ) {
        ref.child(productID).removeValue().addOnCompleteListener {
            if(it.isSuccessful){
                callback(true,"Product deleted successfully")
            }else{
                callback(false,"${it.exception?.message}")
            }
        }
    }

    override fun getProductById(
        productID: String,
        callback: (Boolean, String, ProductModel?) -> Unit
    ) {
        ref.child(productID).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    var dataa = snapshot.getValue(ProductModel::class.java)
                    if(dataa != null){
                        callback(true,"product fetched",dataa)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                callback(false,error.message,null)
            }
        })
    }

    override fun getAllProduct(callback: (Boolean, String, List<ProductModel>?) -> Unit) {
        ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    var allProducts = mutableListOf<ProductModel>()
                    for(data in snapshot.children){
                        var product = data.getValue(ProductModel::class.java)
                        if(product != null){
                            allProducts.add(product)
                        }
                    }

                    callback(true,"product fetched",allProducts)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                callback(false,error.message,emptyList())
            }

        })
    }

    override fun getProductByCategory(
        categoryId: String,
        callback: (Boolean, String, List<ProductModel>?) -> Unit
    ) {
        ref.orderByChild("categoryId").equalTo(categoryId).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    var allProducts = mutableListOf<ProductModel>()
                    for(data in snapshot.children){
                        var product = data.getValue(ProductModel::class.java)
                        if(product != null){
                            allProducts.add(product)
                        }
                    }

                    callback(true,"product fetched",allProducts)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                callback(false,error.message,emptyList())
            }

        })
    }

    override fun uploadImage(context: Context, imageUri: Uri, callback: (String?) -> Unit) {
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
                    callback(imageUrl)
                }

            } catch (e: Exception) {
                e.printStackTrace()
                Handler(Looper.getMainLooper()).post {
                    callback(null)
                }
            }
        }
    }

    override fun getFileNameFromUri(
        context: Context,
        uri: Uri
    ): String? {
        var fileName: String? = null
        val cursor: Cursor? = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1) {
                    fileName = it.getString(nameIndex)
                }
            }
        }
        return fileName
    }
}
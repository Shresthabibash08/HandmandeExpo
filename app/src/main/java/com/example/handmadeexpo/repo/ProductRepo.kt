package com.example.handmadeexpo.repo

import android.content.Context
import android.net.Uri
import com.example.handmadeexpo.model.ProductModel

interface ProductRepo {

//    {
//        succcess : true,
//        message : "product fetched succesfully"
//    }

    fun addProduct(model: ProductModel, callback:(Boolean, String)->Unit)

    fun updateProduct(model: ProductModel,callback: (Boolean, String) -> Unit)

    fun deleteProduct(productID:String,callback: (Boolean, String) -> Unit)

    fun getProductById(productID:String,callback: (Boolean, String, ProductModel?) -> Unit)

    fun getAllProduct(callback: (Boolean, String, List<ProductModel>?) -> Unit)

    fun getProductByCategory(categoryId:String,callback: (Boolean, String, List<ProductModel>?) -> Unit)

    fun uploadImage(context: Context, imageUri: Uri, callback: (String?) -> Unit )

    fun getFileNameFromUri(context:Context,uri: Uri):String?



}
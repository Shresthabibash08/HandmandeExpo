package com.example.handmadeexpo.repo

import android.content.Context
import android.net.Uri
import com.example.handmadeexpo.model.ProductModel
import com.example.handmadeexpo.model.ReportModel

interface ProductRepo {

//    {
//        succcess : true,
//        message : "product fetched succesfully"
//    }

    fun addProduct(model: ProductModel, callback:(Boolean, String,String?)->Unit)

    fun updateProduct(productId: String,model: ProductModel,callback: (Boolean, String) -> Unit)

    fun deleteProduct(productID:String,callback: (Boolean, String) -> Unit)

    fun getProductById(productID:String,callback: (Boolean, String, ProductModel?) -> Unit)

    fun getAllProductByCategory(category:String,callback:(Boolean,String,List<ProductModel?>)->Unit)

    fun getAvailableProducts(callback:(Boolean,String,List<ProductModel>?)->Unit)

    fun getAllProductByUser(userId:String,callback:(List<ProductModel>)->Unit)

    fun updateAvailability(productId:String,available:Boolean,callback:(Boolean,String)->Unit)

    fun updateRating(productId:String,available:Boolean,callback:(Boolean,String)->Unit)
    fun getAllProduct(callback: (Boolean, String, List<ProductModel>?) -> Unit)

    fun getProductByCategory(categoryId:String,callback: (Boolean, String, List<ProductModel>?) -> Unit)

    fun uploadImage(context: Context, imageUri: Uri, callback: (String?) -> Unit )

    fun getFileNameFromUri(context:Context,uri: Uri):String?

    fun getProductsBySeller( sellerId: String, callback: (List<ProductModel>) -> Unit)

    fun rateProduct(
        productId: String,
        rating: Int,
        callback: (Boolean) -> Unit
    )

    fun reportProduct(report: ReportModel, callback: (Boolean, String) -> Unit)
    fun getReportedProducts(callback: (List<ReportModel>?, String) -> Unit)

    fun updateReportStatus(reportId: String, status: String, callback: (Boolean) -> Unit)


}
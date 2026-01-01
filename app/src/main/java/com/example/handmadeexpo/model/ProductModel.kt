package com.example.handmadeexpo.model

data class ProductModel(
//    var productId: String = "",
//    var name: String = "",
//    var price: Double = 0.0,
//    var description: String = "",
//    var categoryId: String = "",
//    var image: String = "",
//    var stock: Int = 0,
//    var sellerId: String = "", // Add seller ID
//    var createdAt: Long = System.currentTimeMillis() // For sorting

    val title:String="",
    val listedBy:String="",
    val description:String="",
    val imageUri: List<String> = emptyList(),
    val price:Double=0.0,
    val productId:String="",
    val availability:Boolean=false,
    val outOfStock:Boolean=false,
    val rating:Double=0.0,
    val ratingCount:Int=0,
    val ratedBy:Map<String,Int> = emptyMap(),
    val category:String="",
    val verified:Boolean=false,
    val flaggedBy:List<String> =emptyList(),
    val flagged:Boolean=false,
    val flaggedReason:List<String> =emptyList(),
    val appealReason:String=""

){
    fun toMap() : Map<String,Any?>{
        return mapOf(
//            "productId" to productId,
//            "name" to name,
//            "price" to price,
//            "description" to description,
//            "categoryId" to categoryId,
//            "image" to image,
//            "stock" to stock,
//            "sellerId" to sellerId,
//            "createdAt" to createdAt

            "title" to title,
            "listedBy" to listedBy,
            "description" to description,
            "imageUri" to imageUri,
            "price" to price,
            "productId" to productId,
            "availability" to availability,
            "outOfStock" to outOfStock,
            "rating" to rating,
            "ratingCount" to ratingCount,
            "ratedBy" to ratedBy,
            "category" to category,
            "verified" to verified,
            "flaggedBy" to flaggedBy,
            "flagged" to flagged,
            "flaggedReason" to flaggedReason,
            "appealReason" to appealReason,

        )
    }

}
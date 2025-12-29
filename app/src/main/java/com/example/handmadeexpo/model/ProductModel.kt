package com.example.handmadeexpo.model

data class ProductModel(
    var productId: String = "",
    var name: String = "",
    var price: Double = 0.0,
    var description: String = "",
    var categoryId : String = "",
    var image : String = "",
    var stock : Int = 0

){
    fun toMap() : Map<String,Any?>{
        return mapOf(
            "name" to name,
            "price" to price,
            "description" to description
        )
    }

}
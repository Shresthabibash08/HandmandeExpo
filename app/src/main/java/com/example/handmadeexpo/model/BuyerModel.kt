package com.example.handmadeexpo.model

data class BuyerModel(
    val buyerId:String="",
    val buyerName:String="",
    val buyerEmail:String="",
    val buyerPhoneNumber:String="",
    val buyerAddress:String="",
    var role: String = "buyer"){
    fun toMap() : Map<String,Any?>{
        return mapOf(
            "buyerId" to buyerId,
            "buyerName" to buyerName,
            "buyerEmail" to buyerEmail,
            "buyerPhoneNumber" to buyerPhoneNumber,
            "buyerAddress" to buyerAddress,
        )
    }
}


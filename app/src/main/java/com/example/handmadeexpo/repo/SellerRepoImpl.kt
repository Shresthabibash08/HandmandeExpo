package com.example.handmadeexpo.repo

import com.example.handmadeexpo.model.BuyerModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SellerRepoImpl :SellerRepo{
    var database: FirebaseDatabase= FirebaseDatabase.getInstance()
    var auth: FirebaseAuth= FirebaseAuth.getInstance()
    var ref: DatabaseReference=database.getReference("Buyer")


    override fun login(
        email: String,
        password: String,
        callback: (Boolean, String) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener {
            if(it.isSuccessful){
                callback(true,"Login Successfully")
            }
            else{
                callback(false,"${it.exception?.message}")
            }
        }
    }


    override fun forgotPassword(
        email: String,
        callback: (Boolean, String) -> Unit
    ) {
        auth.sendPasswordResetEmail(email).addOnCompleteListener {
            if(it.isSuccessful){
                callback(true,"Reset Email is sent to $email")
            }
            else{
                callback(false,"${it.exception?.message}")
            }
        }
    }

    override fun register(
        email: String,
        password: String,
        callback: (Boolean, String,String) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener {
            if(it.isSuccessful){
                callback(true,"Registration Successfully", "${auth.currentUser?.uid}")
            }
            else{
                callback(false,"${it.exception?.message}","")
            }
        }
    }

    override fun logout(callback: (Boolean, String) -> Unit) {
        try{
            auth.signOut()
            callback(true,"Logout Successfully")
        }
        catch (e:Exception){
            callback(false,e.message.toString())
        }
    }

    override fun updateProfile(
        model: BuyerModel,
        callback: (Boolean, String) -> Unit
    ) {
        ref.child(model.buyerId).updateChildren(model.toMap()).addOnCompleteListener {
            if(it.isSuccessful){
                callback(true,"Profile Updated Successfully")
            }
            else{
                callback(false,"${it.exception?.message}")
            }
        }
    }

    override fun deleteAccount(
        buyerId: String,
        callback: (Boolean, String) -> Unit
    ) {
        ref.child(buyerId).removeValue().addOnCompleteListener {
            if(it.isSuccessful){
                callback(true,"Account Deleted Successfully")
            }
            else{
                callback(false,"${it.exception?.message}")
            }
        }
    }



    override fun getBuyerDetailsById(
        buyerId: String,
        callback: (Boolean, String, BuyerModel?) -> Unit
    ) {
        ref.child(buyerId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    val buyer=snapshot.getValue(BuyerModel::class.java)
                    if(buyer!=null){
                        callback(true,"Buyer Details Fetched Successfully",buyer)
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                callback(false,error.message,null)
            }
        })
    }

    override fun addBuyerToDatabase(
        buyerId: String,
        buyerModel: BuyerModel,
        callback: (Boolean, String) -> Unit
    ) {
        ref.child(buyerId).setValue(buyerModel).addOnCompleteListener {
            if(it.isSuccessful){
                callback(true,"Registration Successfully")
            }
            else{
                callback(false,"${it.exception?.message}")
            }
        }
    }

    override fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }
}
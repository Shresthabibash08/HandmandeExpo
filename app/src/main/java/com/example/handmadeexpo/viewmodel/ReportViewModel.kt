package com.example.handmadeexpo.viewmodel

import androidx.lifecycle.ViewModel
import com.example.handmadeexpo.model.ReportModel
import com.example.handmadeexpo.repo.ProductRepo
import com.example.handmadeexpo.repo.ProductRepoImpl

class ReportViewModel : ViewModel() {
    private val repo: ProductRepo = ProductRepoImpl()

    fun submitReport(productId: String, reason: String, callback: (Boolean, String) -> Unit) {
        val currentUser = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            callback(false, "You must be logged in to report.")
            return
        }

        val report = ReportModel(
            productId = productId,
            reporterId = currentUser.uid,
            reason = reason
        )

        repo.reportProduct(report) { success, msg ->
            callback(success, msg)
        }
    }
}
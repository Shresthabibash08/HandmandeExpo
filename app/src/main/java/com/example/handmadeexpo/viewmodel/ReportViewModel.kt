package com.example.handmadeexpo.viewmodel

import androidx.lifecycle.ViewModel
import com.example.handmadeexpo.model.ReportModel
import com.example.handmadeexpo.repo.ReportRepo
import com.example.handmadeexpo.repo.ReportRepoImpl
import com.google.firebase.auth.FirebaseAuth

class ReportViewModel : ViewModel() {
    private val repo: ReportRepo = ReportRepoImpl()
    private val auth = FirebaseAuth.getInstance()

    // FIXED: Renamed to 'submitReport' to match your ReportProductScreen call
    fun submitReport(productId: String, reason: String, callback: (Boolean, String) -> Unit) {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            callback(false, "You must be logged in to report.")
            return
        }

        val report = ReportModel(
            reportedId = productId, // We map the incoming productId to the generic reportedId
            reporterId = currentUser.uid,
            reportType = "PRODUCT", // Explicitly mark as Product
            reason = reason
        )

        repo.submitReport(report) { success, msg ->
            callback(success, msg)
        }
    }

    // This handles the new Seller Reporting feature
    fun reportSeller(sellerId: String, reason: String, callback: (Boolean, String) -> Unit) {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            callback(false, "You must be logged in to report.")
            return
        }

        val report = ReportModel(
            reportedId = sellerId,
            reporterId = currentUser.uid,
            reportType = "SELLER", // Explicitly mark as Seller
            reason = reason
        )

        repo.submitReport(report) { success, msg ->
            callback(success, msg)
        }
    }
}
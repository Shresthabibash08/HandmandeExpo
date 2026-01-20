package com.example.handmadeexpo.viewmodel

import androidx.lifecycle.ViewModel
import com.example.handmadeexpo.model.ReportModel
import com.example.handmadeexpo.repo.ReportRepo
import com.example.handmadeexpo.repo.ReportRepoImpl
import com.google.firebase.auth.FirebaseAuth

class ReportViewModel : ViewModel() {
    private val repo: ReportRepo = ReportRepoImpl()
    private val auth = FirebaseAuth.getInstance()

    // 1. Report Product (Existing)
    fun submitReport(productId: String, reason: String, callback: (Boolean, String) -> Unit) {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            callback(false, "You must be logged in to report.")
            return
        }

        val report = ReportModel(
            reportedId = productId,
            reporterId = currentUser.uid,
            reportType = "PRODUCT",
            reason = reason
        )

        repo.submitReport(report) { success, msg -> callback(success, msg) }
    }

    // 2. Report Seller (Existing)
    fun reportSeller(sellerId: String, reason: String, callback: (Boolean, String) -> Unit) {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            callback(false, "You must be logged in to report.")
            return
        }

        val report = ReportModel(
            reportedId = sellerId,
            reporterId = currentUser.uid,
            reportType = "SELLER",
            reason = reason
        )

        repo.submitReport(report) { success, msg -> callback(success, msg) }
    }

    // 3. NEW: Report Buyer
    fun reportBuyer(buyerId: String, reason: String, callback: (Boolean, String) -> Unit) {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            callback(false, "You must be logged in to report.")
            return
        }

        val report = ReportModel(
            reportedId = buyerId,
            reporterId = currentUser.uid, // This is the Seller's ID
            reportType = "BUYER",         // IMPORTANT: Tag as BUYER
            reason = reason
        )

        repo.submitReport(report) { success, msg -> callback(success, msg) }
    }
}
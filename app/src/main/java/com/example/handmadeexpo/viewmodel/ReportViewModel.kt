package com.example.handmadeexpo.viewmodel

import androidx.lifecycle.ViewModel
import com.example.handmadeexpo.model.ReportModel
import com.example.handmadeexpo.repo.ReportRepo
import com.example.handmadeexpo.repo.ReportRepoImpl
import com.google.firebase.auth.FirebaseAuth
import java.util.UUID

class ReportViewModel : ViewModel() {
    private val repo: ReportRepo = ReportRepoImpl()
    private val auth = FirebaseAuth.getInstance()

    // 1. Report Product
    fun submitReport(productId: String, reason: String, callback: (Boolean, String) -> Unit) {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            callback(false, "You must be logged in to report.")
            return
        }

        val report = ReportModel(
            reportId = UUID.randomUUID().toString(), // Generate Unique ID
            reportedId = productId,
            reporterId = currentUser.uid,
            reportType = "PRODUCT",
            reason = reason,
            timestamp = System.currentTimeMillis() // Capture current time
        )

        repo.submitReport(report) { success, msg -> callback(success, msg) }
    }

    // 2. Report Seller
    fun reportSeller(sellerId: String, reason: String, callback: (Boolean, String) -> Unit) {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            callback(false, "You must be logged in to report.")
            return
        }

        val report = ReportModel(
            reportId = UUID.randomUUID().toString(),
            reportedId = sellerId,
            reporterId = currentUser.uid,
            reportType = "SELLER",
            reason = reason,
            timestamp = System.currentTimeMillis()
        )

        repo.submitReport(report) { success, msg -> callback(success, msg) }
    }

    // 3. Report Buyer (Called by Seller)
    fun reportBuyer(buyerId: String, reason: String, callback: (Boolean, String) -> Unit) {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            callback(false, "You must be logged in to report.")
            return
        }

        val report = ReportModel(
            reportId = UUID.randomUUID().toString(),
            reportedId = buyerId,         // The Buyer's ID
            reporterId = currentUser.uid, // The Seller's ID (Reporter)
            reportType = "BUYER",         // Tag as BUYER
            reason = reason,
            timestamp = System.currentTimeMillis()
        )

        repo.submitReport(report) { success, msg -> callback(success, msg) }
    }
}
package com.example.handmadeexpo.viewmodel

import androidx.lifecycle.ViewModel
import com.example.handmadeexpo.model.ReportModel
import com.example.handmadeexpo.repo.ProductRepo
import com.example.handmadeexpo.repo.ProductRepoImpl
import com.example.handmadeexpo.repo.ReportRepo
import com.example.handmadeexpo.repo.ReportRepoImpl
import com.google.firebase.database.FirebaseDatabase // Import this
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AdminReportViewModel : ViewModel() {
    private val productRepo: ProductRepo = ProductRepoImpl()
    private val reportRepo: ReportRepo = ReportRepoImpl()

    private val _reports = MutableStateFlow<List<ReportModel>>(emptyList())
    val reports: StateFlow<List<ReportModel>> = _reports

    init {
        fetchReports()
    }

    fun fetchReports() {
        reportRepo.getReportedProducts { fetchedReports, _ ->
            if (fetchedReports != null) {
                _reports.value = fetchedReports
            }
        }
    }

    // --- NEW FUNCTION: Ban Seller ---
    fun banSeller(report: ReportModel, callback: (String) -> Unit) {
        val db = FirebaseDatabase.getInstance()

        // 1. Set 'banned' to true in the Seller's node
        val updates = mapOf<String, Any>("banned" to true)

        db.getReference("Seller").child(report.reportedId).updateChildren(updates)
            .addOnSuccessListener {
                // 2. Mark report as accepted/handled so it leaves the list
                reportRepo.updateReportStatus(report.reportId, "Accepted - Banned") {
                    fetchReports()
                    callback("Seller has been BANNED successfully")
                }
            }
            .addOnFailureListener {
                callback("Failed to ban seller: ${it.message}")
            }
    }

    fun acceptReport(report: ReportModel, callback: (String) -> Unit) {
        if (report.reportType == "PRODUCT") {
            productRepo.deleteProduct(report.reportedId) { success, msg ->
                if (success) {
                    reportRepo.updateReportStatus(report.reportId, "Accepted") {
                        fetchReports()
                        callback("Product Deleted & Report Accepted")
                    }
                } else {
                    callback("Failed to delete product: $msg")
                }
            }
        } else {
            // For general accept without banning (optional, but usually we use banSeller now)
            reportRepo.updateReportStatus(report.reportId, "Accepted") {
                fetchReports()
                callback("Report Accepted")
            }
        }
    }

    fun rejectReport(report: ReportModel, callback: (String) -> Unit) {
        reportRepo.updateReportStatus(report.reportId, "Rejected") {
            fetchReports()
            callback("Report Ignored")
        }
    }
}
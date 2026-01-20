package com.example.handmadeexpo.viewmodel

import androidx.lifecycle.ViewModel
import com.example.handmadeexpo.model.ReportModel
import com.example.handmadeexpo.repo.ProductRepo
import com.example.handmadeexpo.repo.ProductRepoImpl
import com.example.handmadeexpo.repo.ReportRepo
import com.example.handmadeexpo.repo.ReportRepoImpl
import com.google.firebase.database.FirebaseDatabase
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

    // --- Ban Seller Function ---
    fun banSeller(report: ReportModel, callback: (String) -> Unit) {
        val db = FirebaseDatabase.getInstance()

        // 1. Set 'banned' to true in the Seller's node
        val updates = mapOf<String, Any>("banned" to true)

        db.getReference("Seller").child(report.reportedId).updateChildren(updates)
            .addOnSuccessListener {
                // 2. Mark report as accepted so it leaves the list
                reportRepo.updateReportStatus(report.reportId, "Accepted - Banned") {
                    fetchReports()
                    callback("Seller has been BANNED successfully")
                }
            }
            .addOnFailureListener {
                callback("Failed to ban seller: ${it.message}")
            }
    }

    // --- NEW: Ban Buyer Function ---
    fun banBuyer(report: ReportModel, callback: (String) -> Unit) {
        val db = FirebaseDatabase.getInstance()

        // 1. Set 'banned' to true in the Buyer's node
        // Note: Ensure your database node is named "Buyer" (capitalized) to match your other files
        val updates = mapOf<String, Any>("banned" to true)

        db.getReference("Buyer").child(report.reportedId).updateChildren(updates)
            .addOnSuccessListener {
                // 2. Mark report as accepted
                reportRepo.updateReportStatus(report.reportId, "Accepted - Banned") {
                    fetchReports()
                    callback("Buyer has been BANNED successfully")
                }
            }
            .addOnFailureListener {
                callback("Failed to ban buyer: ${it.message}")
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
            // Fallback for general acceptance if not using ban function
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
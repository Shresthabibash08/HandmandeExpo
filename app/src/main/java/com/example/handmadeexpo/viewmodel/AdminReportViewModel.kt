package com.example.handmadeexpo.viewmodel

import androidx.lifecycle.ViewModel
import com.example.handmadeexpo.model.ReportModel
import com.example.handmadeexpo.repo.ProductRepo
import com.example.handmadeexpo.repo.ProductRepoImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AdminReportViewModel : ViewModel() {
    private val repo: ProductRepo = ProductRepoImpl()

    private val _reports = MutableStateFlow<List<ReportModel>>(emptyList())
    val reports: StateFlow<List<ReportModel>> = _reports

    init {
        fetchReports()
    }

    fun fetchReports() {
        repo.getReportedProducts { fetchedReports, _ ->
            if (fetchedReports != null) {
                _reports.value = fetchedReports
            }
        }
    }

    fun acceptReport(report: ReportModel, callback: (String) -> Unit) {
        repo.deleteProduct(report.productId) { success, msg ->
            if (success) {
                repo.updateReportStatus(report.reportId, "Accepted") {
                    fetchReports()
                    callback("Product Deleted & Report Accepted")
                }
            } else {
                callback("Failed to delete product: $msg")
            }
        }
    }

    fun rejectReport(report: ReportModel, callback: (String) -> Unit) {
        repo.updateReportStatus(report.reportId, "Rejected") {
            fetchReports()
            callback("Report Ignored")
        }
    }
}
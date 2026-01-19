package com.example.handmadeexpo.repo

import com.example.handmadeexpo.model.ReportModel

interface ReportRepo {
    // For Buyers/Users to submit
    fun submitReport(report: ReportModel, callback: (Boolean, String) -> Unit)

    // For Admins to view
    fun getReportedProducts(callback: (List<ReportModel>?, String) -> Unit)

    // For Admins to resolve/reject
    fun updateReportStatus(reportId: String, status: String, callback: (Boolean) -> Unit)
}
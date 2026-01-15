package com.example.handmadeexpo.repo

import com.example.handmadeexpo.model.ReportModel

interface ReportRepo {
    fun submitReport(report: ReportModel, callback: (Boolean, String) -> Unit)
    fun sendWarningToBuyer(buyerId: String, reason: String, callback: (Boolean, String) -> Unit)
    fun notifyAdmin(report: ReportModel, callback: (Boolean, String) -> Unit)
}
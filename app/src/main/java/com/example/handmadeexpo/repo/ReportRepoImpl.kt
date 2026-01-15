package com.example.handmadeexpo.repo

import com.example.handmadeexpo.model.ReportModel
import com.google.firebase.database.FirebaseDatabase

class ReportRepoImpl : ReportRepo {
    private val database = FirebaseDatabase.getInstance()
    private val reportsRef = database.getReference("Reports")
    private val warningsRef = database.getReference("Warnings")
    private val adminNotificationsRef = database.getReference("AdminNotifications")

    override fun submitReport(report: ReportModel, callback: (Boolean, String) -> Unit) {
        val reportId = reportsRef.push().key ?: return callback(false, "Failed to generate report ID")

        val reportWithId = report.copy(reportId = reportId)

        reportsRef.child(reportId).setValue(reportWithId.toMap())
            .addOnSuccessListener {
                callback(true, "Report submitted successfully")
            }
            .addOnFailureListener { e ->
                callback(false, "Failed to submit report: ${e.message}")
            }
    }

    override fun sendWarningToBuyer(buyerId: String, reason: String, callback: (Boolean, String) -> Unit) {
        val warningId = warningsRef.push().key ?: return callback(false, "Failed to generate warning ID")

        val warning = mapOf(
            "warningId" to warningId,
            "userId" to buyerId,
            "reason" to reason,
            "timestamp" to System.currentTimeMillis(),
            "isRead" to false
        )

        warningsRef.child(buyerId).child(warningId).setValue(warning)
            .addOnSuccessListener {
                callback(true, "Warning sent to buyer")
            }
            .addOnFailureListener { e ->
                callback(false, "Failed to send warning: ${e.message}")
            }
    }

    override fun notifyAdmin(report: ReportModel, callback: (Boolean, String) -> Unit) {
        val notificationId = adminNotificationsRef.push().key ?: return callback(false, "Failed to generate notification ID")

        val notification = mapOf(
            "notificationId" to notificationId,
            "reportId" to report.reportId,
            "type" to "user_report",
            "reporterId" to report.reporterId,
            "reporterName" to report.reporterName,
            "reportedUserId" to report.reportedUserId,
            "reportedUserName" to report.reportedUserName,
            "reason" to report.reason,
            "timestamp" to report.timestamp,
            "isRead" to false
        )

        adminNotificationsRef.child(notificationId).setValue(notification)
            .addOnSuccessListener {
                callback(true, "Admin notified successfully")
            }
            .addOnFailureListener { e ->
                callback(false, "Failed to notify admin: ${e.message}")
            }
    }
}
package com.example.handmadeexpo.viewmodel

import androidx.lifecycle.ViewModel
import com.example.handmadeexpo.model.ReportModel
import com.example.handmadeexpo.repo.ReportRepo
import com.example.handmadeexpo.repo.ReportRepoImpl
import com.google.firebase.database.FirebaseDatabase

class ReportViewModel : ViewModel() {
    private val repo: ReportRepo = ReportRepoImpl()
    private val database = FirebaseDatabase.getInstance()

    fun reportUser(
        reporterId: String,
        reporterName: String,
        reportedUserId: String,
        reportedUserName: String,
        reason: String,
        callback: (Boolean, String) -> Unit
    ) {
        if (reason.isBlank()) {
            callback(false, "Please provide a reason for reporting")
            return
        }

        val report = ReportModel(
            reporterId = reporterId,
            reporterName = reporterName,
            reporterRole = "seller",
            reportedUserId = reportedUserId,
            reportedUserName = reportedUserName,
            reportedUserRole = "buyer",
            reason = reason,
            timestamp = System.currentTimeMillis(),
            status = "pending"
        )

        // Submit report
        repo.submitReport(report) { success, message ->
            if (success) {
                // Send warning to buyer
                repo.sendWarningToBuyer(reportedUserId, reason) { warningSuccess, _ ->
                    // Check total warnings and apply ban if needed
                    checkAndApplyBan(reportedUserId) { banApplied ->
                        // Notify admin
                        repo.notifyAdmin(report) { adminNotified, _ ->
                            val resultMessage = if (banApplied) {
                                "Report submitted. Buyer has been temporarily banned due to multiple warnings."
                            } else {
                                "Report submitted successfully. Buyer has been warned and admin notified."
                            }
                            callback(true, resultMessage)
                        }
                    }
                }
            } else {
                callback(false, message)
            }
        }
    }

    private fun checkAndApplyBan(userId: String, callback: (Boolean) -> Unit) {
        val warningsRef = database.getReference("Warnings").child(userId)

        warningsRef.get().addOnSuccessListener { snapshot ->
            val warningCount = snapshot.childrenCount.toInt()

            // Apply ban if 3 or more warnings
            if (warningCount >= 3) {
                applyTemporaryBan(userId, warningCount)
                callback(true)
            } else {
                callback(false)
            }
        }.addOnFailureListener {
            callback(false)
        }
    }

    private fun applyTemporaryBan(userId: String, warningCount: Int) {
        val bansRef = database.getReference("BannedUsers").child(userId)

        // Calculate ban duration based on warning count
        val banDurationDays = when {
            warningCount >= 5 -> 30  // 5+ warnings = 30 days
            warningCount >= 4 -> 14  // 4 warnings = 14 days
            else -> 7  // 3 warnings = 7 days
        }

        val banExpiryTime = System.currentTimeMillis() + (banDurationDays * 24 * 60 * 60 * 1000L)

        val banData = mapOf(
            "userId" to userId,
            "bannedAt" to System.currentTimeMillis(),
            "banExpiresAt" to banExpiryTime,
            "banDurationDays" to banDurationDays,
            "warningCount" to warningCount,
            "reason" to "Multiple warnings from sellers",
            "isActive" to true
        )

        bansRef.setValue(banData)
    }

    fun checkIfUserBanned(userId: String, callback: (Boolean, Long?) -> Unit) {
        val bansRef = database.getReference("BannedUsers").child(userId)

        bansRef.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                val banExpiresAt = snapshot.child("banExpiresAt").getValue(Long::class.java) ?: 0L
                val isActive = snapshot.child("isActive").getValue(Boolean::class.java) ?: false
                val currentTime = System.currentTimeMillis()

                if (isActive && currentTime < banExpiresAt) {
                    // User is still banned
                    callback(true, banExpiresAt)
                } else if (isActive && currentTime >= banExpiresAt) {
                    // Ban expired, lift it
                    bansRef.child("isActive").setValue(false)
                    callback(false, null)
                } else {
                    callback(false, null)
                }
            } else {
                callback(false, null)
            }
        }.addOnFailureListener {
            callback(false, null)
        }
    }
}
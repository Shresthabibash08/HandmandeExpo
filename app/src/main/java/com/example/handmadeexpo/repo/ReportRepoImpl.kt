package com.example.handmadeexpo.repo

import com.example.handmadeexpo.model.ReportModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ReportRepoImpl : ReportRepo {
    // Initialize Realtime Database Reference for "reports"
    private val db = FirebaseDatabase.getInstance().getReference("reports")

    override fun submitReport(report: ReportModel, callback: (Boolean, String) -> Unit) {
        val newDocRef = db.push() // Generate unique key
        val reportId = newDocRef.key ?: return callback(false, "Failed to generate ID")

        // Add the generated ID to the model
        val reportWithId = report.copy(reportId = reportId)

        newDocRef.setValue(reportWithId)
            .addOnSuccessListener {
                callback(true, "Report submitted successfully.")
            }
            .addOnFailureListener { e ->
                callback(false, "Failed to submit report: ${e.message}")
            }
    }

    override fun getReportedProducts(callback: (List<ReportModel>?, String) -> Unit) {
        db.orderByChild("status").equalTo("Pending")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val reports = mutableListOf<ReportModel>()
                    for (child in snapshot.children) {
                        child.getValue(ReportModel::class.java)?.let { reports.add(it) }
                    }
                    callback(reports, "Success")
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(null, error.message)
                }
            })
    }

    override fun updateReportStatus(reportId: String, status: String, callback: (Boolean) -> Unit) {
        db.child(reportId).child("status").setValue(status)
            .addOnCompleteListener { task ->
                callback(task.isSuccessful)
            }
    }
}
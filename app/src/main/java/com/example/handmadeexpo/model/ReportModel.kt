package com.example.handmadeexpo.model

data class ReportModel(
    val reportId: String = "",
    val productId: String = "",
    val reporterId: String = "",
    val reason: String = "",
    val status: String = "Pending", // Pending, Reviewed, Resolved
    val timestamp: Long = System.currentTimeMillis()
)
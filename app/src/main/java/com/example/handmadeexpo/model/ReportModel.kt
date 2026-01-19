package com.example.handmadeexpo.model

data class ReportModel(
    var reportId: String = "",
    var reporterId: String = "", // <--- NEW: Stores who submitted the report
    var reportedId: String = "", // Stores Product ID or Seller ID
    var reason: String = "",
    var reportType: String = "", // "PRODUCT" or "SELLER"
    var status: String = "Pending",
    var timestamp: Long = System.currentTimeMillis()
)
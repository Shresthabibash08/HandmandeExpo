package com.example.handmadeexpo.model

data class ReportModel(
    val reportId: String = "",
    val reporterId: String = "",
    val reporterName: String = "",
    val reporterRole: String = "seller",
    val reportedUserId: String = "",
    val reportedUserName: String = "",
    val reportedUserRole: String = "buyer",
    val reason: String = "",
    val timestamp: Long = 0L,
    val status: String = "pending" // pending, reviewed, resolved
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "reportId" to reportId,
            "reporterId" to reporterId,
            "reporterName" to reporterName,
            "reporterRole" to reporterRole,
            "reportedUserId" to reportedUserId,
            "reportedUserName" to reportedUserName,
            "reportedUserRole" to reportedUserRole,
            "reason" to reason,
            "timestamp" to timestamp,
            "status" to status
        )
    }
}
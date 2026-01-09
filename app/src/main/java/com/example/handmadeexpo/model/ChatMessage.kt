package com.example.handmadeexpo.model

data class ChatMessage(
    val senderId: String = "",
    val receiverId: String = "", // Added receiverId
    val message: String = "",
    val timestamp: Long = 0L
)
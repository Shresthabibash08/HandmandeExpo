package com.example.handmadeexpo.repo

import com.example.handmadeexpo.model.ChatMessage

interface ChatRepo {
    fun sendMessage(chatId: String, chatMessage: ChatMessage)
    fun getMessages(chatId: String, onUpdate: (List<ChatMessage>) -> Unit)
}
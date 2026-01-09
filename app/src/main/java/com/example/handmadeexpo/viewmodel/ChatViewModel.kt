package com.example.handmadeexpo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.handmadeexpo.model.ChatMessage
import com.example.handmadeexpo.repo.ChatRepoImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ChatViewModel(private val repo: ChatRepoImpl) : ViewModel() {
    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages

    fun listenForMessages(chatId: String) {
        viewModelScope.launch {
            repo.listenForMessages(chatId).collect { _messages.value = it }
        }
    }

    fun sendMessage(chatId: String, senderId: String, receiverId: String, text: String) {
        repo.sendMessage(chatId, senderId, receiverId, text)
    }
}
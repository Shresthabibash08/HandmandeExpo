package com.example.handmadeexpo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.handmadeexpo.model.ChatMessage
import com.example.handmadeexpo.repo.ChatRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ChatViewModel(private val repository: ChatRepo) : ViewModel() {
    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages

    fun listenForMessages(chatId: String) {
        repository.getMessages(chatId) { newList ->
            _messages.value = newList
        }
    }

    fun sendMessage(chatId: String, senderId: String, receiverId: String, text: String) {
        if (text.isBlank()) return
        val msg = ChatMessage(senderId, receiverId, text)
        repository.sendMessage(chatId, msg)
    }
}

class ChatViewModelFactory(private val repository: ChatRepo) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ChatViewModel(repository) as T
    }
}
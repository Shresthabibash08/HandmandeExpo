package com.example.handmadeexpo.repo

import com.example.handmadeexpo.model.ChatMessage
import com.google.firebase.database.*

class ChatRepoImpl : ChatRepo {
    private val database = FirebaseDatabase.getInstance().getReference("chats")
    private val sellerInbox = FirebaseDatabase.getInstance().getReference("seller_inbox")

    override fun sendMessage(chatId: String, chatMessage: ChatMessage) {
        database.child(chatId).push().setValue(chatMessage)

        // Update seller inbox so buyer appears in their list
        val inboxEntry = mapOf(
            "buyerId" to chatMessage.senderId,
            "lastMessage" to chatMessage.message,
            "timestamp" to chatMessage.timestamp,
            "chatId" to chatId
        )
        sellerInbox.child(chatMessage.receiverId).child(chatId).setValue(inboxEntry)
    }

    // FIX: Match the exact signature required by your interface
    override fun getMessages(chatId: String, onUpdate: (List<ChatMessage>) -> Unit) {
        database.child(chatId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val messages = snapshot.children.mapNotNull { it.getValue(ChatMessage::class.java) }
                onUpdate(messages)
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }
}
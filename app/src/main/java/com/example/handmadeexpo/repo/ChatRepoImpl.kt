package com.example.handmadeexpo.repo

import com.example.handmadeexpo.model.ChatMessage
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class ChatRepoImpl {
    private val db = FirebaseDatabase.getInstance()

    fun sendMessage(chatId: String, senderId: String, receiverId: String, messageText: String) {
        val timestamp = System.currentTimeMillis()
        val messageData = ChatMessage(senderId, receiverId, messageText, timestamp)

        // 1. Save the actual message to the shared chat thread
        db.getReference("chats").child(chatId).child("messages").push().setValue(messageData)

        // 2. Prepare the Inbox Entry
        // This is what shows up in the "Messages" list (Last message, time, etc.)
        val inboxEntry = mapOf(
            "chatId" to chatId,
            "lastMessage" to messageText,
            "timestamp" to timestamp
        )

        // 3. UPDATE INBOXES (The "Dual Update" Logic)
        // We update BOTH the sender's and receiver's inbox so the list stays current for both.

        // Update Buyer Inbox folder
        // If sender is Buyer, participant is Receiver. If sender is Seller, participant is Sender.
        // To keep it simple, we save the "other person" as the participantId.

        val senderInbox = inboxEntry + ("participantId" to receiverId)
        val receiverInbox = inboxEntry + ("participantId" to senderId)

        // We check if the sender is a buyer or seller and update the correct folder
        // Logic: Try updating both; only the existing path in Firebase will reflect changes for that user
        db.getReference("buyer_inbox").child(senderId).child(chatId).setValue(senderInbox)
        db.getReference("seller_inbox").child(senderId).child(chatId).setValue(senderInbox)

        db.getReference("buyer_inbox").child(receiverId).child(chatId).setValue(receiverInbox)
        db.getReference("seller_inbox").child(receiverId).child(chatId).setValue(receiverInbox)
    }

    fun listenForMessages(chatId: String): Flow<List<ChatMessage>> = callbackFlow {
        val ref = db.getReference("chats").child(chatId).child("messages")
        val listener = ref.addValueEventListener(object : com.google.firebase.database.ValueEventListener {
            override fun onDataChange(snapshot: com.google.firebase.database.DataSnapshot) {
                // Sorting by timestamp ensures older messages are at the top
                val messages = snapshot.children.mapNotNull { it.getValue(ChatMessage::class.java) }
                    .sortedBy { it.timestamp }
                trySend(messages)
            }
            override fun onCancelled(error: com.google.firebase.database.DatabaseError) {
                close(error.toException())
            }
        })
        awaitClose { ref.removeEventListener(listener) }
    }
}
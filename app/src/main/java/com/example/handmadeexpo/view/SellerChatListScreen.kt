package com.example.handmadeexpo.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.handmadeexpo.repo.ChatRepoImpl
import com.google.firebase.database.*

@Composable
fun ChatListContent(sellerId: String, onChatSelected: (String, String, String) -> Unit) {
    val database = FirebaseDatabase.getInstance().getReference("seller_inbox").child(sellerId)
    var activeChats by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }

    LaunchedEffect(sellerId) {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = snapshot.children.mapNotNull { it.value as? Map<String, Any> }
                activeChats = list.sortedByDescending { it["timestamp"] as? Long ?: 0L }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(activeChats) { chat ->
            val bId = chat["participantId"].toString()
            val cId = chat["chatId"].toString()

            var buyerName by remember { mutableStateOf("Loading...") }
            LaunchedEffect(bId) {
                FirebaseDatabase.getInstance().getReference("buyers").child(bId).child("name")
                    .get().addOnSuccessListener { buyerName = it.value?.toString() ?: "Customer" }
            }

            ListItem(
                modifier = Modifier.clickable { onChatSelected(cId, bId, buyerName) },
                leadingContent = {
                    Box(Modifier.size(40.dp).background(Color.LightGray, CircleShape), Alignment.Center) {
                        Icon(Icons.Default.Person, null)
                    }
                },
                headlineContent = { Text(buyerName, fontWeight = FontWeight.Bold) },
                supportingContent = { Text(chat["lastMessage"].toString(), maxLines = 1) }
            )
        }
    }
}

@Composable
fun SellerReplyView(chatId: String, buyerId: String, sellerId: String) {
    val repo = remember { ChatRepoImpl() }

    // FIX: Variable initialized at the top level of the Composable
    var messageText by remember { mutableStateOf("") }
    val messages by repo.listenForMessages(chatId).collectAsState(initial = emptyList())

    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(modifier = Modifier.weight(1f).padding(8.dp)) {
            items(messages) { msg ->
                val isMe = msg.senderId == sellerId
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = if (isMe) Alignment.CenterEnd else Alignment.CenterStart) {
                    Surface(
                        color = if (isMe) Color(0xFFFFE0B2) else Color(0xFFEEEEEE),
                        shape = MaterialTheme.shapes.medium,
                        modifier = Modifier.padding(4.dp)
                    ) {
                        Text(msg.message, modifier = Modifier.padding(8.dp))
                    }
                }
            }
        }
        Row(Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
            TextField(
                value = messageText,
                onValueChange = { messageText = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Type a reply...") }
            )
            IconButton(onClick = {
                if (messageText.isNotBlank()) {
                    repo.sendMessage(chatId, sellerId, buyerId, messageText)
                    messageText = ""
                }
            }) { Icon(Icons.Default.Send, null, tint = Color(0xFFE65100)) }
        }
    }
}
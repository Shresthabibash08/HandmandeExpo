package com.example.handmadeexpo.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.firebase.database.*

@Composable
fun SellerChatListScreen(
    currentUserId: String,
    onChatClick: (String, String, String) -> Unit
) {
    val database = FirebaseDatabase.getInstance().getReference("seller_inbox").child(currentUserId)

    var activeChats by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    // 1. Fetch Chat List
    DisposableEffect(currentUserId) {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = snapshot.children.mapNotNull { it.value as? Map<String, Any> }
                activeChats = list.sortedByDescending { it["timestamp"] as? Long ?: 0L }
                isLoading = false
            }
            override fun onCancelled(error: DatabaseError) {
                isLoading = false
            }
        }
        database.addValueEventListener(listener)
        onDispose { database.removeEventListener(listener) }
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = OrangeBrand)
        }
    } else if (activeChats.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No messages yet", color = Color.Gray)
        }
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(activeChats) { chat ->
                val buyerId = chat["participantId"]?.toString() ?: ""
                val chatId = chat["chatId"]?.toString() ?: ""
                val lastMessage = chat["lastMessage"]?.toString() ?: "No message"

                // State for Buyer Name
                var buyerDisplayName by remember { mutableStateOf("Loading...") }

                // 2. Fetch 'buyerName' from 'Buyer' node
                DisposableEffect(buyerId) {
                    if (buyerId.isNotEmpty()) {
                        val ref = FirebaseDatabase.getInstance().getReference("Buyer").child(buyerId)

                        val nameListener = object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                // *** FIX: Look for 'buyerName' ***
                                val bName = snapshot.child("buyerName").value?.toString()

                                if (!bName.isNullOrEmpty()) {
                                    buyerDisplayName = bName
                                } else {
                                    // Fallback if buyerName is empty
                                    buyerDisplayName = snapshot.child("name").value?.toString() ?: "Customer"
                                }
                            }
                            override fun onCancelled(error: DatabaseError) {}
                        }
                        ref.addValueEventListener(nameListener)
                        onDispose { ref.removeEventListener(nameListener) }
                    } else {
                        onDispose { }
                    }
                }

                ListItem(
                    modifier = Modifier.clickable {
                        onChatClick(chatId, buyerId, buyerDisplayName)
                    },
                    leadingContent = {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(Color.LightGray, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = Color.White)
                        }
                    },
                    headlineContent = {
                        Text(text = buyerDisplayName, fontWeight = FontWeight.Bold)
                    },
                    supportingContent = {
                        Text(text = lastMessage, maxLines = 1, color = Color.Gray)
                    }
                )
                HorizontalDivider()
            }
        }
    }
}
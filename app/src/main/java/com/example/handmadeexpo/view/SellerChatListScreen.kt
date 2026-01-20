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
    currentUserId: String, // Matches the call in SellerDashboard
    onChatClick: (String, String, String) -> Unit // Matches the call in SellerDashboard
) {

    val database = FirebaseDatabase.getInstance().getReference("seller_inbox").child(currentUserId)

    var activeChats by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(currentUserId) {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = snapshot.children.mapNotNull { it.value as? Map<String, Any> }
                // Sort by timestamp descending (newest first)
                activeChats = list.sortedByDescending { it["timestamp"] as? Long ?: 0L }
                isLoading = false
            }
            override fun onCancelled(error: DatabaseError) {
                isLoading = false
            }
        }
        database.addValueEventListener(listener)

        // Cleanup listener when leaving screen is not strictly necessary for addValueEventListener
        // inside LaunchedEffect without a DisposableEffect, but good practice.
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else if (activeChats.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No messages yet", color = Color.Gray)
        }
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(activeChats) { chat ->
                // Safely extract data
                val buyerId = chat["participantId"]?.toString() ?: ""
                val chatId = chat["chatId"]?.toString() ?: ""
                val lastMessage = chat["lastMessage"]?.toString() ?: "No message"

                // Fetch Buyer Name State
                var buyerName by remember { mutableStateOf("Loading...") }

                LaunchedEffect(buyerId) {
                    if (buyerId.isNotEmpty()) {
                        FirebaseDatabase.getInstance().getReference("buyers").child(buyerId).child("name")
                            .get().addOnSuccessListener {
                                buyerName = it.value?.toString() ?: "Customer"
                            }
                    }
                }

                ListItem(
                    modifier = Modifier.clickable {
                        // Call the navigation callback
                        onChatClick(chatId, buyerId, buyerName)
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
                        Text(text = buyerName, fontWeight = FontWeight.Bold)
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
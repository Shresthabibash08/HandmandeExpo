package com.example.handmadeexpo.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.firebase.database.*

@Composable
fun BuyerChatListScreen(
    currentUserId: String, // Adjusted name for clarity
    onChatClick: (String, String, String) -> Unit
) {
    // 1. Reference to the buyer's inbox
    val database = FirebaseDatabase.getInstance().getReference("buyer_inbox").child(currentUserId)

    var activeChats by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    // 2. Fetch Chat List (Real-time listener for the inbox itself)
    DisposableEffect(currentUserId) {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = snapshot.children.mapNotNull { it.value as? Map<String, Any> }
                // Sort by newest timestamp first
                activeChats = list.sortedByDescending { it["timestamp"] as? Long ?: 0L }
                isLoading = false
            }
            override fun onCancelled(error: DatabaseError) {
                isLoading = false
            }
        }
        database.addValueEventListener(listener)

        onDispose {
            database.removeEventListener(listener)
        }
    }

    // 3. UI Content
    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Color(0xFFE65100))
        }
    } else if (activeChats.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No messages yet.", color = Color.Gray)
        }
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(activeChats) { chat ->
                // Safely extract data
                val sellerId = chat["participantId"]?.toString() ?: ""
                val chatId = chat["chatId"]?.toString() ?: ""
                val lastMessage = chat["lastMessage"]?.toString() ?: "No message"

                // State for Seller's Shop Name
                var shopName by remember { mutableStateOf("Loading...") }

                // 4. REAL-TIME LISTENER FOR SELLER NAME
                // This specifically looks into the "Seller" node for "shopName"
                DisposableEffect(sellerId) {
                    if (sellerId.isNotEmpty()) {
                        val db = FirebaseDatabase.getInstance()
                        val ref = db.getReference("Seller").child(sellerId)

                        val nameListener = object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                // *** FIX: Look for 'shopName' first ***
                                val fetchedName = snapshot.child("shopName").value?.toString()
                                    ?: snapshot.child("name").value?.toString() // Fallback

                                if (!fetchedName.isNullOrEmpty()) {
                                    shopName = fetchedName
                                } else {
                                    // Fallback if data exists but name is blank
                                    shopName = if (snapshot.exists()) "Seller" else "Unknown"
                                }
                            }
                            override fun onCancelled(error: DatabaseError) {
                                // Error handling
                            }
                        }
                        ref.addValueEventListener(nameListener)

                        onDispose { ref.removeEventListener(nameListener) }
                    } else {
                        onDispose { }
                    }
                }

                ListItem(
                    modifier = Modifier.clickable {
                        // Navigate passing the fetched shopName
                        onChatClick(chatId, sellerId, shopName)
                    },
                    leadingContent = {
                        Box(
                            modifier = Modifier
                                .size(45.dp)
                                .background(Color(0xFFF5F5F5), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            // Changed icon to Store for Sellers
                            Icon(Icons.Default.Store, contentDescription = null, tint = Color.Gray)
                        }
                    },
                    headlineContent = {
                        Text(shopName, fontWeight = FontWeight.Bold)
                    },
                    supportingContent = {
                        Text(lastMessage, maxLines = 1, color = Color.Gray)
                    }
                )
                HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray)
            }
        }
    }
}
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
fun BuyerChatListScreen(
    userId: String,
    onChatClick: (String, String, String) -> Unit
) {
    val database = remember(userId) {
        FirebaseDatabase.getInstance().getReference("buyer_inbox").child(userId)
    }

    var chattedSellers by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(userId) {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<Map<String, Any>>()
                snapshot.children.forEach { doc ->
                    (doc.value as? Map<String, Any>)?.let { list.add(it) }
                }
                chattedSellers = list.sortedByDescending { it["timestamp"] as? Long ?: 0L }
                isLoading = false
            }
            override fun onCancelled(error: DatabaseError) {
                isLoading = false
            }
        })
    }

    if (isLoading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Color(0xFFE65100))
        }
    } else if (chattedSellers.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No messages yet.", color = Color.Gray)
        }
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp)) {
            items(chattedSellers) { chat ->
                val sId = chat["participantId"].toString()
                val cId = chat["chatId"].toString()
                val lastMsg = chat["lastMessage"].toString()

                // Fetch real name from sellers node
                var sellerName by remember { mutableStateOf("Loading...") }
                LaunchedEffect(sId) {
                    FirebaseDatabase.getInstance().getReference("sellers").child(sId).child("name")
                        .get().addOnSuccessListener { snapshot ->
                            sellerName = snapshot.value?.toString() ?: "Unknown Seller"
                        }
                }

                ListItem(
                    modifier = Modifier.clickable { onChatClick(cId, sId, sellerName) },
                    leadingContent = {
                        Box(
                            Modifier
                                .size(45.dp)
                                .background(Color(0xFFF5F5F5), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Person, null, tint = Color.Gray)
                        }
                    },
                    headlineContent = { Text(sellerName, fontWeight = FontWeight.Bold) },
                    supportingContent = { Text(lastMsg, maxLines = 1, color = Color.Gray) }
                )
                HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray)
            }
        }
    }
}
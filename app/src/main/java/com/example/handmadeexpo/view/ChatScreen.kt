package com.example.handmadeexpo.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.handmadeexpo.repo.ChatRepoImpl
import com.example.handmadeexpo.viewmodel.ChatViewModel
import com.example.handmadeexpo.viewmodel.ChatViewModelFactory
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    chatId: String,
    sellerId: String,
    sellerName: String,
    currentUserId: String,
    onBackClick: () -> Unit,
    isReportingSeller: Boolean, // true = Buyer viewing Seller; false = Seller viewing Buyer
    onReportClick: () -> Unit
) {
    val viewModel: ChatViewModel = viewModel(factory = ChatViewModelFactory(ChatRepoImpl()))

    // Default to "Loading..." so we don't show "Unknown" prematurely
    var displayName by remember { mutableStateOf(if(sellerName == "Unknown" || sellerName == "Loading...") "Loading..." else sellerName) }

    var messageText by remember { mutableStateOf("") }
    val messages by viewModel.messages.collectAsState(initial = emptyList())

    LaunchedEffect(chatId) {
        viewModel.listenForMessages(chatId)
    }

    // *** FIX: Fetch correct field based on user type ***
    DisposableEffect(sellerId) {
        val database = FirebaseDatabase.getInstance()

        // 1. Determine correct Node and Field Name
        val nodeName = if (isReportingSeller) "Seller" else "Buyer"
        val targetField = if (isReportingSeller) "shopName" else "buyerName"

        val ref = database.getReference(nodeName).child(sellerId)

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Fetch the specific field: shopName OR buyerName
                val name = snapshot.child(targetField).value?.toString()

                if (!name.isNullOrEmpty()) {
                    displayName = name
                } else {
                    // Fallback: Check 'name' or 'username' just in case
                    val backupName = snapshot.child("name").value?.toString()
                        ?: snapshot.child("username").value?.toString()

                    if (!backupName.isNullOrEmpty()) displayName = backupName
                    else displayName = "User"
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        }

        ref.addValueEventListener(listener)
        onDispose { ref.removeEventListener(listener) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(displayName, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text(if (isReportingSeller) "Seller" else "Buyer", fontSize = 12.sp, color = Color.Gray)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onReportClick) {
                        Icon(Icons.Default.Warning, contentDescription = "Report User", tint = Color.Red)
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar(containerColor = Color.White, modifier = Modifier.imePadding()) {
                Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    TextField(
                        value = messageText,
                        onValueChange = { messageText = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Type a message...") },
                        colors = TextFieldDefaults.colors(focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent),
                        shape = RoundedCornerShape(24.dp)
                    )
                    IconButton(onClick = {
                        if (messageText.isNotBlank()) {
                            viewModel.sendMessage(chatId, currentUserId, sellerId, messageText)
                            messageText = ""
                        }
                    }) {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = Color(0xFFE65100))
                    }
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                items(messages) { msg ->
                    ChatBubble(text = msg.message, isMe = msg.senderId == currentUserId)
                }
            }
        }
    }
}

@Composable
fun ChatBubble(text: String, isMe: Boolean) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalAlignment = if (isMe) Alignment.End else Alignment.Start
    ) {
        Surface(
            color = if (isMe) Color(0xFFE65100) else Color(0xFFF1F1F1),
            shape = RoundedCornerShape(
                topStart = 12.dp, topEnd = 12.dp,
                bottomStart = if (isMe) 12.dp else 0.dp,
                bottomEnd = if (isMe) 0.dp else 12.dp
            )
        ) {
            Text(text = text, modifier = Modifier.padding(12.dp), color = if (isMe) Color.White else Color.Black, fontSize = 14.sp)
        }
    }
}
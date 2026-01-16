package com.example.handmadeexpo.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
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
    onBackClick: () -> Unit
) {
    // FIX: Pass ChatRepoImpl() to the Factory to solve the "No value passed for parameter 'repo'" error
    val viewModel: ChatViewModel = viewModel(
        factory = ChatViewModelFactory(ChatRepoImpl())
    )

    var messageText by remember { mutableStateOf("") }

    // Using collectAsState to sync with the Repository/ViewModel Flow
    val messages by viewModel.messages.collectAsState(initial = emptyList())

    // Start listening for messages when the screen opens
    LaunchedEffect(chatId) {
        viewModel.listenForMessages(chatId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(sellerName, fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = Color.White,
                modifier = Modifier.imePadding() // Ensures keyboard doesn't cover input
            ) {
                Row(
                    modifier = Modifier.padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = messageText,
                        onValueChange = { messageText = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Type a message...") },
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        shape = RoundedCornerShape(24.dp)
                    )
                    IconButton(onClick = {
                        if (messageText.isNotBlank()) {
                            viewModel.sendMessage(chatId, currentUserId, sellerId, messageText)
                            messageText = ""
                        }
                    }) {
                        Icon(
                            Icons.AutoMirrored.Filled.Send,
                            contentDescription = null,
                            tint = Color(0xFFE65100)
                        )
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            reverseLayout = false
        ) {
            items(messages) { msg ->
                val isMe = msg.senderId == currentUserId
                ChatBubble(text = msg.message, isMe = isMe)
            }
        }
    }
}

@Composable
fun ChatBubble(text: String, isMe: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
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
            Text(
                text = text,
                modifier = Modifier.padding(12.dp),
                color = if (isMe) Color.White else Color.Black,
                fontSize = 14.sp
            )
        }
    }
}
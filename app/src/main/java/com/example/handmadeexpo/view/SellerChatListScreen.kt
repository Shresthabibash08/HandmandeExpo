package com.example.handmadeexpo.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.handmadeexpo.model.ChatMessage
import com.example.handmadeexpo.repo.ChatRepoImpl
import com.example.handmadeexpo.viewmodel.ChatViewModel
import com.example.handmadeexpo.viewmodel.ChatViewModelFactory
import com.google.firebase.database.*

@Composable
fun SellerChatListScreen(sellerId: String) {
    var selectedChatData by remember { mutableStateOf<Map<String, String>?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        if (selectedChatData == null) {
            ChatListContent(
                sellerId = sellerId,
                onChatSelected = { chatId, buyerId ->
                    selectedChatData = mapOf("chatId" to chatId, "buyerId" to buyerId)
                }
            )
        } else {
            SellerReplyScreen(
                chatId = selectedChatData!!["chatId"]!!,
                buyerId = selectedChatData!!["buyerId"]!!,
                sellerId = sellerId,
                onBack = { selectedChatData = null }
            )
        }
    }
}

@Composable
fun ChatListContent(sellerId: String, onChatSelected: (String, String) -> Unit) {
    val database = FirebaseDatabase.getInstance().getReference("seller_inbox").child(sellerId)
    var activeChats by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(sellerId) {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<Map<String, Any>>()
                snapshot.children.forEach { doc ->
                    val data = doc.value as? Map<String, Any> // FIX: Safe casting
                    if (data != null) list.add(data)
                }
                activeChats = list.sortedByDescending { it["timestamp"] as? Long ?: 0L }
                isLoading = false
            }
            override fun onCancelled(error: DatabaseError) { isLoading = false }
        })
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Color(0xFFE65100))
        }
    } else {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Text("Customer Messages", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))
            LazyColumn {
                items(activeChats) { chat ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable { onChatSelected(chat["chatId"].toString(), chat["buyerId"].toString()) },
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFBFBFB))
                    ) {
                        ListItem(
                            leadingContent = {
                                Box(Modifier.size(40.dp).background(Color(0xFFE0E0E0), CircleShape), contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.Person, null)
                                }
                            },
                            headlineContent = { Text("Buyer ID: ${chat["buyerId"]}", fontWeight = FontWeight.SemiBold) },
                            supportingContent = { Text(chat["lastMessage"].toString(), maxLines = 1) }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellerReplyScreen(chatId: String, buyerId: String, sellerId: String, onBack: () -> Unit) {
    val chatViewModel: ChatViewModel = viewModel(factory = ChatViewModelFactory(ChatRepoImpl()))
    val messages by chatViewModel.messages.collectAsState()
    var replyText by remember { mutableStateOf("") }

    LaunchedEffect(chatId) { chatViewModel.listenForMessages(chatId) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chat with $buyerId", fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null) //
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar(containerColor = Color.White) {
                Row(modifier = Modifier.padding(8.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    TextField(
                        value = replyText,
                        onValueChange = { replyText = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Type a reply...") },
                        shape = RoundedCornerShape(24.dp), // Very round input field
                        colors = TextFieldDefaults.colors(focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent)
                    )
                    IconButton(onClick = {
                        if (replyText.isNotBlank()) {
                            chatViewModel.sendMessage(chatId, sellerId, buyerId, replyText)
                            replyText = ""
                        }
                    }) {
                        Icon(Icons.AutoMirrored.Filled.Send, null, tint = Color(0xFFE65100)) //
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding).fillMaxSize().padding(horizontal = 16.dp)) {
            items(messages) { msg ->
                ChatBubble(msg = msg, isCurrentUser = msg.senderId == sellerId)
            }
        }
    }
}

@Composable
fun ChatBubble(msg: ChatMessage, isCurrentUser: Boolean) {
    // --- DESIGN UPDATES FOR ROUNDER BUBBLES ---
    val bubbleShape = if (isCurrentUser) {
        // Seller reply: Round on all sides except bottom right
        RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp, bottomStart = 20.dp, bottomEnd = 4.dp)
    } else {
        // Buyer message: Round on all sides except bottom left
        RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp, bottomStart = 4.dp, bottomEnd = 20.dp)
    }

    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = if (isCurrentUser) Arrangement.End else Arrangement.Start
    ) {
        Surface(
            color = if (isCurrentUser) Color(0xFFE65100) else Color(0xFFF1F1F1),
            shape = bubbleShape, // FIX: Rounded reference applied here
            tonalElevation = 2.dp,
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Text(
                text = msg.message,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                color = if (isCurrentUser) Color.White else Color.Black,
                fontSize = 15.sp
            )
        }
    }
}
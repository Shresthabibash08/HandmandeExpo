package com.example.handmadeexpo.view

import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.handmadeexpo.repo.ChatRepoImpl
import com.example.handmadeexpo.viewmodel.ChatViewModel
import com.example.handmadeexpo.viewmodel.ChatViewModelFactory
import com.example.handmadeexpo.viewmodel.ReportViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    chatId: String,
    sellerId: String, // Note: If you are the Seller, this ID is the Buyer you are talking to.
    sellerName: String,
    currentUserId: String,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current

    // 1. ViewModels
    val viewModel: ChatViewModel = viewModel(
        factory = ChatViewModelFactory(ChatRepoImpl())
    )
    // We use the default factory for ReportViewModel since it has no params in your previous code
    val reportViewModel: ReportViewModel = viewModel()

    // 2. State
    var messageText by remember { mutableStateOf("") }
    val messages by viewModel.messages.collectAsState(initial = emptyList())

    // State to control the Report Popup
    var showReportDialog by remember { mutableStateOf(false) }

    LaunchedEffect(chatId) {
        viewModel.listenForMessages(chatId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(sellerName, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text("Buyer", fontSize = 12.sp, color = Color.Gray)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    // 3. Report Button Logic
                    IconButton(onClick = { showReportDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Report User",
                            tint = Color.Red
                        )
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = Color.White,
                modifier = Modifier.imePadding()
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
                            contentDescription = "Send",
                            tint = Color(0xFFE65100)
                        )
                    }
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                reverseLayout = false
            ) {
                items(messages) { msg ->
                    val isMe = msg.senderId == currentUserId
                    ChatBubble(text = msg.message, isMe = isMe)
                }
            }

            // 4. Report Dialog Implementation
            if (showReportDialog) {
                ReportDialog(
                    name = sellerName,
                    onDismiss = { showReportDialog = false },
                    onSubmit = { reason ->
                        // Call the reportBuyer function we created in ReportViewModel
                        reportViewModel.reportBuyer(sellerId, reason) { success, msg ->
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                            if (success) {
                                showReportDialog = false
                            }
                        }
                    }
                )
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

// 5. Reusable Dialog Component
@Composable
fun ReportDialog(name: String, onDismiss: () -> Unit, onSubmit: (String) -> Unit) {
    var reason by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Report $name") },
        text = {
            Column {
                Text("Why are you reporting this user?")
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = reason,
                    onValueChange = { reason = it },
                    placeholder = { Text("e.g. Harassment, Scam, Rude...") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { if (reason.isNotBlank()) onSubmit(reason) },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Text("Report")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
package com.example.handmadeexpo.view

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
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
    isReportingSeller: Boolean = true,
    onReportClick: (() -> Unit)? = null
) {
    val context = LocalContext.current

    val viewModel: ChatViewModel = viewModel(
        factory = ChatViewModelFactory(ChatRepoImpl())
    )
    val reportViewModel: ReportViewModel = viewModel()

    // Display name state with proper fallback
    var displayName by remember {
        mutableStateOf(
            if (sellerName == "Unknown" || sellerName == "Loading...") "Loading..."
            else sellerName
        )
    }

    var messageText by remember { mutableStateOf("") }
    val messages by viewModel.messages.collectAsState(initial = emptyList())
    var showReportDialog by remember { mutableStateOf(false) }

    LaunchedEffect(chatId) {
        viewModel.listenForMessages(chatId)
    }

    // Fetch correct name based on user type
    DisposableEffect(sellerId, isReportingSeller) {
        val database = FirebaseDatabase.getInstance()
        val nodeName = if (isReportingSeller) "sellers" else "buyers"
        val targetField = if (isReportingSeller) "shopName" else "buyerName"

        val ref = database.getReference(nodeName).child(sellerId)

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val name = snapshot.child(targetField).value?.toString()
                if (!name.isNullOrEmpty()) {
                    displayName = name
                } else {
                    val backupName = snapshot.child("name").value?.toString()
                        ?: snapshot.child("username").value?.toString()
                        ?: snapshot.child("fullName").value?.toString()
                    displayName = backupName ?: "User"
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        }

        ref.addValueEventListener(listener)
        onDispose { ref.removeEventListener(listener) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F7FA))
    ) {
        // Modern Header
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(4.dp, RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color(0xFFF5F5F5), CircleShape)
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF212121)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Text("Back", fontSize = 14.sp, color = Color(0xFF757575))
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(Color(0xFF1E88E5).copy(alpha = 0.15f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            if (isReportingSeller) Icons.Default.Store else Icons.Default.Person,
                            contentDescription = null,
                            tint = Color(0xFF1E88E5),
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            displayName,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF212121)
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                color = if (isReportingSeller) Color(0xFFFF9800).copy(alpha = 0.15f)
                                else Color(0xFF4CAF50).copy(alpha = 0.15f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    if (isReportingSeller) "Seller" else "Buyer",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = if (isReportingSeller) Color(0xFFFF9800) else Color(0xFF4CAF50),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                    IconButton(
                        onClick = {
                            if (onReportClick != null) {
                                onReportClick()
                            } else {
                                showReportDialog = true
                            }
                        },
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color(0xFFF44336).copy(alpha = 0.1f), CircleShape)
                    ) {
                        Icon(
                            Icons.Default.Flag,
                            contentDescription = "Report",
                            tint = Color(0xFFF44336),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        // Chat Messages
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            if (messages.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        Icons.Default.ChatBubbleOutline,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = Color.Gray.copy(alpha = 0.3f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Start the conversation",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Gray
                    )
                    Text(
                        "Send a message to $displayName",
                        fontSize = 14.sp,
                        color = Color.Gray.copy(alpha = 0.7f)
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(messages) { msg ->
                        ModernChatBubble(text = msg.message, isMe = msg.senderId == currentUserId)
                    }
                }
            }
        }

        // Message Input
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shadowElevation = 8.dp,
            color = Color.White
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .imePadding(),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(24.dp),
                    color = Color(0xFFF5F5F5)
                ) {
                    TextField(
                        value = messageText,
                        onValueChange = { messageText = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text("Type a message...", color = Color(0xFF9E9E9E), fontSize = 14.sp)
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFFF5F5F5),
                            unfocusedContainerColor = Color(0xFFF5F5F5),
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            cursorColor = Color(0xFF1E88E5)
                        ),
                        shape = RoundedCornerShape(24.dp),
                        maxLines = 4
                    )
                }

                IconButton(
                    onClick = {
                        if (messageText.isNotBlank()) {
                            viewModel.sendMessage(chatId, currentUserId, sellerId, messageText)
                            messageText = ""
                        }
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            if (messageText.isNotBlank()) Color(0xFF1E88E5) else Color(0xFFE0E0E0),
                            CircleShape
                        ),
                    enabled = messageText.isNotBlank()
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        tint = if (messageText.isNotBlank()) Color.White else Color(0xFFBDBDBD),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }

    // Built-in Report Dialog
    if (showReportDialog) {
        ModernReportDialog(
            name = displayName,
            userType = if (isReportingSeller) "Seller" else "Buyer",
            onDismiss = { showReportDialog = false },
            onSubmit = { reason ->
                if (isReportingSeller) {
                    reportViewModel.reportSeller(sellerId, reason) { success, msg ->
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                        if (success) showReportDialog = false
                    }
                } else {
                    reportViewModel.reportBuyer(sellerId, reason) { success, msg ->
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                        if (success) showReportDialog = false
                    }
                }
            }
        )
    }
}

@Composable
fun ModernChatBubble(text: String, isMe: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalAlignment = if (isMe) Alignment.End else Alignment.Start
    ) {
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
        ) {
            if (!isMe) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(Color(0xFF1E88E5).copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        tint = Color(0xFF1E88E5),
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
            }

            Card(
                modifier = Modifier.widthIn(max = 280.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isMe) Color(0xFF1E88E5) else Color.White
                ),
                shape = RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp,
                    bottomStart = if (isMe) 16.dp else 4.dp,
                    bottomEnd = if (isMe) 4.dp else 16.dp
                ),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Text(
                    text = text,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                    color = if (isMe) Color.White else Color(0xFF212121),
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            }

            if (isMe) {
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(Color(0xFF1E88E5).copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        tint = Color(0xFF1E88E5),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ModernReportDialog(
    name: String,
    userType: String = "User",
    onDismiss: () -> Unit,
    onSubmit: (String) -> Unit
) {
    var reason by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(Color(0xFFF44336).copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Flag,
                    contentDescription = null,
                    tint = Color(0xFFF44336),
                    modifier = Modifier.size(28.dp)
                )
            }
        },
        title = {
            Text(
                "Report $userType",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Color(0xFF212121)
            )
        },
        text = {
            Column {
                Text(
                    "Reporting: $name",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF424242)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Help us understand the problem. Your report is anonymous.",
                    fontSize = 13.sp,
                    color = Color(0xFF616161)
                )
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = reason,
                    onValueChange = { reason = it },
                    placeholder = {
                        Text("e.g., Harassment, Scam, Inappropriate behavior...", fontSize = 13.sp)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFF44336),
                        unfocusedBorderColor = Color(0xFFE0E0E0)
                    ),
                    minLines = 3,
                    maxLines = 5
                )

                Spacer(modifier = Modifier.height(12.dp))

                Surface(
                    color = Color(0xFFFFF3E0),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = null,
                            tint = Color(0xFFFF9800),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "False reports may result in account suspension",
                            fontSize = 11.sp,
                            color = Color(0xFFE65100)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { if (reason.isNotBlank()) onSubmit(reason) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336)),
                shape = RoundedCornerShape(12.dp),
                enabled = reason.isNotBlank()
            ) {
                Icon(Icons.Default.Send, null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Submit Report", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF757575))
            ) {
                Text("Cancel", fontWeight = FontWeight.Medium)
            }
        },
        shape = RoundedCornerShape(20.dp),
        containerColor = Color.White
    )
}
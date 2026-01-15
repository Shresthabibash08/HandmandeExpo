package com.example.handmadeexpo.view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Block
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.handmadeexpo.viewmodel.ReportViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

@Composable
fun WarningBanner(userId: String) {
    var hasWarnings by remember { mutableStateOf(false) }
    var warningCount by remember { mutableStateOf(0) }
    var latestWarning by remember { mutableStateOf("") }
    var showWarningDialog by remember { mutableStateOf(false) }
    var isBanned by remember { mutableStateOf(false) }
    var banExpiresAt by remember { mutableStateOf<Long?>(null) }

    val reportViewModel: ReportViewModel = viewModel()

    LaunchedEffect(userId) {
        // Check warnings
        val warningsRef = FirebaseDatabase.getInstance()
            .getReference("Warnings")
            .child(userId)

        warningsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val unreadWarnings = snapshot.children.filter {
                    it.child("isRead").getValue(Boolean::class.java) == false
                }

                warningCount = unreadWarnings.size
                hasWarnings = warningCount > 0

                if (hasWarnings) {
                    val latest = unreadWarnings.maxByOrNull {
                        it.child("timestamp").getValue(Long::class.java) ?: 0L
                    }
                    latestWarning = latest?.child("reason")?.getValue(String::class.java) ?: ""
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })

        // Check if banned
        reportViewModel.checkIfUserBanned(userId) { banned, expiryTime ->
            isBanned = banned
            banExpiresAt = expiryTime
        }
    }

    // Show ban warning if user is close to being banned (2+ warnings)
    if (warningCount >= 2 && !isBanned) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Block,
                    contentDescription = "Warning",
                    tint = Color(0xFFD32F2F),
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "⚠️ Final Warning - $warningCount warnings received",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color(0xFFD32F2F)
                    )
                    Text(
                        text = "One more warning will result in a temporary ban",
                        fontSize = 14.sp,
                        color = Color(0xFFD32F2F),
                        fontWeight = FontWeight.SemiBold
                    )
                }
                TextButton(onClick = { showWarningDialog = true }) {
                    Text("View", color = Color(0xFFD32F2F), fontWeight = FontWeight.Bold)
                }
            }
        }
    } else if (hasWarnings && !isBanned) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Warning",
                    tint = Color(0xFFFF6F00),
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "⚠️ You have $warningCount warning${if (warningCount > 1) "s" else ""}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color(0xFFFF6F00)
                    )
                    Text(
                        text = "Please review your behavior to avoid suspension",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
                TextButton(onClick = { showWarningDialog = true }) {
                    Text("View", color = Color(0xFFFF6F00))
                }
            }
        }
    }

    if (showWarningDialog) {
        AlertDialog(
            onDismissRequest = { showWarningDialog = false },
            icon = {
                Icon(
                    imageVector = if (warningCount >= 2) Icons.Default.Block else Icons.Default.Warning,
                    contentDescription = "Warning",
                    tint = Color(0xFFD32F2F),
                    modifier = Modifier.size(40.dp)
                )
            },
            title = {
                Text(
                    if (warningCount >= 2) "FINAL WARNING" else "Warning Notice",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        text = "You have received $warningCount warning${if (warningCount > 1) "s" else ""} from sellers:",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = latestWarning,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    if (warningCount >= 2) {
                        Text(
                            text = "⚠️ ONE MORE WARNING WILL RESULT IN A TEMPORARY BAN",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFD32F2F),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    Text(
                        text = "Ban Duration Based on Warnings:\n" +
                                "• 3 warnings = 7-day ban\n" +
                                "• 4 warnings = 14-day ban\n" +
                                "• 5+ warnings = 30-day ban",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        // Mark warnings as read
                        val warningsRef = FirebaseDatabase.getInstance()
                            .getReference("Warnings")
                            .child(userId)

                        warningsRef.get().addOnSuccessListener { snapshot ->
                            snapshot.children.forEach { warning ->
                                warning.ref.child("isRead").setValue(true)
                            }
                        }
                        showWarningDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFD32F2F)
                    )
                ) {
                    Text("I Understand")
                }
            }
        )
    }
}
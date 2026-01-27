package com.example.handmadeexpo.view

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
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
import com.example.handmadeexpo.ui.theme.MainColor
import com.example.handmadeexpo.viewmodel.ReportViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportProductScreen(
    productId: String,
    onBackClick: () -> Unit
) {
    val viewModel: ReportViewModel = viewModel()
    val context = LocalContext.current
    var reason by remember { mutableStateOf("") }
    var isSubmitting by remember { mutableStateOf(false) }

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
                // Back Button Row
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
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF212121)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        "Back",
                        fontSize = 14.sp,
                        color = Color(0xFF757575)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Title Row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
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
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            "Report Product",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF212121)
                        )
                        Text(
                            "Help us maintain quality",
                            fontSize = 13.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
        }

        // Scrollable Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Main Form Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(2.dp, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    // Info Banner
                    Surface(
                        color = Color(0xFFFFF3E0),
                        shape = RoundedCornerShape(12.dp),
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
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    "Your report is important",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFE65100)
                                )
                                Text(
                                    "Help us identify issues and maintain a safe marketplace",
                                    fontSize = 12.sp,
                                    color = Color(0xFF424242)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Question Text
                    Row(
                        verticalAlignment = Alignment.Top,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            Icons.Default.HelpOutline,
                            contentDescription = null,
                            tint = Color(0xFF757575),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                "What's wrong with this product?",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF212121)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "Please provide specific details so we can review and take appropriate action",
                                fontSize = 13.sp,
                                color = Color(0xFF616161),
                                lineHeight = 18.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Reason Text Field
                    Text(
                        "Describe the Issue",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF424242)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = reason,
                        onValueChange = { reason = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp),
                        placeholder = {
                            Text(
                                "e.g., Misleading description, Counterfeit item, Inappropriate content...",
                                fontSize = 13.sp,
                                color = Color(0xFF9E9E9E)
                            )
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFF44336),
                            unfocusedBorderColor = Color(0xFFE0E0E0),
                            focusedContainerColor = Color(0xFFFFF5F5),
                            unfocusedContainerColor = Color(0xFFFAFAFA)
                        ),
                        maxLines = 6
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Character hint
                    Text(
                        "${reason.length} characters",
                        fontSize = 11.sp,
                        color = Color(0xFF9E9E9E)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Common Issues
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.List,
                                    contentDescription = null,
                                    tint = Color(0xFF757575),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "Common Issues",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF424242)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            CommonIssueChip("Counterfeit or fake")
                            CommonIssueChip("Misleading information")
                            CommonIssueChip("Inappropriate content")
                            CommonIssueChip("Safety concerns")
                            CommonIssueChip("Poor quality")
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Warning Banner
                    Surface(
                        color = Color(0xFFFFEBEE),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Warning,
                                contentDescription = null,
                                tint = Color(0xFFF44336),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                "False reports may result in account penalties",
                                fontSize = 11.sp,
                                color = Color(0xFFC62828),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = onBackClick,
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color(0xFF757575)
                            )
                        ) {
                            Text("Cancel", fontWeight = FontWeight.Medium, fontSize = 15.sp)
                        }

                        Button(
                            onClick = {
                                if (reason.isNotBlank()) {
                                    isSubmitting = true
                                    viewModel.submitReport(productId, reason) { success: Boolean, msg: String ->
                                        isSubmitting = false
                                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                        if (success) {
                                            onBackClick()
                                        }
                                    }
                                } else {
                                    Toast.makeText(context, "Please describe the issue", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp),
                            enabled = !isSubmitting && reason.isNotBlank(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFF44336),
                                disabledContainerColor = Color(0xFFE0E0E0)
                            )
                        ) {
                            if (isSubmitting) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(
                                    Icons.Default.Send,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Submit", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun CommonIssueChip(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .background(Color(0xFF9E9E9E), CircleShape)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text,
            fontSize = 12.sp,
            color = Color(0xFF616161)
        )
    }
}
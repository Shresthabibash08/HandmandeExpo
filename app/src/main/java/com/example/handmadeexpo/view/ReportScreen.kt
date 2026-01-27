package com.example.handmadeexpo.view

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.example.handmadeexpo.viewmodel.ReportViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen(
    reportTargetId: String,
    isReportingSeller: Boolean, // True = Report Seller, False = Report Buyer
    onBackClick: () -> Unit
) {
    val viewModel: ReportViewModel = viewModel()
    val context = LocalContext.current
    var reason by remember { mutableStateOf("") }
    var isSubmitting by remember { mutableStateOf(false) }

    // Dynamic Text based on who we are reporting
    val titleText = if (isReportingSeller) "Report Seller" else "Report Buyer"
    val reasonLabel = if (isReportingSeller) "Reason (e.g. Scam, Fake Items)" else "Reason (e.g. Harassment, Spam)"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(titleText, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .background(Color.White),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Why are you reporting this user?",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Your report helps us keep the marketplace safe. Please be specific.",
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = reason,
                onValueChange = { reason = it },
                label = { Text(reasonLabel) },
                modifier = Modifier.fillMaxWidth().height(150.dp),
                maxLines = 5,
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.height(32.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(
                    onClick = onBackClick,
                    modifier = Modifier.weight(1f).height(50.dp),
                    shape = RoundedCornerShape(12.dp)
                ) { Text("Cancel", color = Color.Gray) }

                Button(
                    onClick = {
                        if (reason.isNotBlank()) {
                            isSubmitting = true
                            val callback: (Boolean, String) -> Unit = { success, msg ->
                                isSubmitting = false
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                if (success) onBackClick()
                            }
                            // Logic to call correct ViewModel function
                            if (isReportingSeller) viewModel.reportSeller(reportTargetId, reason, callback)
                            else viewModel.reportBuyer(reportTargetId, reason, callback)
                        } else {
                            Toast.makeText(context, "Please write a reason", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.weight(1f).height(50.dp),
                    enabled = !isSubmitting,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
                ) {
                    if (isSubmitting) CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                    else Text("Submit Report")
                }
            }
        }
    }
}
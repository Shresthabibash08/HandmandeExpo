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
import androidx.compose.ui.unit.dp
import com.google.firebase.database.FirebaseDatabase

@Composable
fun AllSellersListScreen(
    currentUserId: String,
    onSellerSelected: (String, String, String) -> Unit
) {
    val sellersRef = remember { FirebaseDatabase.getInstance().getReference("sellers") }
    var sellerList by remember { mutableStateOf<List<Map<String, String>>>(emptyList()) }

    LaunchedEffect(Unit) {
        sellersRef.get().addOnSuccessListener { snapshot ->
            val list = mutableListOf<Map<String, String>>()
            snapshot.children.forEach { child ->
                val name = child.child("name").value.toString()
                val id = child.key.toString()
                if (id != currentUserId) list.add(mapOf("name" to name, "id" to id))
            }
            sellerList = list
        }
    }

    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        items(sellerList) { seller ->
            val sId = seller["id"]!!
            val sName = seller["name"]!!

            Row(modifier = Modifier.fillMaxWidth().clickable {
                val chatId = ChatUtils.generateChatId(currentUserId, sId)
                onSellerSelected(chatId, sId, sName)
            }.padding(12.dp)) {
                Box(Modifier.size(40.dp).background(Color.Gray, CircleShape))
                Spacer(Modifier.width(12.dp))
                Text(sName)
            }
        }
    }
}
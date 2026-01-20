package com.example.handmadeexpo.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.handmadeexpo.R
import com.example.handmadeexpo.ui.theme.MainColor
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

data class SellerInfo(
    val sellerId: String = "",
    val shopName: String = "",
    val fullName: String = "",
    val sellerEmail: String = "",
    val sellerPhoneNumber: String = "",
    val sellerAddress: String = "",
    val panNumber: String = "",
    val documentUrl: String = "",
    val verificationStatus: String = "Unverified",
    val totalProducts: Int = 0,
    val totalSales: Int = 0,
    val rating: Double = 0.0
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellerDetailsScreen(
    sellerId: String,
    onBackClick: () -> Unit,
    onContactClick: () -> Unit = {}
) {
    val context = LocalContext.current
    var sellerInfo by remember { mutableStateOf<SellerInfo?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    // Fetch seller details
    LaunchedEffect(sellerId) {
        val sellerRef = FirebaseDatabase.getInstance().getReference("Seller").child(sellerId)

        sellerRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val seller = SellerInfo(
                        sellerId = sellerId,
                        shopName = snapshot.child("shopName").getValue(String::class.java) ?: "Unknown Shop",
                        fullName = snapshot.child("fullName").getValue(String::class.java) ?: "",
                        sellerEmail = snapshot.child("sellerEmail").getValue(String::class.java) ?: "",
                        sellerPhoneNumber = snapshot.child("sellerPhoneNumber").getValue(String::class.java) ?: "",
                        sellerAddress = snapshot.child("sellerAddress").getValue(String::class.java) ?: "",
                        panNumber = snapshot.child("panNumber").getValue(String::class.java) ?: "",
                        documentUrl = snapshot.child("documentUrl").getValue(String::class.java) ?: "",
                        verificationStatus = snapshot.child("verificationStatus").getValue(String::class.java) ?: "Unverified"
                    )

                    // Count seller's products
                    FirebaseDatabase.getInstance().getReference("products")
                        .orderByChild("sellerId")
                        .equalTo(sellerId)
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(productsSnapshot: DataSnapshot) {
                                val productCount = productsSnapshot.childrenCount.toInt()
                                var totalSold = 0

                                productsSnapshot.children.forEach { productSnapshot ->
                                    val sold = productSnapshot.child("sold").getValue(Int::class.java) ?: 0
                                    totalSold += sold
                                }

                                sellerInfo = seller.copy(
                                    totalProducts = productCount,
                                    totalSales = totalSold
                                )
                                isLoading = false
                            }

                            override fun onCancelled(error: DatabaseError) {
                                sellerInfo = seller
                                isLoading = false
                            }
                        })
                } else {
                    error = "Seller not found"
                    isLoading = false
                }
            }

            override fun onCancelled(dbError: DatabaseError) {
                error = dbError.message
                isLoading = false
            }
        })
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Seller Details", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MainColor,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Background
            Image(
                painter = painterResource(R.drawable.bg10),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = MainColor)
                    }
                }

                error != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.Error,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = Color.Gray
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                error ?: "Unknown error",
                                color = Color.Gray,
                                fontSize = 16.sp
                            )
                        }
                    }
                }

                sellerInfo != null -> {
                    SellerDetailsContent(
                        seller = sellerInfo!!,
                        onContactClick = onContactClick
                    )
                }
            }
        }
    }
}

@Composable
fun SellerDetailsContent(
    seller: SellerInfo,
    onContactClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Profile Picture and Shop Name
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Profile Image
                Box(
                    contentAlignment = Alignment.BottomEnd
                ) {
                    Image(
                        painter = painterResource(R.drawable.profilephoto),
                        contentDescription = "Profile Photo",
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(Color.LightGray),
                        contentScale = ContentScale.Crop
                    )

                    // Verification Badge
                    if (seller.verificationStatus == "Verified") {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFF4CAF50),
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = "Verified",
                                tint = Color.White,
                                modifier = Modifier.padding(4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Shop Name
                Text(
                    text = seller.shopName,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MainColor
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Verification Status
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = when (seller.verificationStatus) {
                        "Verified" -> Color(0xFF4CAF50).copy(alpha = 0.1f)
                        "Pending" -> Color(0xFFFF9800).copy(alpha = 0.1f)
                        else -> Color.Gray.copy(alpha = 0.1f)
                    }
                ) {
                    Text(
                        text = seller.verificationStatus,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        color = when (seller.verificationStatus) {
                            "Verified" -> Color(0xFF4CAF50)
                            "Pending" -> Color(0xFFFF9800)
                            else -> Color.Gray
                        },
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Statistics Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Inventory,
                label = "Products",
                value = seller.totalProducts.toString()
            )

            StatCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.ShoppingBag,
                label = "Sales",
                value = seller.totalSales.toString()
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Seller Information Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    "Seller Information",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MainColor
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (seller.fullName.isNotEmpty()) {
                    InfoRow(
                        icon = Icons.Default.Person,
                        label = "Owner",
                        value = seller.fullName
                    )
                }

                if (seller.sellerEmail.isNotEmpty()) {
                    InfoRow(
                        icon = Icons.Default.Email,
                        label = "Email",
                        value = seller.sellerEmail
                    )
                }

                if (seller.sellerPhoneNumber.isNotEmpty()) {
                    InfoRow(
                        icon = Icons.Default.Phone,
                        label = "Phone",
                        value = seller.sellerPhoneNumber
                    )
                }

                if (seller.sellerAddress.isNotEmpty()) {
                    InfoRow(
                        icon = Icons.Default.LocationOn,
                        label = "Address",
                        value = seller.sellerAddress
                    )
                }

                if (seller.panNumber.isNotEmpty()) {
                    InfoRow(
                        icon = Icons.Default.Badge,
                        label = "PAN Number",
                        value = seller.panNumber
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Contact Button
        Button(
            onClick = onContactClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MainColor)
        ) {
            Icon(
                Icons.Default.Chat,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                "Contact Seller",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun StatCard(
    modifier: Modifier = Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = MainColor,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                value,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MainColor
            )
            Text(
                label,
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun InfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = MainColor,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    label,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    value,
                    fontSize = 15.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Normal
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f))
    }
}
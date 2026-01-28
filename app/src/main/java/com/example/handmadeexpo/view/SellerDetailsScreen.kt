package com.example.handmadeexpo.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.handmadeexpo.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

// --- Modern Design Constants ---
private val AppBackground = Color(0xFFF5F7FA)
private val PrimaryGreen = Color(0xFF4CAF50)
private val TextDark = Color(0xFF212121)
private val TextGray = Color(0xFF757575)

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

@Composable
fun SellerDetailsScreen(
    sellerId: String,
    onBackClick: () -> Unit,
    onContactClick: () -> Unit = {}
) {
    var sellerInfo by remember { mutableStateOf<SellerInfo?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    // Fetch seller details logic (Unchanged)
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
    ) {
        when {
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = PrimaryGreen)
                }
            }

            error != null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.ErrorOutline,
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
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = onBackClick, colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)) {
                            Text("Go Back")
                        }
                    }
                }
            }

            sellerInfo != null -> {
                val seller = sellerInfo!!

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    // --- 1. Header Section (Overlapping) ---
                    Box(modifier = Modifier.fillMaxWidth()) {
                        // Green Gradient Banner
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(PrimaryGreen, Color(0xFF43A047))
                                    ),
                                    shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
                                )
                        ) {
                            // Custom Back Button in Header
                            IconButton(
                                onClick = onBackClick,
                                modifier = Modifier
                                    .padding(top = 40.dp, start = 8.dp)
                                    .align(Alignment.TopStart)
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    tint = Color.White
                                )
                            }

                            Text(
                                "Seller Details",
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier
                                    .align(Alignment.TopCenter)
                                    .padding(top = 52.dp)
                            )
                        }

                        // Floating Profile Content
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 100.dp), // Push down to overlap
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Profile Image
                            Card(
                                shape = CircleShape,
                                elevation = CardDefaults.cardElevation(8.dp),
                                modifier = Modifier.size(120.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White)
                            ) {
                                Image(
                                    painter = painterResource(R.drawable.profilephoto),
                                    contentDescription = "Profile Photo",
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(4.dp)
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Shop Name & Badge
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = seller.shopName,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextDark
                                )
                                if (seller.verificationStatus == "Verified") {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(
                                        Icons.Default.Verified,
                                        contentDescription = "Verified",
                                        tint = PrimaryGreen,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }

                            // Verification Status Text
                            Spacer(modifier = Modifier.height(4.dp))
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = when (seller.verificationStatus) {
                                    "Verified" -> PrimaryGreen.copy(alpha = 0.1f)
                                    "Pending" -> Color(0xFFFF9800).copy(alpha = 0.1f)
                                    else -> Color.Gray.copy(alpha = 0.1f)
                                }
                            ) {
                                Text(
                                    text = seller.verificationStatus,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                    color = when (seller.verificationStatus) {
                                        "Verified" -> PrimaryGreen
                                        "Pending" -> Color(0xFFFF9800)
                                        else -> Color.Gray
                                    },
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // --- 2. Statistics Row ---
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        ModernStatCard(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Default.Inventory,
                            label = "Total Products",
                            value = seller.totalProducts.toString(),
                            accentColor = Color(0xFF2196F3)
                        )

                        ModernStatCard(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Default.ShoppingBag,
                            label = "Total Sales",
                            value = seller.totalSales.toString(),
                            accentColor = Color(0xFFFF9800)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // --- 3. Information Card ---
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .shadow(2.dp, RoundedCornerShape(16.dp)),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text(
                                "Contact Information",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextDark,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )

                            if (seller.fullName.isNotEmpty()) {
                                ModernInfoRow(Icons.Default.Person, "Owner Name", seller.fullName)
                            }
                            if (seller.sellerEmail.isNotEmpty()) {
                                ModernInfoRow(Icons.Default.Email, "Email Address", seller.sellerEmail)
                            }
                            if (seller.sellerPhoneNumber.isNotEmpty()) {
                                ModernInfoRow(Icons.Default.Phone, "Phone Number", seller.sellerPhoneNumber)
                            }
                            if (seller.sellerAddress.isNotEmpty()) {
                                ModernInfoRow(Icons.Default.LocationOn, "Shop Address", seller.sellerAddress)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // --- 4. Action Button ---
                    Button(
                        onClick = onContactClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .height(56.dp)
                            .shadow(4.dp, RoundedCornerShape(12.dp)),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
                    ) {
                        Icon(Icons.Default.Chat, contentDescription = null)
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
        }
    }
}

@Composable
fun ModernStatCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    label: String,
    value: String,
    accentColor: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(accentColor.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = value,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )
            Text(
                text = label,
                fontSize = 12.sp,
                color = TextGray,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun ModernInfoRow(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon Box
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(PrimaryGreen.copy(alpha = 0.1f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = PrimaryGreen,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Text Content
        Column {
            Text(
                text = label,
                fontSize = 12.sp,
                color = TextGray,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = value,
                fontSize = 15.sp,
                color = TextDark,
                fontWeight = FontWeight.Normal
            )
        }
    }
    // Subtle Divider
    Divider(
        color = Color.Gray.copy(alpha = 0.1f),
        thickness = 1.dp,
        modifier = Modifier.padding(start = 56.dp)
    )
}
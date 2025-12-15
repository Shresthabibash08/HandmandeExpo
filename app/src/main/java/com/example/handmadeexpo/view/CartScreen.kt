package com.example.handmadeexpo.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.handmadeexpo.R
import com.example.handmadeexpo.ui.theme.LightPurple
import androidx.compose.material3.Text


// ✅ Cart Item Model
data class CartItem(
    val name: String,
    val price: Double,
    var quantity: Int,
    val imageRes: Int
)

// ✅ Product Model for Grid
data class Product(val image: Int, val label: String)

@Composable
fun CartScreen() {

    var cartItems by remember {
        mutableStateOf(
            listOf(
                CartItem("Budha Statue", 25000.0, 1, R.drawable.budha),
                CartItem("Singing Bowl", 15000.0, 2, R.drawable.bowl),
                CartItem("Wooden Mask", 5000.0, 1, R.drawable.mask)
            )
        )
    }

    val listData = listOf(
        Product(R.drawable.bowl, "Singing Bowl"),
        Product(R.drawable.budha, "Budhha Statue"),
        Product(R.drawable.decore, "Wall Decore"),
        Product(R.drawable.thanka, "Thanka"),
        Product(R.drawable.mask, "Wooden Mask"),
        Product(R.drawable.box, "Wooden Box"),
        Product(R.drawable.music, "Dhyangro"),
        Product(R.drawable.painting, "Oil Painting"),
    )

    val total = cartItems.sumOf { it.price * it.quantity }

    Scaffold(
        bottomBar = { BottomBar(total) },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)   // <-- THIS LINE REMOVES TOP WHITE SPACE
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            Image(
                painter = painterResource(R.drawable.bg4),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // … (rest of your LazyColumn content unchanged)
                item {
                    Text(
                        text = "My Cart",
                        fontWeight = FontWeight.Bold,
                        fontSize = 35.sp,
                        color = LightPurple,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }
                items(cartItems) { item -> CartItemCard(item) }
                item {
                    Divider(Modifier.padding(vertical = 8.dp))
                    Text(text = "Flash Sale", fontWeight = FontWeight.Bold, fontSize = 25.sp)
                    Text(text = "Limited Stock Available!")
                }
                item {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.height(400.dp).fillMaxWidth()
                    ) {
                        items(listData.size) { index ->
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(8.dp)
                            ) {
                                Image(
                                    painter = painterResource(listData[index].image),
                                    contentDescription = null,
                                    modifier = Modifier.size(150.dp),
                                    contentScale = ContentScale.Crop
                                )
                                Text(
                                    text = listData[index].label,
                                    modifier = Modifier.padding(top = 6.dp),
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ✅ Cart Item Card
@Composable
fun CartItemCard(item: CartItem) {
    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xAAFFFFFF))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Image(
                painter = painterResource(item.imageRes),
                contentDescription = item.name,
                modifier = Modifier
                    .size(64.dp)
                    .padding(end = 16.dp),
                contentScale = ContentScale.Crop
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(text = item.name, fontWeight = FontWeight.Bold)
                Text(text = "Rs ${item.price}")
            }

            Text(text = "Qty: ${item.quantity}")
        }
    }
}

// ✅ Bottom bar (fixed)
@Composable
fun BottomBar(total: Double) {
    Surface(
        shadowElevation = 8.dp,
        color = Color.White
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Text(
                text = "Total: Rs ${"%.2f".format(total)}",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = LightPurple
            )

            Button(
                onClick = {},
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Checkout",
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CartPreview() {
    CartScreen()
}
package com.example.handmadeexpo.view

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.handmadeexpo.R
import com.example.handmadeexpo.repo.BuyerProfileRepoImpl
import com.example.handmadeexpo.ui.theme.Blue12
import com.example.handmadeexpo.ui.theme.MainColor
import com.example.handmadeexpo.ui.theme.White12
import com.example.handmadeexpo.viewmodel.BuyerProfileViewModel


//
class DashboardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DashboardBody()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardBody() {
    val context = LocalContext.current
    val activity = context as Activity

    data class NavItem(val icon: Int, val label: String)

    val listItems = listOf(
        NavItem(icon = R.drawable.outline_home_24, label = "Home"),
        NavItem(icon = R.drawable.outline_search_24, label = "Search"),
        NavItem(icon = R.drawable.baseline_shopping_cart_24, label = "Cart"),
        NavItem(icon = R.drawable.outline_contacts_product_24, label = "Profile"),
    )

    var selectedIndex by remember { mutableStateOf(0) }

    val profileRepo = remember { BuyerProfileRepoImpl() }
    val profileViewModel = remember { BuyerProfileViewModel(profileRepo) }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    titleContentColor = White12,
                    actionIconContentColor = White12,
                    navigationIconContentColor = White12,
                    containerColor = MainColor
                ),
                navigationIcon = {
                    IconButton(onClick = {}) {
                        Icon(
                            painter = painterResource(R.drawable.outline_arrow_back_ios_24),
                            contentDescription = null
                        )
                    }
                },
                title = {
                    Text(
                        "Handmade Expo",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(
                            painter = painterResource(R.drawable.outline_search_24),
                            contentDescription = null
                        )
                    }
                    IconButton(onClick = {}) {
                        Icon(
                            painter = painterResource(R.drawable.outline_more_horiz_24),
                            contentDescription = null
                        )
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                listItems.forEachIndexed { index, item ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                painter = painterResource(item.icon),
                                contentDescription = null
                            )
                        },
                        label = {
                            Text(item.label)
                        },
                        onClick = {
                            selectedIndex = index
                        },
                        selected = selectedIndex == index
                    )
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (selectedIndex) {
                0 -> HomeScreen()
                1 -> Text("Search Screen - Coming Soon!")
                2 -> CartScreen()
                3 -> BuyerProfileScreen(profileViewModel)
                else -> HomeScreen()
            }
        }
    }
}

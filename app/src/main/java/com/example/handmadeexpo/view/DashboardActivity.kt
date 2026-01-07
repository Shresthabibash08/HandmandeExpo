package com.example.handmadeexpo.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.handmadeexpo.R
import com.example.handmadeexpo.repo.BuyerRepoImpl
import com.example.handmadeexpo.ui.theme.MainColor
import com.example.handmadeexpo.ui.theme.White12
import com.example.handmadeexpo.viewmodel.BuyerViewModel

// TODO: IMPORT YOUR LOGIN ACTIVITY HERE
// Example: import com.example.handmadeexpo.view.LoginActivity
// Example: import com.example.handmadeexpo.view.BuyerLoginActivity
// Example: import com.example.handmadeexpo.view.AuthActivity

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

    data class NavItem(val icon: Int, val label: String)

    val listItems = listOf(
        NavItem(R.drawable.outline_home_24, "Home"),
        NavItem(R.drawable.outline_search_24, "Search"),
        NavItem(R.drawable.baseline_shopping_cart_24, "Cart"),
        NavItem(R.drawable.outline_contacts_product_24, "Profile")
    )

    var selectedIndex by remember { mutableStateOf(0) }
    var editing by remember { mutableStateOf(false) }
    var changingPassword by remember { mutableStateOf(false) }

    val repo = remember { BuyerRepoImpl() }
    val viewModel = remember { BuyerViewModel(repo) }

    val context = LocalContext.current
    val activity = context as? Activity

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MainColor,
                    titleContentColor = White12
                ),
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
                        selected = selectedIndex == index,
                        onClick = {
                            selectedIndex = index
                            editing = false
                            changingPassword = false
                        },
                        icon = {
                            Icon(
                                painter = painterResource(item.icon),
                                contentDescription = null
                            )
                        },
                        label = { Text(item.label) }
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
                1 -> Text("Search Screen")
                2 -> CartScreen()
                3 -> {
                    when {
                        changingPassword -> {
                            ChangePasswordScreen(
                                viewModel = viewModel,
                                onBackClick = { changingPassword = false },
                                onPasswordChanged = { changingPassword = false }
                            )
                        }
                        editing -> {
                            EditBuyerProfileScreen(
                                viewModel = viewModel,
                                onBack = { editing = false }
                            )
                        }
                        else -> {
                            BuyerProfileScreen(
                                viewModel = viewModel,
                                onEditClick = { editing = true },
                                onChangePasswordClick = { changingPassword = true },
                                onLogoutSuccess = {
                                    // TODO: REPLACE "LoginActivity" WITH YOUR ACTUAL LOGIN ACTIVITY NAME
                                    // Common names: LoginActivity, BuyerLoginActivity, AuthActivity, SignInActivity

                                    val intent = Intent(context, SignInActivity::class.java)

                                    // These flags clear the back stack so user can't go back after logout
                                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

                                    context.startActivity(intent)
                                    activity?.finish()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
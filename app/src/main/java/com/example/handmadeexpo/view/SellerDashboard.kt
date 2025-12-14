package com.example.handmadeexpo.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.colorspace.WhitePoint
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.handmadeexpo.R
import com.example.handmadeexpo.view.ui.theme.HandmadeExpoTheme
import com.example.handmadeexpo.view.ui.theme.SearchScreen

class SellerDashboard : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SellerDashboardBody()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellerDashboardBody(){
    data class NavItem(val icon:Int,val label:String)
    val listItems=listOf(
        NavItem(icon=R.drawable.baseline_home_24,label="Home"),
        NavItem(icon=R.drawable.baseline_search_24,label="Search"),
        NavItem(icon=R.drawable.baseline_settings_24,label="Setting")
    )
    var selectedIndex by remember{ mutableStateOf(0) }

    Scaffold(topBar = {
        CenterAlignedTopAppBar(
            colors= TopAppBarDefaults.topAppBarColors(
                titleContentColor = Color.White,
                actionIconContentColor = Color.White,
                navigationIconContentColor = Color.White,
                containerColor = Color.Blue
            ),
            navigationIcon={
                IconButton(onClick = {}) {
                    Icon(
                        painter= painterResource(R.drawable.outline_arrow_back_ios_24),
                        contentDescription = null
                    )
                }
            },
            title={
                Text("HandMade Expo")
            },
            actions = {
                IconButton(onClick = {}) {
                    Icon(
                        painter = painterResource(R.drawable.outline_arrow_back_ios_24),
                        contentDescription = null
                    )
                }
                IconButton(onClick = {}) {
                    Icon(
                        painter = painterResource(R.drawable.outline_arrow_back_ios_24),
                        contentDescription = null
                    )
                }
            }
        )
    },
        bottomBar = {
            NavigationBar {
                listItems.forEachIndexed { index,item ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                            painter=painterResource(item.icon),
                            contentDescription = null
                            )
                        },
                        label= {
                            Text(item.label)
                        },
                        onClick = {
                            selectedIndex=index
                        },
                        selected = selectedIndex == index
                    )
                }
            }
        }
    ){ padding ->
        Column(modifier=Modifier.fillMaxSize()
            .padding(padding)){
            when(selectedIndex){
                0 -> HomeScreen()
                1 -> SearchScreen()
                2 -> SellerProfileScreen()
            }
        }

    }
}

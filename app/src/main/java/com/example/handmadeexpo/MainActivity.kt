package com.example.handmadeexpo

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import com.example.handmadeexpo.view.SplashActivity

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = Intent(this, SplashActivity::class.java)
        startActivity(intent)
        finish()
    }
}
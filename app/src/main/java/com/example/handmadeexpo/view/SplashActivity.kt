package com.example.handmadeexpo.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.handmadeexpo.R
import kotlinx.coroutines.delay

class SplashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SplashBody()
        }
    }
}

@Composable
fun SplashBody() {
    val context = LocalContext.current
    val activity = context as Activity
    var startAnimation by remember { mutableStateOf(false) }

    // --- ANIMATIONS ---
    val logoOffsetY by animateDpAsState(
        targetValue = if (startAnimation) 0.dp else (-150).dp,
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
        label = "logoOffset"
    )

    val logoRotation by animateFloatAsState(
        targetValue = if (startAnimation) 360f else 0f,
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
        label = "logoRotate"
    )

    val textOffsetY by animateDpAsState(
        targetValue = if (startAnimation) 0.dp else 150.dp,
        animationSpec = tween(durationMillis = 1000, delayMillis = 100, easing = FastOutSlowInEasing),
        label = "textOffset"
    )

    val commonAlpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 1000),
        label = "alpha"
    )

    LaunchedEffect(Unit) {
        startAnimation = true
        delay(3000)
        val intent = Intent(context, SignInActivity::class.java)
        context.startActivity(intent)
        activity.finish()
    }

    Scaffold { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Background Image
            Image(
                painter = painterResource(id = R.drawable.bg1),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Content Column
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(y = (-50).dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // --- LOGO ---
                Image(
                    painter = painterResource(id = R.drawable.finallogo),
                    contentDescription = "App Logo",
                    modifier = Modifier
                        .size(130.dp)
                        .offset(y = logoOffsetY)
                        .rotate(logoRotation)
                        .alpha(commonAlpha)
                )

                Spacer(modifier = Modifier.height(20.dp)) // Adjusted space for Pacifico

                // --- TEXT WITH PACIFICO FONT ---
                Text(
                    text = "Handmade Expo",
                    style = TextStyle(
                        // Loading the custom font here
                        fontFamily = FontFamily(Font(R.font.badscript)),
                        fontSize = 36.sp, // Pacifico looks good slightly larger
                        color = Color.Black,
                        textAlign = TextAlign.Center,
                        // Soft white shadow for readability
                        shadow = Shadow(
                            color = Color.White.copy(alpha = 0.5f),
                            offset = Offset(2f, 2f),
                            blurRadius = 8f
                        )
                    ),
                    modifier = Modifier
                        .offset(y = textOffsetY)
                        .alpha(commonAlpha)
                )
            }
        }
    }
}
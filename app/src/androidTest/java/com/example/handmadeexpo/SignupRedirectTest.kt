package com.example.handmadeexpo

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import com.example.handmadeexpo.view.SignInActivity
import com.example.handmadeexpo.view.SignupActivity
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SignupRedirectTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<SignInActivity>()

    @Before
    fun setup() {
        Intents.init()
    }

    @After
    fun tearDown() {
        Intents.release()
    }

    @Test
    fun testSignupToSignInRedirect() {
        composeRule.onNodeWithTag("signup").performClick()
        composeRule.waitForIdle()
        Intents.intended(hasComponent(SignupActivity::class.java.name))
    }
}

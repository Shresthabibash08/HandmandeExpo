package com.example.handmadeexpo

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import com.example.handmadeexpo.view.ForgetPasswordActivity
import com.example.handmadeexpo.view.SignInActivity
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ForgotPasswordNavigationTest {

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
    fun testNavigateToForgotPassword() {
        composeRule.onNodeWithTag("forgetpassword").performClick()
        composeRule.waitForIdle()
        Intents.intended(hasComponent(ForgetPasswordActivity::class.java.name))
    }
}

package com.example.handmadeexpo

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import com.example.handmadeexpo.view.DashboardActivity
import com.example.handmadeexpo.view.SignInActivity
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginToDashboardTest {

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
    fun testSuccessfulLogin_navigatesToDashboard() {
        composeRule.onNodeWithTag("email").performTextInput("user1@gmail.com")
        composeRule.onNodeWithTag("password").performTextInput("abc123")
        composeRule.onNodeWithTag("singin").performClick()

        composeRule.waitUntil(timeoutMillis = 5000) {
            try {
                composeRule.onNodeWithTag("dashboard").assertExists()
                true
            } catch (e: AssertionError) {
                false
            }
        }

        Intents.intended(hasComponent(DashboardActivity::class.java.name))
    }
}

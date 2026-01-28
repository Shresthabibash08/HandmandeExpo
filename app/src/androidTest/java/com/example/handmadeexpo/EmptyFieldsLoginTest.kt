package com.example.handmadeexpo

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.handmadeexpo.view.SignInActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EmptyFieldsLoginTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<SignInActivity>()

    @Test
    fun testLoginFailsWithEmptyFields() {
        composeRule.onNodeWithTag("singin").performClick()
        composeRule.onNodeWithTag("singin").assertIsDisplayed()
    }
}

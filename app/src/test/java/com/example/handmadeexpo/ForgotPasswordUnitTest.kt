package com.example.handmadeexpo

import com.example.handmadeexpo.repo.BuyerRepo
import com.example.handmadeexpo.viewmodel.BuyerViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class ForgotPasswordUnitTest {

    @Test
    fun forgotPassword_success_test() {
        val repo = mock<BuyerRepo>()
        val viewModel = BuyerViewModel(repo)

        val testEmail = "manav12@gmail.com"

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String) -> Unit>(1)
            callback(true, "Reset link is sent to $testEmail")
            null
        }.`when`(repo).forgotPassword(eq(testEmail), any())

        var successResult = false
        var messageResult = ""

        viewModel.forgotPassword(testEmail) { success, msg ->
            successResult = success
            messageResult = msg
        }

        assertTrue(successResult)
        assertEquals("Reset link is sent to $testEmail", messageResult)
        verify(repo).forgotPassword(eq(testEmail), any())
    }
}
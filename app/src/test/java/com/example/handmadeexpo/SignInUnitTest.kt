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

class SignInUnitTest {

    @Test
    fun login_success_test() {
        val repo = mock<BuyerRepo>()
        val viewModel = BuyerViewModel(repo)

        val testEmail = "manav12@gmail.com"
        val testPassword = "manav@1234"

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String) -> Unit>(2)
            callback(true, "Login Successfully")
            null
        }.`when`(repo).login(eq(testEmail), eq(testPassword), any())


        var successResult = false
        var messageResult = ""


        viewModel.login(testEmail, testPassword) { success, msg ->
            successResult = success
            messageResult = msg
        }
        assertTrue("Expected successResult to be true", successResult)
        assertEquals("Login Successfully", messageResult)
        verify(repo).login(eq(testEmail), eq(testPassword), any())
    }
}
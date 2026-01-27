package com.example.handmadeexpo

import com.example.handmadeexpo.model.SellerModel
import com.example.handmadeexpo.repo.SellerRepo
import com.example.handmadeexpo.viewmodel.SellerViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.kotlin.*

class SellerRegistrationUnitTest {

    @Test
    fun seller_registration_success_test() {

        val repo = mock<SellerRepo>()
        val viewModel = SellerViewModel(repo)


        val testEmail = "manav12@gmail.com"
        val testPassword = "manav@1234"
        val testSellerId = "seller_unique_id_123"


        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String, String) -> Unit>(2)
            callback(true, "Auth Success", testSellerId)
            null
        }.`when`(repo).register(eq(testEmail), eq(testPassword), any())


        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String) -> Unit>(2)
            callback(true, "Registration Successfully")
            null
        }.`when`(repo).addSellerToDatabase(eq(testSellerId), any(), any())


        var finalSuccess = false
        var messageResult = ""


        viewModel.register(testEmail, testPassword) { success, msg, sellerId ->
            if (success) {

                val sellerModel = SellerModel(
                    sellerId = sellerId,
                    fullName = "Manav",
                    sellerEmail = testEmail
                )

                viewModel.addSellerToDatabase(sellerId, sellerModel) { dbSuccess, dbMsg ->
                    finalSuccess = dbSuccess
                    messageResult = dbMsg
                }
            }
        }

        assertTrue("Expected finalSuccess to be true", finalSuccess)
        assertEquals("Registration Successfully", messageResult)


        verify(repo).register(eq(testEmail), eq(testPassword), any())
        verify(repo).addSellerToDatabase(eq(testSellerId), any(), any())
    }
}
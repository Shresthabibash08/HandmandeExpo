package com.example.handmadeexpo

import android.content.Context
import android.net.Uri
import com.example.handmadeexpo.repo.SellerRepo
import com.example.handmadeexpo.viewmodel.SellerViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.kotlin.*

class SellerVerificationUnitTest {

    @Test
    fun seller_verification_success_test() {
        val repo = mock<SellerRepo>()
        val viewModel = SellerViewModel(repo)

        val mockContext = mock<Context>()
        val mockUri = mock<Uri>()
        val testUserId = "WMbhJGE6DhcBl5NnyoimBnYDvfK2"
        val testImageUrl = "https://res.cloudinary.com/dj3k1ik5u/image/doc.jpg"
        val testUpdates = mapOf(
            "documentType" to "National ID",
            "documentUrl" to testImageUrl,
            "verificationStatus" to "Pending"
        )

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String) -> Unit>(2)
            callback(true, testImageUrl)
            null
        }.`when`(repo).uploadImage(eq(mockContext), eq(mockUri), any())

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String) -> Unit>(2)
            callback(true, "Profile Updated Successfully")
            null
        }.`when`(repo).updateProfileFields(eq(testUserId), eq(testUpdates), any())

        var finalSuccess = false
        var finalMessage = ""

        viewModel.uploadImage(mockContext, mockUri) { uploadSuccess, imageUrl ->
            if (uploadSuccess) {
                viewModel.updateProfileFields(testUserId, testUpdates) { profileSuccess, msg ->
                    finalSuccess = profileSuccess
                    finalMessage = msg
                }
            }
        }

        assertTrue(finalSuccess)
        assertEquals("Profile Updated Successfully", finalMessage)

        verify(repo).uploadImage(eq(mockContext), eq(mockUri), any())
        verify(repo).updateProfileFields(eq(testUserId), eq(testUpdates), any())
    }
}
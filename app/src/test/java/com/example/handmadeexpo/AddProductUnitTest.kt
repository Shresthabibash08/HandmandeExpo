package com.example.handmadeexpo

import android.content.Context
import android.net.Uri
import com.example.handmadeexpo.model.ProductModel
import com.example.handmadeexpo.repo.ProductRepo
import com.example.handmadeexpo.viewmodel.ProductViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.kotlin.*

class AddProductUnitTest {

    @Test
    fun addProduct_success_test() {
        val repo = mock<ProductRepo>()

        doNothing().`when`(repo).getAllProduct(any())

        val viewModel = ProductViewModel(repo)
        val mockUri = mock<Uri>()
        val mockContext = mock<Context>()

        val testProduct = ProductModel(
            productId = "prod123",
            name = "Skin Soul Product",
            price = 1500.0,
            description = "Handcrafted artisan product",
            image = "https://res.cloudinary.com/dj3k1ik5u/image/dummy.jpg",
            categoryId = "Others",
            stock = 10,
            sellerId = "WMbhJGE6DhcBl5NnyoimBnYDvfK2"
        )

        doAnswer { invocation ->
            val callback = invocation.getArgument<(String?) -> Unit>(2)
            callback("https://res.cloudinary.com/dj3k1ik5u/image/dummy.jpg")
            null
        }.`when`(repo).uploadImage(eq(mockContext), eq(mockUri), any())

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String, String?) -> Unit>(1)
            callback(true, "Product Added Successfully", "prod123")
            null
        }.`when`(repo).addProduct(any(), any())

        var finalSuccess = false
        var messageResult = ""

        viewModel.uploadImage(mockContext, mockUri) { imageUrl ->
            if (imageUrl != null) {
                viewModel.addProduct(testProduct) { success, msg, _ ->
                    finalSuccess = success
                    messageResult = msg
                }
            }
        }

        assertTrue(finalSuccess)
        assertEquals("Product Added Successfully", messageResult)

        verify(repo).uploadImage(eq(mockContext), eq(mockUri), any())
        verify(repo).addProduct(any(), any())
    }
}
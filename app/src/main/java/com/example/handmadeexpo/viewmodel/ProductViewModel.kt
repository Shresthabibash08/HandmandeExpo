package com.example.handmadeexpo.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import com.example.handmadeexpo.model.ProductModel
import com.example.handmadeexpo.repo.ProductRepo
import org.tensorflow.lite.support.label.Category

class ProductViewModel(private val repo: ProductRepo) : ViewModel() {

    private val _products = MutableLiveData<ProductModel?>()
    val products: MutableLiveData<ProductModel?>
        get() = _products
    private val _allProducts = MutableLiveData<List<ProductModel>>(emptyList())
    val allProducts: MutableLiveData<List<ProductModel>> = _allProducts

    private val _loading = MutableLiveData<Boolean>()
    val loading: MutableLiveData<Boolean>
        get() = _loading


    fun addProduct(model: ProductModel, callback: (Boolean, String, String?) -> Unit) {
        repo.addProduct(model, callback)
    }

    fun updateProduct(productId:String,model: ProductModel, callback: (Boolean, String) -> Unit) {
        repo.updateProduct(productId,model, callback)
    }

    fun deleteProduct(productID: String, callback: (Boolean, String) -> Unit) {
        repo.deleteProduct(productID, callback)
    }


    private val _allProductsCategory = MutableLiveData<List<ProductModel>>(emptyList())
    val allProductsCategory: MutableLiveData<List<ProductModel>> get() = _allProductsCategory

    fun getProductById(productID: String) {
        repo.getProductById(productID) { success, msg, data ->
            if (success) {
                _products.postValue(data)
            }
        }
    }

    fun getAllProduct() {
        repo.getAllProduct { success, msg, data ->
            if (success) {
                // Ensure we never post null
                _allProducts.postValue(data ?: emptyList())
            } else {
                _allProducts.postValue(emptyList())
            }
        }
    }

    fun getProductByCategory(categoryId: String) {
        repo.getProductByCategory(categoryId) { success, msg, data ->
            if (success) {
                _allProductsCategory.postValue(data ?: emptyList())
            } else {
                _allProductsCategory.postValue(emptyList())
            }
        }
    }

    fun uploadImage(context: Context, imageUri: Uri, callback: (String?) -> Unit) {
        repo.uploadImage(context, imageUri, callback)
    }

    private val _sellerProducts = MutableLiveData<List<ProductModel>>()
    val sellerProducts: LiveData<List<ProductModel>> = _sellerProducts

    fun getProductsBySeller(sellerId: String) {
        repo.getProductsBySeller(sellerId) {
            _sellerProducts.postValue(it)
        }
    }
}

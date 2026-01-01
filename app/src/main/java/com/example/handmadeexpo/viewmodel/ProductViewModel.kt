package com.example.handmadeexpo.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.content.Context
import android.net.Uri
import com.example.handmadeexpo.model.ProductModel
import com.example.handmadeexpo.repo.ProductRepo
import org.tensorflow.lite.support.label.Category

class ProductViewModel(val repo: ProductRepo) : ViewModel() {

    private val _products = MutableLiveData<ProductModel?>()
    val products: MutableLiveData<ProductModel?>
        get() = _products
    private val _allProducts = MutableLiveData<List<ProductModel>>(emptyList())
    val allProducts : MutableLiveData<List<ProductModel>> = _allProducts

    private val _loading= MutableLiveData<Boolean>()
    val loading: MutableLiveData<Boolean>
        get()=_loading

    private val _flaggedProducts= MutableLiveData<List<ProductModel>>(emptyList())
    val flaggedProducts: MutableLiveData<List<ProductModel>> =_flaggedProducts
    fun addProduct(model: ProductModel, callback: (Boolean, String,String?) -> Unit) {
        repo.addProduct(model, callback)
    }

    fun updateProduct(model: ProductModel, callback: (Boolean, String) -> Unit) {
        repo.updateProduct(model, callback)
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
    fun getAllProductByCategory(category: String,callback: (Boolean, String,List<ProductModel>?) -> Unit){
        _loading.postValue(true)
        repo.getAllProductByCategory(category){
                success,msg,data ->
            if(success){
                _allProducts.postValue((data?: emptyList()) as List<ProductModel>?)
            }
            else{
                _allProducts.postValue(emptyList())
            }
            _loading.postValue(false)
        }
    }

}

package com.example.handmadeexpo.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.handmadeexpo.model.ProductModel
import com.example.handmadeexpo.repo.ProductRepo

class ProductViewModel(private val repo: ProductRepo) : ViewModel() {

    // --- State Management ---
    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    private val _products = MutableLiveData<ProductModel?>()
    val products: LiveData<ProductModel?> get() = _products

    private val _allProducts = MutableLiveData<List<ProductModel>>(emptyList())
    val allProducts: LiveData<List<ProductModel>> = _allProducts

    private val _allProductsCategory = MutableLiveData<List<ProductModel>>(emptyList())
    val allProductsCategory: LiveData<List<ProductModel>> get() = _allProductsCategory

    private val _sellerProducts = MutableLiveData<List<ProductModel>>()
    val sellerProducts: LiveData<List<ProductModel>> = _sellerProducts

    // --- Filtering State (from Development Branch) ---
    private val _filteredProducts = MutableLiveData<List<ProductModel>>()
    val filteredProducts: LiveData<List<ProductModel>> get() = _filteredProducts

    private val _sliderValue = MutableLiveData(100f)
    val sliderValue: LiveData<Float> get() = _sliderValue

    private val _maxPriceDisplay = MutableLiveData(100000.0)
    val maxPriceDisplay: LiveData<Double> get() = _maxPriceDisplay

    private val ABSOLUTE_MAX = 100000.0

    init {
        getAllProduct()
    }

    // --- CRUD Operations ---

    fun addProduct(model: ProductModel, callback: (Boolean, String, String?) -> Unit) {
        repo.addProduct(model, callback)
    }

    fun updateProduct(productId: String, model: ProductModel, callback: (Boolean, String) -> Unit) {
        repo.updateProduct(productId, model, callback)
    }

    fun deleteProduct(productID: String, callback: (Boolean, String) -> Unit) {
        repo.deleteProduct(productID, callback)
    }

    fun uploadImage(context: Context, imageUri: Uri, callback: (String?) -> Unit) {
        repo.uploadImage(context, imageUri, callback)
    }

    // --- Data Fetching Logic ---

    fun getProductById(productID: String) {
        repo.getProductById(productID) { success, _, data ->
            if (success) _products.postValue(data)
        }
    }

    fun getAllProduct() {
        _loading.postValue(true)
        repo.getAllProduct { success, _, data ->
            _loading.postValue(false)
            if (success && data != null) {
                _allProducts.postValue(data)
                _filteredProducts.postValue(data)
                // Reset slider to max when data is refreshed
                _sliderValue.postValue(100f)
                _maxPriceDisplay.postValue(ABSOLUTE_MAX)
            } else {
                _allProducts.postValue(emptyList())
                _filteredProducts.postValue(emptyList())
            }
        }
    }

    fun getProductByCategory(categoryId: String) {
        repo.getProductByCategory(categoryId) { success, _, data ->
            _allProductsCategory.postValue(if (success) data ?: emptyList() else emptyList())
        }
    }

    fun getProductsBySeller(sellerId: String) {
        repo.getProductsBySeller(sellerId) { data ->
            _sellerProducts.postValue(data)
        }
    }

    // --- UI Logic: Filtering & Sliders ---

    fun onSliderChange(value: Float) {
        _sliderValue.value = value
        val calculatedLimit = (value / 100) * ABSOLUTE_MAX
        _maxPriceDisplay.value = calculatedLimit
        filterList(calculatedLimit)
    }

    fun onCategorySelect(price: Double) {
        _maxPriceDisplay.value = price
        filterList(price)
        val newSliderPos = (price / ABSOLUTE_MAX) * 100f
        _sliderValue.value = newSliderPos.toFloat()
    }

    private fun filterList(limit: Double) {
        val currentList = _allProducts.value ?: emptyList()
        val filtered = currentList.filter { it.price <= limit }
        _filteredProducts.value = filtered
    }
}
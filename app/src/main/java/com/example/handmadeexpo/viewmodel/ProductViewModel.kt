package com.example.handmadeexpo.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.handmadeexpo.model.ProductModel
import com.example.handmadeexpo.repo.ProductRepo

class ProductViewModel(val repo: ProductRepo) : ViewModel() {

    // --- REPO FUNCTIONS ---
    fun addProduct(model: ProductModel, callback: (Boolean, String) -> Unit) = repo.addProduct(model, callback)
    fun updateProduct(model: ProductModel, callback: (Boolean, String) -> Unit) = repo.updateProduct(model, callback)
    fun deleteProduct(productID: String, callback: (Boolean, String) -> Unit) = repo.deleteProduct(productID, callback)
    fun uploadImage(context: Context, imageUri: Uri, callback: (String?) -> Unit) = repo.uploadImage(context, imageUri, callback)

    // --- DATA HOLDERS ---
    private val _allProducts = MutableLiveData<List<ProductModel>?>()
    val allProducts: MutableLiveData<List<ProductModel>?> get() = _allProducts

    // Filtered list (This is what the UI observes)
    private val _filteredProducts = MutableLiveData<List<ProductModel>>()
    val filteredProducts: LiveData<List<ProductModel>> get() = _filteredProducts

    // Slider State (0f to 100f)
    private val _sliderValue = MutableLiveData(100f)
    val sliderValue: LiveData<Float> get() = _sliderValue

    // Display Price (e.g. 50000.0)
    private val _maxPriceDisplay = MutableLiveData(100000.0)
    val maxPriceDisplay: LiveData<Double> get() = _maxPriceDisplay

    private val ABSOLUTE_MAX = 100000.0

    init {
        getAllProduct()
    }

    // --- FETCH DATA ---
    fun getAllProduct() {
        repo.getAllProduct { success, msg, data ->
            if (success) {
                _allProducts.postValue(data)

                // Initially show everything
                if (!data.isNullOrEmpty()) {
                    _filteredProducts.postValue(data)
                    _sliderValue.postValue(100f)
                    _maxPriceDisplay.postValue(ABSOLUTE_MAX)
                } else {
                    _filteredProducts.postValue(emptyList())
                }
            }
        }
    }

    // --- LOGIC 1: SLIDER MOVED ---
    fun onSliderChange(value: Float) {
        _sliderValue.value = value

        // Calculate price from percentage
        val percentage = value / 100
        val calculatedLimit = percentage * ABSOLUTE_MAX

        _maxPriceDisplay.value = calculatedLimit
        filterList(calculatedLimit)
    }

    // --- LOGIC 2: CATEGORY CLICKED ---
    fun onCategorySelect(price: Double) {
        // Update price display
        _maxPriceDisplay.value = price
        filterList(price)

        // Move slider to correct position
        // Formula: (Price / Max) * 100
        val newSliderPos = (price / ABSOLUTE_MAX) * 100f
        _sliderValue.value = newSliderPos.toFloat()
    }

    private fun filterList(limit: Double) {
        val currentList = _allProducts.value ?: emptyList()
        val filtered = currentList.filter { product ->
            product.price <= limit
        }
        _filteredProducts.value = filtered
    }

    // ... Keep other fetch methods ...
    private val _products = MutableLiveData<ProductModel?>()
    val products: MutableLiveData<ProductModel?> get() = _products

    fun getProductById(productID: String) {
        repo.getProductById(productID) { success, msg, data ->
            if (success) _products.postValue(data)
        }
    }

    private val _allProductsCategory = MutableLiveData<List<ProductModel>?>()
    val allProductsCategory: MutableLiveData<List<ProductModel>?> get() = _allProductsCategory

    fun getProductByCategory(categoryId: String) {
        repo.getProductByCategory(categoryId) { success, msg, data ->
            if (success) _allProductsCategory.postValue(data)
        }
    }
}
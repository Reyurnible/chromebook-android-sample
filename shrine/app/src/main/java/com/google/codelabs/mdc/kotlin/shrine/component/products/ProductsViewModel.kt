package com.google.codelabs.mdc.kotlin.shrine.component.products

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.codelabs.mdc.kotlin.shrine.network.ProductEntry
import com.google.codelabs.mdc.kotlin.shrine.network.ProductRepository

class ProductsViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: ProductRepository = ProductRepository(application)

    val productList: MutableLiveData<List<ProductEntry>> by lazy {
        MutableLiveData<List<ProductEntry>>()
    }
    val cartProductList: MutableLiveData<MutableList<ProductEntry>> by lazy {
        MutableLiveData<MutableList<ProductEntry>>().apply {
            value = mutableListOf()
        }
    }

    init {
        productList.value = repository.fetchList()
    }

    fun onClickAddCart(product: ProductEntry) {
        val oldList = cartProductList.value ?: mutableListOf()
        cartProductList.value = oldList.apply { add(product) }
    }
}

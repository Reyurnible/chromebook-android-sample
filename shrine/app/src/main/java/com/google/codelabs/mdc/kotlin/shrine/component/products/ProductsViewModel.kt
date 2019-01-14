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

    init {
        productList.value = repository.fetchList()
    }

}

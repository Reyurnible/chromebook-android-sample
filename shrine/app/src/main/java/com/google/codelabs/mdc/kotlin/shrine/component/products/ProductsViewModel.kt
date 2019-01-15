package com.google.codelabs.mdc.kotlin.shrine.component.products

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.codelabs.mdc.kotlin.shrine.network.ProductEntry
import com.google.codelabs.mdc.kotlin.shrine.network.ProductRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo

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
    val isLoading: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>().apply {
            value = false
        }
    }
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    init {
        repository.fetchList()
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                isLoading.value = true
            }
            .doOnEvent { _, _ ->
                isLoading.value = false
            }
            .subscribe({
                productList.value = it
            }, {

            })
            .addTo(compositeDisposable)
    }

    override fun onCleared() {
        compositeDisposable.clear()
        super.onCleared()
    }

    fun onClickAddCart(product: ProductEntry) {
        val oldList = cartProductList.value ?: mutableListOf()
        cartProductList.value = oldList.apply { add(product) }
    }
}

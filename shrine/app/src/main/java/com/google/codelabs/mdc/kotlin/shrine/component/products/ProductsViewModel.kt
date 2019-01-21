package com.google.codelabs.mdc.kotlin.shrine.component.products

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.codelabs.mdc.kotlin.shrine.network.ProductEntry
import com.google.codelabs.mdc.kotlin.shrine.network.ProductRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import java.util.ArrayDeque

class ProductsViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: ProductRepository = ProductRepository(application)
    private var undoStack = ArrayDeque<StackActions>()
    private var redoStack = ArrayDeque<StackActions>()

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
        oldList.add(product)
        cartProductList.value = oldList
        undoStack.add(StackActions.AddCart(product))
    }

    fun onUndoKeyShortcut() {
        undoStack.poll()?.let { lastAction ->
            redoStack.push(lastAction)
            when (lastAction) {
                is StackActions.AddCart -> {
                    val oldList = cartProductList.value ?: mutableListOf()
                    oldList.remove(lastAction.product)
                    cartProductList.value = oldList
                }
                else -> {
                    Log.d("OptimizedChromeOS", "Error on Ctrl-z: Unknown Action")
                }
            }
        }
    }

    fun onRedoKeyShortcut() {
        redoStack.poll()?.let { prevAction ->
                undoStack.push(prevAction)
                when (prevAction) {
                    is StackActions.AddCart -> {
                        val oldList = cartProductList.value ?: mutableListOf()
                        oldList.add(prevAction.product)
                        cartProductList.value = oldList
                    }
                    else -> {
                        Log.d("OptimizedChromeOS", "Error on Ctrl-Shift-z: Unknown Action")
                    }
                }
            }
    }

    private sealed class StackActions {
        data class AddCart(val product: ProductEntry) : StackActions()
    }
}

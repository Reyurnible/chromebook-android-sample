package com.google.example.resizecodelab.view

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.view.View
import com.google.example.resizecodelab.model.AppData
import com.google.example.resizecodelab.model.DataProvider

class MainViewModel : ViewModel() {

    private val internalProductName = MutableLiveData<String>()
    private val internalIsDescriptionExpanded = MutableLiveData<Boolean>()
    private val internalAppData = MutableLiveData<AppData>()
    private val reviewProvider = DataProvider()

    val productName: LiveData<String>
        get() = internalProductName

    fun setProductName(newName: String) {
        internalProductName.value = newName
    }

    val isDescriptionExpanded: LiveData<Boolean>
        get() = internalIsDescriptionExpanded

    fun setDescriptionExpanded(newState: Boolean) {
        internalIsDescriptionExpanded.value = newState
    }

    val appData: LiveData<AppData>
        get() = internalAppData

    init {
        reviewProvider.fetchData(object : DataProvider.Listener {
            override fun onSuccess(appData: AppData) {
                internalAppData.postValue(appData)
            }
        })
    }

}
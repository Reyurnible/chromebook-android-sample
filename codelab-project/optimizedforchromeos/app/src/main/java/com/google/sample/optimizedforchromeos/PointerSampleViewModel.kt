package com.google.sample.optimizedforchromeos

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.view.PointerIcon

class PointerSampleViewModel(application: Application) : AndroidViewModel(application) {

    val arrowTypeList: MutableLiveData<List<Pair<String, PointerIcon>>> = MutableLiveData()

    init {
        val newValue = mutableListOf<Pair<String, PointerIcon>>()
        SystemPointerType
            .values()
            .map { it.name to PointerIcon.getSystemIcon(application, it.value) }
            .let(newValue::addAll)
        newValue.add("custom" to PointerIcon.load(application.resources, R.xml.custom_pointer))
        arrowTypeList.postValue(newValue)
    }
}

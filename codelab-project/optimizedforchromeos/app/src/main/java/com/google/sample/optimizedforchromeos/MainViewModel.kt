package com.google.sample.optimizedforchromeos

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.MutableLiveData
import java.util.*


class MainViewModel : ViewModel() {
    val messagesSent: MutableLiveData<Int> =
            MutableLiveData<Int>().apply {
                value = 0
            }
    val dinosClicked: MutableLiveData<Int> =
            MutableLiveData<Int>().apply {
                value = 0
            }
    val dropText: MutableLiveData<String> =
            MutableLiveData<String>().apply {
                value = "Drop Things Here!"
            }

    val undoStack: ArrayDeque<Int> = ArrayDeque()
    val redoStack: ArrayDeque<Int> = ArrayDeque()

    fun incrementMessageSent() {
        messagesSent.value = (messagesSent.value ?: 0) + 1
    }

    fun decrementMessageSent() {
        messagesSent.value = (messagesSent.value ?: 0) - 1
    }

    fun incrementDinosClicked() {
        dinosClicked.value = (dinosClicked.value ?: 0) + 1
    }

    fun decrementDinosClicked() {
        dinosClicked.value = (dinosClicked.value ?: 0) - 1
    }
}
package com.google.codelabs.mdc.kotlin.shrine.component.login

import android.util.Log
import android.view.KeyEvent
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LoginViewModel : ViewModel() {
    val userNameValidationError: MutableLiveData<UserNameValidationError> by lazy {
        MutableLiveData<UserNameValidationError>()
    }
    val passwordValidationError: MutableLiveData<PasswordValidationError> by lazy {
        MutableLiveData<PasswordValidationError>()
    }
    var view: LoginView? = null

    fun onUserNameKeyEvent(keyCode: Int, event: KeyEvent): Boolean {
        userNameValidationError.value = null
        return false
    }

    fun onPasswordKeyEvent(keyCode: Int, event: KeyEvent): Boolean {
        passwordValidationError.value = null
        return false
    }

    fun onClickNext(username: String?, password: String?) {
        Log.d(LoginViewModel::class.java.simpleName, "onClickNext($username, $password)")
        if (username.isNullOrEmpty()) {
            userNameValidationError.value = UserNameValidationError.EMPTY
            return
        } else {
            userNameValidationError.value = null
        }
        if (password == null || password.length < 8) {
            passwordValidationError.value = PasswordValidationError.TOO_SHORT
            return
        } else if (password == username) {
            passwordValidationError.value = PasswordValidationError.EQUAL_USERNAME
            return
        } else {
            passwordValidationError.value = null
        }
        view?.navigateToProducts()
    }

    override fun onCleared() {
        view = null
        super.onCleared()
    }

    enum class UserNameValidationError {
        EMPTY
    }

    enum class PasswordValidationError {
        TOO_SHORT,
        EQUAL_USERNAME
    }
}

interface LoginView {
    fun navigateToProducts()
}

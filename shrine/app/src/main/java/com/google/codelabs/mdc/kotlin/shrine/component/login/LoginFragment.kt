package com.google.codelabs.mdc.kotlin.shrine.component.login

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import com.google.codelabs.mdc.kotlin.shrine.R
import kotlinx.android.synthetic.main.shr_login_fragment.view.*

/**
 * Fragment representing the login screen for Shrine.
 */
class LoginFragment : Fragment(), LoginView {
    private lateinit var viewModel: LoginViewModel

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(LoginViewModel::class.java).apply {
            view = this@LoginFragment
        }
        observeViewModelValues()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.shr_login_fragment, container, false)
        // Set an error if the password is less than 8 characters.
        view.next_button.setOnClickListener {
            viewModel.onClickNext(
                username = view?.user_name_edit_text?.text?.toString(),
                password = view?.password_edit_text?.text?.toString()
            )
        }
        view.user_name_edit_text.setOnKeyListener { _, keyCode, event ->
            viewModel.onUserNameKeyEvent(keyCode, event)
        }
        // Clear the error once more than 8 characters are typed.
        view.password_edit_text.setOnKeyListener { _, keyCode, event ->
            viewModel.onPasswordKeyEvent(keyCode, event)
        }
        return view
    }

    override fun navigateToProducts() {
        Navigation.createNavigateOnClickListener(R.id.action_login_fragment_to_products_fragment).onClick(view?.next_button)
    }

    private fun observeViewModelValues() {
        viewModel.userNameValidationError.observe(this, Observer<LoginViewModel.UserNameValidationError> { error ->
            Log.d(LoginFragment::class.java.simpleName, "userNameValidationError($error)")
            view?.user_name_text_input?.error =
                when (error) {
                    LoginViewModel.UserNameValidationError.EMPTY -> getString(R.string.shr_validation_error_username)
                    else -> null
                }
        })
        viewModel.passwordValidationError.observe(this, Observer<LoginViewModel.PasswordValidationError> { error ->
            Log.d(LoginFragment::class.java.simpleName, "passwordValidationError($error)")
            view?.password_text_input?.error =
                when (error) {
                    LoginViewModel.PasswordValidationError.TOO_SHORT -> getString(R.string.shr_validation_error_password)
                    LoginViewModel.PasswordValidationError.EQUAL_USERNAME -> getString(R.string.shr_validation_error_password_equal_username)
                    else -> null
                }
        })
    }
}

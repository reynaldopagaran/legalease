package com.hcdc.legalease.ui.components.bottomSheet.ForgotPasswordSheet

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ForgotPasswordViewModel : ViewModel() {

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    // Email error state
    private val _emailError = MutableStateFlow<String?>(null)
    val emailError: StateFlow<String?> = _emailError.asStateFlow()

    fun onEmailChanged(newValue: String) {
        _email.value = newValue
        _emailError.value = null // reset error on change
    }

    fun validateEmail(): Boolean {
        val emailValue = _email.value.trim()

        return if (emailValue.isBlank()) {
            _emailError.value = "Email is required"
            false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailValue).matches()) {
            _emailError.value = "Invalid email format"
            false
        } else {
            _emailError.value = null
            true
        }
    }
}

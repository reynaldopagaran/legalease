import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.hcdc.legalease.ui.screens.upload_pdf.await
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RegisterSheetViewModel : ViewModel() {

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _repeatPassword = MutableStateFlow("")
    val repeatPassword: StateFlow<String> = _repeatPassword.asStateFlow()

    // Field-specific errors
    private val _emailError = MutableStateFlow<String?>(null)
    val emailError: StateFlow<String?> = _emailError.asStateFlow()

    private val _passwordError = MutableStateFlow<String?>(null)
    val passwordError: StateFlow<String?> = _passwordError.asStateFlow()

    private val _repeatPasswordError = MutableStateFlow<String?>(null)
    val repeatPasswordError: StateFlow<String?> = _repeatPasswordError.asStateFlow()

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun onEmailChanged(value: String) {
        _email.value = value
        _emailError.value = null
    }

    fun onPasswordChanged(value: String) {
        _password.value = value
        _passwordError.value = null
    }

    fun onRepeatPasswordChanged(value: String) {
        _repeatPassword.value = value
        _repeatPasswordError.value = null
    }

    private fun resetFields() {
        _email.value = ""
        _password.value = ""
        _repeatPassword.value = ""
        _emailError.value = null
        _passwordError.value = null
        _repeatPasswordError.value = null
    }

    fun register(onSuccess: () -> Unit) {
        val email = _email.value.trim()
        val password = _password.value.trim()
        val repeatPassword = _repeatPassword.value.trim()

        // Reset previous errors
        _emailError.value = null
        _passwordError.value = null
        _repeatPasswordError.value = null

        var hasError = false

        if (email.isBlank()) {
            _emailError.value = "Email is required"
            hasError = true
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailError.value = "Invalid email format"
            hasError = true
        }

        if (password.isBlank()) {
            _passwordError.value = "Password is required"
            hasError = true
        } else if (password.length < 8) {
            _passwordError.value = "Password must be at least 8 characters"
            hasError = true
        }

        if (repeatPassword.isBlank()) {
            _repeatPasswordError.value = "Repeat your password"
            hasError = true
        } else if (repeatPassword != password) {
            _repeatPasswordError.value = "Passwords do not match"
            hasError = true
        }

        if (hasError) return

        // No errors → proceed with Firebase registration
        viewModelScope.launch {
            try {
                val result = auth.createUserWithEmailAndPassword(email, password).await()
                result.user?.sendEmailVerification()?.await()
                onSuccess()
                resetFields()
            } catch (e: Exception) {
                _emailError.value = e.localizedMessage ?: "Registration failed"
            }
        }
    }
}

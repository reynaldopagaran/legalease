package com.hcdc.legalease.ui.components.bottomSheet.LoginSheet

import android.app.Activity
import android.util.Log
import androidx.credentials.GetCredentialRequest
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.hcdc.legalease.R
import com.hcdc.legalease.ui.screens.upload_pdf.await
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class LoginSheetViewModel : ViewModel() {

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    // Error states
    private val _emailError = MutableStateFlow<String?>(null)
    val emailError: StateFlow<String?> = _emailError.asStateFlow()

    private val _passwordError = MutableStateFlow<String?>(null)
    val passwordError: StateFlow<String?> = _passwordError.asStateFlow()

    // ⭐️ Loading State Management
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val _isTermsAccepted = MutableStateFlow(false)
    val isTermsAccepted: StateFlow<Boolean> = _isTermsAccepted

    fun onTermsAcceptedChanged(accepted: Boolean) {
        _isTermsAccepted.value = accepted
    }

    fun onEmailChanged(newValue: String) {
        _email.value = newValue
        _emailError.value = null
    }

    fun onPasswordChanged(newValue: String) {
        _password.value = newValue
        _passwordError.value = null
    }

    // ⭐️ Public function to control loading state
    fun setIsLoading(loading: Boolean) {
        _isLoading.value = loading
    }


    /**
     * Builds the GetCredentialRequest for Google Sign-In.
     */
    fun buildCredentialRequest(activity: Activity): GetCredentialRequest {
        // You MUST ensure R.string.webz_client_id is your correct Web (Server) Client ID
        val serverClientId = activity.getString(R.string.webz_client_id)
        Log.d("LoginSheet", "VM: Using Server Client ID: ${serverClientId}")

        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(true)
            .setServerClientId(serverClientId)
            .build()

        return GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()
    }

    /**
     * Initiates the Google Sign-In flow using CredentialManager within the ViewModel's lifecycle.
     */
    fun startGoogleSignIn(
        activity: Activity,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                // Loading state should be set by the Composable before calling this
                Log.d("LoginSheet", "VM: Google sign-in initiated.")
                val credentialManager = CredentialManager.create(activity)
                val request = buildCredentialRequest(activity)

                val result = credentialManager.getCredential(
                    request = request,
                    context = activity
                )

                Log.d("LoginSheet", "VM: Credential received. Authenticating with Firebase.")

                handleCredential(
                    credential = result.credential,
                    onSuccess = onSuccess,
                    onError = onError
                )

            } catch (e: Exception) {
                // ⭐️ Stop loading on exception
                setIsLoading(false)
                // Catch user cancellation and other API errors
                Log.e("LoginSheet", "VM: Google Sign-In Exception", e)
                val displayMessage = when {
                    e.localizedMessage?.contains("cancellation", ignoreCase = true) == true -> "Google Sign-In was cancelled."
                    else -> e.localizedMessage ?: "Sign-In failed unexpectedly."
                }
                onError(displayMessage)
            }
        }
    }


    /**
     * Handles the credential result from CredentialManager and signs in with Firebase.
     */
    fun handleCredential(
        credential: Credential,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            Log.d("LoginSheet", "VM: Parsing Google credential...")
            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
            val idToken = googleIdTokenCredential.idToken

            if (idToken == null) {
                // ⭐️ Stop loading on error
                setIsLoading(false)
                onError("ID Token is null. Check Web Client ID configuration.")
                return
            }

            val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
            Log.d("LoginSheet", "VM: Signing in with Firebase...")
            auth.signInWithCredential(firebaseCredential)
                .addOnCompleteListener { task ->
                    // ⭐️ Stop loading on success or failure inside the callback
                    setIsLoading(false)
                    if (task.isSuccessful) {
                        Log.d("LoginSheet", "VM: Firebase sign-in success: ${auth.currentUser?.email}")
                        onSuccess()
                    } else {
                        Log.e("LoginSheet", "VM: Firebase sign-in failed", task.exception)
                        onError(task.exception?.localizedMessage ?: "Firebase auth failed")
                    }
                }
        } catch (e: Exception) {
            // ⭐️ Stop loading on exception
            setIsLoading(false)
            Log.e("LoginSheet", "VM: Google credential parsing failed", e)
            onError("Google credential parsing failed: ${e.localizedMessage}")
        }
    }

    /** Validate the fields before attempting login */
    fun validate(): Boolean {
        var isValid = true
        val emailValue = _email.value.trim()
        val passwordValue = _password.value.trim()

        if (emailValue.isBlank()) {
            _emailError.value = "Email is required"
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailValue).matches()) {
            _emailError.value = "Invalid email format"
            isValid = false
        }

        if (passwordValue.isBlank()) {
            _passwordError.value = "Password is required"
            isValid = false
        } else if (passwordValue.length < 8) {
            _passwordError.value = "Password must be at least 8 characters"
            isValid = false
        }

        return isValid
    }

    /** Attempt Firebase login */
    fun login(onSuccess: () -> Unit, onUnverified: () -> Unit, onError: (String) -> Unit) { // ⭐️ Added onError
        if (!validate()) {
            setIsLoading(false) // Stop loading if validation fails immediately
            return
        }

        val emailValue = _email.value.trim()
        val passwordValue = _password.value.trim()

        viewModelScope.launch {
            try {
                // The 'await()' function is assumed to be an extension function for Kotlin Coroutines
                val result = auth.signInWithEmailAndPassword(emailValue, passwordValue).await()
                val user = auth.currentUser

                // ⭐️ Stop loading on success/failure path
                setIsLoading(false)

                if (user != null) {
                    if (user.isEmailVerified) {
                        onSuccess()
                    } else {
                        onUnverified() // Trigger snackbar for unverified email
                        auth.signOut()
                    }
                }
            } catch (e: Exception) {
                // ⭐️ Stop loading on exception
                setIsLoading(false)

                val message = e.localizedMessage ?: "Login failed"
                _passwordError.value = message // Show error on UI
                onError(message) // Trigger snackbar/logic in Composable
            }
        }
    }

    /** Reset fields and errors */
    fun resetFields() {
        _email.value = ""
        _password.value = ""
        _emailError.value = null
        _passwordError.value = null
    }
}
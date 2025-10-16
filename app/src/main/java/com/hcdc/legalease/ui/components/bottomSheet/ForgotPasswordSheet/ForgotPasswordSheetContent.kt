package com.hcdc.legalease.ui.components.bottomSheet.ForgotPasswordSheet

import PrimaryTextField
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth
import com.hcdc.legalease.ui.components.bottomSheet.MainBottomSheets.BottomSheetContent
import com.hcdc.legalease.ui.components.buttons.PrimaryButton
import com.hcdc.legalease.ui.components.spacers.VerticalSpacer
import com.hcdc.legalease.ui.components.bottomSheet.MainBottomSheets.BottomSheetViewModel
import com.hcdc.legalease.ui.screens.upload_pdf.await
import kotlinx.coroutines.launch

@Composable
fun ForgotPasswordSheetContent(onSubmit: () -> Unit) {

    val bottomSheetViewModel: BottomSheetViewModel = viewModel()
    val forgotPasswordViewModel: ForgotPasswordViewModel = viewModel()

    val email by forgotPasswordViewModel.email.collectAsState()
    val emailError by forgotPasswordViewModel.emailError.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val auth: FirebaseAuth = FirebaseAuth.getInstance()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 0.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "Forgot Password",
                style = MaterialTheme.typography.displayLarge.copy(
                    color = MaterialTheme.colorScheme.primary
                )
            )
            VerticalSpacer(height = 10.dp)

            PrimaryTextField(
                value = email,
                onValueChange = { forgotPasswordViewModel.onEmailChanged(it) },
                label = "Email",
                errorMessage = emailError
            )

            VerticalSpacer(height = 30.dp)

            PrimaryButton(
                text = "Submit",
                onClick = {
                    if (forgotPasswordViewModel.validateEmail()) {
                        scope.launch {
                            val emailValue = forgotPasswordViewModel.email.value.trim()
                            try {
                                auth.sendPasswordResetEmail(emailValue).await()
                            } catch (e: Exception) {
                                // Optional: log e, but do NOT reveal to user
                            }
                            snackbarHostState.showSnackbar(
                                "If an account exists with this email, a password reset email has been sent."
                            )
                        }
                    }
                }
            )

            VerticalSpacer(height = 30.dp)

            Text(
                text = "────── or ──────",
                style = MaterialTheme.typography.bodySmall
            )
            VerticalSpacer(height = 25.dp)

            Text(
                text = "Login Instead",
                modifier = Modifier.clickable {
                    bottomSheetViewModel.show(BottomSheetContent.Login)
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
        }

        // Snackbar stacked at the bottom
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
        )
    }
}

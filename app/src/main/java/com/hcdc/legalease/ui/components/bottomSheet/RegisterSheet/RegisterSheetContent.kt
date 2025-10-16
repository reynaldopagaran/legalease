package com.hcdc.legalease.ui.components.bottomSheet.RegisterSheet

import PrimaryTextField
import RegisterSheetViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hcdc.legalease.ui.components.bottomSheet.MainBottomSheets.BottomSheetContent
import com.hcdc.legalease.ui.components.bottomSheet.MainBottomSheets.BottomSheetViewModel
import com.hcdc.legalease.ui.components.buttons.PrimaryButton
import com.hcdc.legalease.ui.components.spacers.VerticalSpacer

@Composable
fun RegisterSheetContent(onSubmit: () -> Unit) {

    val bottomSheetViewModel: BottomSheetViewModel = viewModel()
    val registerSheetViewModel: RegisterSheetViewModel = viewModel()

    val email by registerSheetViewModel.email.collectAsState()
    val password by registerSheetViewModel.password.collectAsState()
    val repeatPassword by registerSheetViewModel.repeatPassword.collectAsState()

    val emailError by registerSheetViewModel.emailError.collectAsState()
    val passwordError by registerSheetViewModel.passwordError.collectAsState()
    val repeatPasswordError by registerSheetViewModel.repeatPasswordError.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp, vertical = 0.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        Text(
            text = "Register",
            style = MaterialTheme.typography.displayLarge.copy(
                color = MaterialTheme.colorScheme.primary
            )
        )
        VerticalSpacer(height = 10.dp)

        PrimaryTextField(
            value = email,
            onValueChange = { registerSheetViewModel.onEmailChanged(it) },
            label = "Email",
            errorMessage = emailError
        )
        VerticalSpacer(height = 5.dp)

        PrimaryTextField(
            value = password,
            onValueChange = { registerSheetViewModel.onPasswordChanged(it) },
            label = "Password",
            isPassword = true,
            errorMessage = passwordError
        )
        VerticalSpacer(height = 5.dp)

        PrimaryTextField(
            value = repeatPassword,
            onValueChange = { registerSheetViewModel.onRepeatPasswordChanged(it) },
            label = "Repeat Password",
            isPassword = true,
            errorMessage = repeatPasswordError
        )
        VerticalSpacer(height = 30.dp)

        PrimaryButton(
            text = "Register",
            onClick = { registerSheetViewModel.register(onSubmit) } // No snackbar here
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
}

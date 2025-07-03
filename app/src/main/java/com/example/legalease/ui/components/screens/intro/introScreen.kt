package com.example.legalease.ui.components.screens.intro

import BottomSheet
import PrimaryTextField
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.legalease.R
import com.example.legalease.ui.components.bottomSheet.BottomSheetContent
import com.example.legalease.ui.components.bottomSheet.LoginSheetContent
import com.example.legalease.ui.components.buttons.PrimaryButton
import com.example.legalease.ui.components.spacers.VerticalSpacer
import com.legalease.viewmodel.BottomSheetViewModel

@Composable
fun IntroScreen(
    viewModel: IntroViewModel,
    sheetViewModel: BottomSheetViewModel
) {

    val email by viewModel.email.collectAsState()
    val password by viewModel.password.collectAsState()

    BottomSheet(
        viewModel = sheetViewModel,
        loginContent = { LoginSheetContent { sheetViewModel.hide() } },
        registerContent = { Text("Register Form") },
        forgotPasswordContent = { Text("Forgot Password Form") }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp, vertical = 0.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Image(
            painter = painterResource(id = R.drawable.lawyer),
            contentDescription = "Intro Screen Image",
            modifier = Modifier.size(350.dp)
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "LegalEase",
                style = MaterialTheme.typography.displayLarge.copy(
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 50.sp
                )
            )
            VerticalSpacer(height = 8.dp)
            Text(
                text = "Understand your contract before you sign.",
                style = MaterialTheme.typography.titleSmall.copy(
                    color = MaterialTheme.colorScheme.onBackground

                )
            )
            VerticalSpacer(height = 35.dp)
            PrimaryButton(
                text = "Begin",
                onClick = {sheetViewModel.show(BottomSheetContent.Login)}
                )
        }
    }
}


package com.example.legalease.ui.screens.intro

import com.example.legalease.ui.components.bottomSheet.MainBottomSheets.BottomSheet
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.legalease.R
import com.example.legalease.ui.components.bottomSheet.MainBottomSheets.BottomSheetContent
import com.example.legalease.ui.components.bottomSheet.ForgotPasswordSheet.ForgotPasswordSheetContent
import com.example.legalease.ui.components.bottomSheet.LoginSheet.LoginSheetContent
import com.example.legalease.ui.components.bottomSheet.RegisterSheet.RegisterSheetContent
import com.example.legalease.ui.components.buttons.PrimaryButton
import com.example.legalease.ui.components.spacers.VerticalSpacer
import com.example.legalease.ui.components.bottomSheet.MainBottomSheets.BottomSheetViewModel

@Composable
fun IntroScreen(
    navController: NavController
) {
    val sheetViewModel: BottomSheetViewModel = viewModel()

    BottomSheet(
        viewModel = sheetViewModel,
        loginContent = {
            LoginSheetContent(
                navController = navController,
                onLoginSuccess = { sheetViewModel.hide() }
            )
        },
        registerContent = { RegisterSheetContent { sheetViewModel.hide() } },
        forgotPasswordContent = { ForgotPasswordSheetContent { sheetViewModel.hide() } }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp, vertical = 20.dp),
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


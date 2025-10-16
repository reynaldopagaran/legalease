package com.hcdc.legalease.ui.screens.intro

import android.content.Context
import com.hcdc.legalease.ui.components.bottomSheet.MainBottomSheets.BottomSheet
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.hcdc.legalease.R
import com.hcdc.legalease.ui.components.bottomSheet.MainBottomSheets.BottomSheetContent
import com.hcdc.legalease.ui.components.bottomSheet.ForgotPasswordSheet.ForgotPasswordSheetContent
import com.hcdc.legalease.ui.components.bottomSheet.LoginSheet.LoginSheetContent
import com.hcdc.legalease.ui.components.bottomSheet.RegisterSheet.RegisterSheetContent
import com.hcdc.legalease.ui.components.buttons.PrimaryButton
import com.hcdc.legalease.ui.components.spacers.VerticalSpacer
import com.hcdc.legalease.ui.components.bottomSheet.MainBottomSheets.BottomSheetViewModel

@Composable
fun IntroScreen(
    navController: NavController
) {
    val user = FirebaseAuth.getInstance().currentUser

    // 👇 If already logged in, redirect immediately
    if (user != null) {
        // use LaunchedEffect so it only runs once, not every recomposition
        LaunchedEffect(Unit) {
            navController.navigate("dashboard") {
                popUpTo("home") { inclusive = true } // remove intro from backstack
            }
        }
    } else {
        // 👇 Show your existing intro UI only if no user

        IntroContent(navController)
    }
}

@Composable
private fun IntroContent(navController: NavController) {
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
                onClick = { sheetViewModel.show(BottomSheetContent.Login) }
            )
        }
    }
}



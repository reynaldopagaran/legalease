package com.hcdc.legalease.ui.components.bottomSheet.LoginSheet

import PrimaryTextField
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.hcdc.legalease.R
import com.hcdc.legalease.ui.components.bottomSheet.MainBottomSheets.BottomSheetContent
import com.hcdc.legalease.ui.components.buttons.PrimaryButton
import com.hcdc.legalease.ui.components.spacers.VerticalSpacer
import com.hcdc.legalease.ui.components.bottomSheet.MainBottomSheets.BottomSheetViewModel
import com.hcdc.legalease.ui.screens.dashboard.LoadingDialog
import kotlinx.coroutines.launch
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.foundation.layout.heightIn


// Extension to safely unwrap an Activity
fun Context.findActivity(): Activity? {
    var ctx = this
    while (ctx is ContextWrapper) {
        if (ctx is Activity) return ctx
        ctx = ctx.baseContext
    }
    return null
}

@Composable
fun PolicyDialog(
    title: String,
    text: String,
    onDismissRequest: () -> Unit
) {
    // 1. Get the screen height configuration
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp

    // Calculate 50% of the screen height
    val maxHeight = screenHeight * 0.5f

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium.copy(
                    color = MaterialTheme.colorScheme.primary
                )
            )
        },
        text = {
            // 2. Apply the height constraint to the scrollable content
            Column(
                modifier = Modifier
                    .heightIn(max = maxHeight) // Set the maximum height constraint
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismissRequest) {
                Text(
                    text = "Close",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.surface
    )
}

@Composable
fun LoginSheetContent(
    navController: NavController,
    onLoginSuccess: () -> Unit
) {
    val loginSheetViewModel: LoginSheetViewModel = viewModel()
    val bottomSheetViewModel: BottomSheetViewModel = viewModel()

    val email by loginSheetViewModel.email.collectAsState()
    val password by loginSheetViewModel.password.collectAsState()
    val emailError by loginSheetViewModel.emailError.collectAsState()
    val passwordError by loginSheetViewModel.passwordError.collectAsState()
    val isLoading by loginSheetViewModel.isLoading.collectAsState()
    // ⭐️ Observe the terms acceptance state
    val isTermsAccepted by loginSheetViewModel.isTermsAccepted.collectAsState()

    // ⭐️ State for dialog visibility
    var showTermsDialog by remember { mutableStateOf(false) }
    var showConditionsDialog by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
   // val scrollState = rememberScrollState()

    val TERMS_TEXT = context.getString(R.string.terms)
    val CONDITIONS_TEXT = context.getString(R.string.conditions)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 0.dp),
                //.verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "Login",
                style = MaterialTheme.typography.displayLarge.copy(
                    color = MaterialTheme.colorScheme.primary
                )
            )
            VerticalSpacer(height = 10.dp)

            PrimaryTextField(
                value = email,
                onValueChange = { loginSheetViewModel.onEmailChanged(it) },
                label = "Email",
                errorMessage = emailError,
                //enabled = !isLoading
            )
            VerticalSpacer(height = 5.dp)

            PrimaryTextField(
                value = password,
                onValueChange = { loginSheetViewModel.onPasswordChanged(it) },
                label = "Password",
                isPassword = true,
                errorMessage = passwordError,
                // enabled = !isLoading
            )
            VerticalSpacer(height = 10.dp)

            // ✅ NEW LOCATION: Combined "Forgot password?" and "Create an Account" into one spaced-out Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 2. Create an Account
                Text(
                    text = "Create an Account",
                    modifier = Modifier.clickable(enabled = !isLoading) {
                        bottomSheetViewModel.show(BottomSheetContent.Register)
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
                // 1. Forgot Password
                Text(
                    text = "Forgot password?",
                    modifier = Modifier.clickable(enabled = !isLoading) {
                        bottomSheetViewModel.show(BottomSheetContent.ForgotPassword)
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            // END OF NEW LOCATION

            VerticalSpacer(height = 20.dp) // Adjusted spacer after the inline links

            // ⭐️ Terms and Conditions Checkbox Row (Now below the inline links)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Checkbox(
                    checked = isTermsAccepted,
                    onCheckedChange = { loginSheetViewModel.onTermsAcceptedChanged(it) },
                    enabled = !isLoading,
                    colors = CheckboxDefaults.colors(
                        checkedColor = MaterialTheme.colorScheme.primary,
                        uncheckedColor = MaterialTheme.colorScheme.outline
                    )
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    // Click on the overall text toggles the checkbox
                    modifier = Modifier.clickable(enabled = !isLoading) {
                        loginSheetViewModel.onTermsAcceptedChanged(!isTermsAccepted)
                    }
                ) {
                    Text(
                        text = "I agree to the ",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Terms",
                        // Specific click to open the Terms Dialog
                        modifier = Modifier.clickable(enabled = !isLoading) {
                            showTermsDialog = true
                        },
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                    Text(
                        text = " and ",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Conditions",
                        // Specific click to open the Conditions Dialog
                        modifier = Modifier.clickable(enabled = !isLoading) {
                            showConditionsDialog = true
                        },
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                }
            }

            VerticalSpacer(height = 10.dp)

            PrimaryButton(
                text = "Login",
                // ⭐️ Button is enabled only if not loading AND terms are accepted
                enabled = !isLoading && isTermsAccepted,
                onClick = {
                    loginSheetViewModel.setIsLoading(true)
                    loginSheetViewModel.login(
                        onSuccess = {
                            val sharedPref = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                            val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""
                            sharedPref.edit().putString("uid", uid).apply()

                            onLoginSuccess()
                            navController.navigate("dashboard")
                        },
                        onUnverified = {
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    "Please verify your email before logging in."
                                )
                            }
                        },
                        onError = { msg ->
                            scope.launch {
                                snackbarHostState.showSnackbar(msg)
                            }
                        }
                    )
                }
            )

            VerticalSpacer(height = 30.dp)

            Text(
                text = "────── or ──────",
                style = MaterialTheme.typography.bodySmall
            )
            VerticalSpacer(height = 25.dp)

            Image(
                painter = painterResource(id = R.drawable.google),
                contentDescription = "Google Icon",
                modifier = Modifier
                    .size(40.dp)
                    // ⭐️ Google login is enabled only if not loading AND terms are accepted
                    .clickable(enabled = !isLoading && isTermsAccepted) {
                        scope.launch {
                            val currentActivity = context.findActivity()
                            if (currentActivity == null) {
                                Log.e("LoginSheet", "FATAL: Google Sign-In failed: Activity is null")
                                scope.launch { snackbarHostState.showSnackbar("Setup Error: Cannot find Activity.") }
                                return@launch
                            }

                            loginSheetViewModel.startGoogleSignIn(
                                activity = currentActivity,
                                onSuccess = {
                                    val sharedPref = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                                    val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""
                                    sharedPref.edit().putString("uid", uid).apply()

                                    onLoginSuccess()
                                    navController.navigate("dashboard")
                                },
                                onError = { msg ->
                                    scope.launch {
                                        Log.e("LoginSheet", "Google Sign-In error: $msg")
                                        snackbarHostState.showSnackbar(msg)
                                    }
                                }
                            )
                        }
                    }
            )

            VerticalSpacer(height = 30.dp)
            VerticalSpacer(height = 30.dp)
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
        )

        LoadingDialog(show = isLoading, message = "Signing in...")

        // ⭐️ Terms Dialog
        if (showTermsDialog) {
            PolicyDialog(
                title = "Terms of Service",
                text = TERMS_TEXT,
                onDismissRequest = { showTermsDialog = false }
            )
        }

        // ⭐️ Conditions Dialog
        if (showConditionsDialog) {
            PolicyDialog(
                title = "Conditions of Use",
                text = CONDITIONS_TEXT,
                onDismissRequest = { showConditionsDialog = false }
            )
        }
    }
}
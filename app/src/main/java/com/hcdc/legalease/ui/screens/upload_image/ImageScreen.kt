package com.hcdc.legalease.ui.screens.upload_image

import android.annotation.SuppressLint
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue // <-- Required for `var showLoadingDialog by remember`
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.hcdc.legalease.ui.components.buttons.PrimaryButton
import com.hcdc.legalease.ui.components.buttons.SecondaryButton
import com.hcdc.legalease.ui.components.spacers.VerticalSpacer
import com.hcdc.legalease.ui.screens.dashboard.LoadingDialog // <-- New import
import kotlinx.coroutines.launch


@SuppressLint("NewApi")
@Composable
fun ImageScreen(
    navController: NavController,
    viewModel: ImageViewModel = viewModel()
) {

    val images by viewModel.selectedImages.collectAsState()
    val context = LocalContext.current
    val texts = viewModel.ocrResults.collectAsState()
    val scanCompleted by viewModel.scanCompleted.collectAsState()
    var hasNavigated = remember { false }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    // Replaced isLoading with showLoadingDialog
    var showLoadingDialog by remember { mutableStateOf(false) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        viewModel.setSelectedImages(uris)
    }

    LaunchedEffect(scanCompleted) {
        if (scanCompleted && !hasNavigated) {
            showLoadingDialog = false // <-- Dismiss dialog before navigating
            hasNavigated = true
            val scannedText = texts.value.joinToString(" ").replace("\\s+".toRegex(), " ").trim()
            val encodedText = Uri.encode(scannedText)
            navController.navigate("result/$encodedText") {
                popUpTo("dashboard") { inclusive = false }
                launchSingleTop = true
            }
        }
    }

    // Integrated LoadingDialog
    LoadingDialog(
        show = showLoadingDialog,
        message = "Please wait..."
    )

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 20.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "(${images.size}) Images selected"
            )

            VerticalSpacer(20.dp)
            // Image Viewer Placeholder
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                if (images.isNotEmpty()) {
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(images) { uri ->
                            AsyncImage(
                                model = uri,
                                contentDescription = null,
                                modifier = Modifier
                                    .background(MaterialTheme.colorScheme.surfaceVariant),
                                contentScale = ContentScale.Fit
                            )
                        }
                    }
                } else {
                    Text("No images selected.")
                }
            }
            VerticalSpacer(20.dp)
            SecondaryButton(
                text = "Choose Image/s",
                onClick = {
                    imagePickerLauncher.launch("image/*")
                }
            )
            VerticalSpacer(10.dp)
            PrimaryButton(
                text = "Proceed to Scan",
                onClick = {
                    if (images.isNotEmpty()){
                        // Replaced isLoading.value = true
                        showLoadingDialog = true
                        viewModel.clearOcrResults()
                        viewModel.runTextRecognition(context, images)
                    }else{
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                message = "Please select images",
                                actionLabel = "OK"
                            )
                        }
                    }
                }
            )
        }
    }
}
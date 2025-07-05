package com.example.legalease.ui.screens.upload_pdf

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.provider.OpenableColumns
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.example.legalease.ui.components.buttons.PrimaryButton
import com.example.legalease.ui.components.buttons.SecondaryButton
import com.example.legalease.ui.components.spacers.VerticalSpacer
import com.example.legalease.viewmodel.PdfViewModel
import com.rajat.pdfviewer.HeaderData
import com.rajat.pdfviewer.PdfRendererView
import com.rajat.pdfviewer.compose.PdfRendererViewCompose
import com.rajat.pdfviewer.util.PdfSource


@SuppressLint("NewApi")
@Composable
fun PDFScreen(
    navController: NavController,
    viewModel: PdfViewModel
) {

    val context = LocalContext.current
    val pdfUri by viewModel.pdfUri.collectAsState()
    val fileName by viewModel.fileName.collectAsState()


    fun getFileNameFromUri(context: Context, uri: Uri): String? {
        val returnCursor = context.contentResolver.query(uri, null, null, null, null)
        returnCursor?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (nameIndex != -1 && cursor.moveToFirst()) {
                return cursor.getString(nameIndex)
            }
        }
        return null
    }


    val pdfPickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        uri?.let {
            context.contentResolver.takePersistableUriPermission(
                it,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            viewModel.setPdfUri(it)
            viewModel.setPdfName(getFileNameFromUri(context, uri))
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = fileName ?: "-",
            style = MaterialTheme.typography.bodyLarge.copy(
            )
        )

        VerticalSpacer(20.dp)
        // PDF Viewer Placeholder
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

            pdfUri?.let { uri ->
                val source = PdfSource.LocalUri(uri)
                key(uri) {
                    PdfRendererViewCompose(
                        source = source,
                        lifecycleOwner = LocalLifecycleOwner.current,
                        modifier = Modifier.fillMaxSize(),
                        zoomListener = object : PdfRendererView.ZoomListener {
                            override fun onZoomChanged(isZoomedIn: Boolean, scale: Float) {

                            }
                        }
                    )
                }
            } ?: Text("No PDF selected")

        }
        VerticalSpacer(20.dp)
        SecondaryButton(
            text = "Choose PDF",
            onClick = {
                pdfPickerLauncher.launch(arrayOf("application/pdf"))
            }
        )
        VerticalSpacer(10.dp)
        PrimaryButton(
            text = "Proceed to Scan",
            onClick = { navController.navigate("dashboard") }
        )
    }
}
package com.hcdc.legalease.ui.screens.upload_image

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class ImageViewModel : ViewModel() {
    private val _selectedImages = MutableStateFlow<List<Uri>>(emptyList())
    val selectedImages: StateFlow<List<Uri>> = _selectedImages

    fun setSelectedImages(uris: List<Uri>) {
        _selectedImages.value = uris
    }

    private val _ocrResults = MutableStateFlow<List<String>>(emptyList())
    val ocrResults: StateFlow<List<String>> = _ocrResults

    fun addOcrResult(text: String) {
        _ocrResults.update { it + text }
    }

    fun clearOcrResults() {
        _ocrResults.value = emptyList()
    }

    private val _scanCompleted = MutableStateFlow(false)
    val scanCompleted: StateFlow<Boolean> = _scanCompleted


    fun runTextRecognition(context: Context, uris: List<Uri>) {
        _scanCompleted.value = false
        _ocrResults.value = emptyList()
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        val tasks = uris.map { uri ->
            val image = InputImage.fromFilePath(context, uri)
            recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    addOcrResult(visionText.text)
                }
        }

        // Wait for all tasks to finish before marking scan as complete
        com.google.android.gms.tasks.Tasks.whenAllComplete(tasks)
            .addOnSuccessListener {
                _scanCompleted.value = true
            }
            .addOnFailureListener {
                _scanCompleted.value = true // still mark complete even if one failed
            }
    }


}
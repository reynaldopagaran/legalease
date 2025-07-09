package com.hcdc.legalease.ui.screens.result

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend
import com.hcdc.legalease.data.ClausesModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

class ResultViewmodel : ViewModel() {

    private val model = Firebase
        .ai(backend = GenerativeBackend.googleAI())
        .generativeModel("gemini-2.0-flash-lite")

    private val _clauses = mutableStateOf<ClausesModel?>(null)
    val clauses: State<ClausesModel?> = _clauses

    private val _scanCompleted = MutableStateFlow(false)
    val scanCompleted: StateFlow<Boolean> = _scanCompleted

    fun analyzePrompt(prompt: String) {
        viewModelScope.launch {
          _scanCompleted.value = false
            val response = model.generateContent(prompt)
            val rawText = response.text ?: "No output returned."
            try {
                val jsonStart = rawText.indexOf("{")
                val jsonEnd = rawText.lastIndexOf("}") + 1
                if (jsonStart == -1 || jsonEnd <= jsonStart) {
                    _clauses.value = null
                    return@launch
                }
                val jsonText = rawText.substring(jsonStart, jsonEnd)
                val parsed = Json.decodeFromString<ClausesModel>(jsonText)
                _clauses.value = parsed
            } catch (e: Exception) {
                _clauses.value = null
            }
            _scanCompleted.value = true
        }
    }
}

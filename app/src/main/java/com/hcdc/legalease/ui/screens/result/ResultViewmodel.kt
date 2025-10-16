package com.hcdc.legalease.ui.screens.result

import ClausesModel
import android.app.Application
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.hcdc.legalease.ui.components.spacers.OfflineInference
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.Date

class ResultViewmodel(application: Application) : AndroidViewModel(application) {

    private val _clauses = mutableStateOf<ClausesModel?>(null)
    val clauses: State<ClausesModel?> = _clauses
    private val _scanCompleted = MutableStateFlow(false)
    val scanCompleted: StateFlow<Boolean> = _scanCompleted
    private val spaceV by lazy { OfflineInference(application) }


    private val jsonParser = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    fun analyzeContract(contract: String) {

        viewModelScope.launch {
            _scanCompleted.value = false

            try {
               // val response = model.generateContent(prompt)
                val response = spaceV.contractText(contract)
                val rawText = response?: "No output returned."

                val regex = Regex("\\{[\\s\\S]*\\}")
                val match = regex.find(rawText)
                if (match == null) {
                    Log.w("TextGenerator", "No valid JSON found in AI response.")
                    _clauses.value = null
                } else {
                    val sanitized = sanitizeJson(match.value)
                    try {
                        val parsed = jsonParser.decodeFromString<ClausesModel>(sanitized)
                        _clauses.value = parsed
                        Log.d("TextGenerator", response)
                    } catch (jsonError: Exception) {
                        Log.e("TextGenerator", "JSON parsing failed, raw: $sanitized", jsonError)
                        _clauses.value = null
                    }
                }
            } catch (e: Exception) {
                Log.e("dex", "Error while parsing AI response", e)
                _clauses.value = null
            }
            _scanCompleted.value = true
        }

    }

    private fun sanitizeJson(text: String): String {
        var cleaned = text
            .replace("\n", " ")
            .replace("\r", " ")
            .replace("\\s+".toRegex(), " ")          // collapse multiple spaces
            .replace("\\\\", "")                     // remove double backslashes
            .replace("\"\\[\"", "[")                 // fix cases like "["" at start of array
            .replace("\"\\[", "[")                   // fix cases like "[Deed..."
            .replace("\\]\"", "]")                   // fix cases like "...square meters"]"
            .replace("\"]\"", "]")                   // fix cases like "]""
            .replace("\"\"", "\"")                   // remove double quotes
            .replace(Regex("[^\\x20-\\x7E]"), "")    // remove non-printable characters
            .trim()

        // --- Targeted JSON repairs ---
        // Replace bad array syntax like "acceptable": ["]
        cleaned = cleaned.replace(Regex("\"acceptable\"\\s*:\\s*\\[\"\\]"), "\"acceptable\": []")
        cleaned = cleaned.replace(Regex("\"high_risk\"\\s*:\\s*\\[\"\\]"), "\"high_risk\": []")

        // If acceptable or high_risk accidentally end with an open quote but no value
        cleaned = cleaned.replace(Regex("\"acceptable\"\\s*:\\s*\\[\"\\s*\\]"), "\"acceptable\": []")
        cleaned = cleaned.replace(Regex("\"high_risk\"\\s*:\\s*\\[\"\\s*\\]"), "\"high_risk\": []")

        // Ensure valid JSON start and end
        if (!cleaned.startsWith("{")) cleaned = "{ $cleaned"
        if (!cleaned.endsWith("}")) cleaned = "$cleaned }"

        return cleaned
    }



    fun saveContract(contractName: String, uid: String, onResult: (Boolean) -> Unit) {

        val contractData = _clauses.value ?: return onResult(false)

        if (uid == null) {
            Log.e("dex", "No logged-in user")
            onResult(false)
            return
        }

        val db = FirebaseFirestore.getInstance()

        val contractType = contractData.contractName.ifBlank { "Unknown Type" }

        // Generate a new document reference (Firestore generates unique ID)
        val docRef = db.collection(uid).document()
        val docId = docRef.id

        val data = mapOf(
            "id" to docId,               // store the generated ID
            "contractType" to contractType,
            "contractName" to contractName,
            "savedAt" to Date(),
            "clauses" to Json.encodeToString(contractData)
        )

        docRef.set(data)
            .addOnSuccessListener {
                onResult(true)
            }
            .addOnFailureListener { e ->
                Log.e("dex", "Failed to save contract", e)
                onResult(false)
            }
    }


}

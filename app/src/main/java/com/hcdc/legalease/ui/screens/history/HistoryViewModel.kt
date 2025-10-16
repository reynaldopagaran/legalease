package com.hcdc.legalease.ui.screens.history

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class HistoryViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val _contractNames = MutableStateFlow<List<String>>(emptyList())
    private val _ids = MutableStateFlow<List<String>>(emptyList())
    private val _contractTypes = MutableStateFlow<List<String>>(emptyList())

    val myID: StateFlow<List<String>> = _ids
    val contractNames: StateFlow<List<String>> = _contractNames
    val contractTypes: StateFlow<List<String>> = _contractTypes

    private var uid: String = ""

    fun setUid(uid: String) {
        this.uid = uid
        fetchContractData()
    }

    private fun fetchContractData() {
        if (uid.isEmpty()) return
        db.collection(uid)
            .get()
            .addOnSuccessListener { snapshot ->
                val names = snapshot.documents.mapNotNull { it.getString("contractName") }
                val ids = snapshot.documents.mapNotNull { it.getString("id") }
                val types = snapshot.documents.mapNotNull { it.getString("contractType") }

                _ids.value = ids
                _contractNames.value = names
                _contractTypes.value = types
            }
            .addOnFailureListener {
                _ids.value = emptyList()
                _contractNames.value = emptyList()
                _contractTypes.value = emptyList()
            }
    }

    // ✅ New function to delete a contract by ID
    fun deleteContract(contractId: String) {
        if (uid.isEmpty()) return
        db.collection(uid)
            .document(contractId)
            .delete()
            .addOnSuccessListener {
                // Remove from local state so UI updates immediately
                val index = _ids.value.indexOf(contractId)
                if (index >= 0) {
                    _ids.value = _ids.value.toMutableList().apply { removeAt(index) }
                    _contractNames.value = _contractNames.value.toMutableList().apply { removeAt(index) }
                    _contractTypes.value = _contractTypes.value.toMutableList().apply { removeAt(index) }
                }
            }
            .addOnFailureListener {
                // optional: handle failure, maybe show a toast
            }
    }
}

package com.hcdc.legalease.ui.screens.history

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.hcdc.legalease.R
import com.hcdc.legalease.ui.components.cards.HistoryCard.HistoryCard
import com.hcdc.legalease.ui.components.searchBar.ScanSearchBar
import com.hcdc.legalease.ui.components.spacers.VerticalSpacer

@Composable
fun HistoryScreen(
    navController: NavController,
    viewModel: HistoryViewModel = viewModel()
) {
    val context = LocalContext.current
    val sharedPref = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    val uid = sharedPref.getString("uid", "") ?: ""

    LaunchedEffect(uid) {
        viewModel.setUid(uid)
    }

    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }
    val contractNames by viewModel.contractNames.collectAsState()
    val myID by viewModel.myID.collectAsState()
    val contractTypes by viewModel.contractTypes.collectAsState()

    // Context Menu / Tooltip State (For Rename/Delete options)
    var expandedMenuIndex by remember { mutableStateOf<Int?>(null) }
    var selectedIdForAction by remember { mutableStateOf<String?>(null) }
    var selectedNameForAction by remember { mutableStateOf<String?>(null) } // Needed for Rename pre-fill

    // Dialog States
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showRenameDialog by remember { mutableStateOf(false) }
    var newContractName by remember { mutableStateOf("") }


    // Snackbar
    val snackbarHostState = remember { SnackbarHostState() }
    var snackbarMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
            snackbarMessage = null
        }
    }


    val filterMap = mapOf(
        "DS" to "Deed of Sale",
        "RA" to "Rent Agreement",
        "LA" to "Lease Agreement",
        "LO" to "Loan Agreement",
        "All" to "All"
    )

    val filteredItems = contractNames.mapIndexedNotNull { index, name ->
        val typeCode = contractTypes.getOrNull(index) ?: ""

        val selectedFullName = filterMap[selectedFilter] ?: selectedFilter

        if (name.contains(searchQuery, ignoreCase = true) &&
            (selectedFullName == "All" || typeCode == selectedFullName)
        ) {
            // Find the short code for the HistoryCard
            val typeShort = filterMap.entries.find { it.value == typeCode }?.key ?: ""
            index to Pair(name, typeShort) // Store original index, name, and short type
        } else null
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 20.dp, vertical = 20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "My Scans",
                        style = MaterialTheme.typography.displayLarge.copy(
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                    VerticalSpacer(5.dp)
                    Text(
                        text = "Your contract history in one place.",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    )
                }
                Image(
                    painter = painterResource(R.drawable.together),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(100.dp)
                )
            }

            Divider(color = Color.Gray.copy(alpha = .3f), thickness = 1.dp)
            VerticalSpacer(height = 15.dp)

            // Search + Filter Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ScanSearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(10.dp))

                var expandedFilter by remember { mutableStateOf(false) }
                Box {
                    Button(
                        onClick = { expandedFilter = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
                        modifier = Modifier.height(56.dp)
                    ) {
                        // FIX #2: Reverting to selectedFilter to display short code (e.g., "RA")
                        Text(text = selectedFilter)
                    }
                    DropdownMenu(
                        expanded = expandedFilter,
                        onDismissRequest = { expandedFilter = false }
                    ) {
                        listOf("All", "DS", "RA", "LA", "LO").forEach { option ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        when(option) {
                                            "DS" -> "Deed of Sale"
                                            "RA" -> "Rent Agreement"
                                            "LA" -> "Lease Agreement"
                                            "LO" -> "Loan Agreement"
                                            else -> "All"
                                        }
                                    )
                                },
                                onClick = {
                                    selectedFilter = option
                                    expandedFilter = false
                                }
                            )
                        }
                    }
                }
            }

            VerticalSpacer(height = 15.dp)

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(15.dp),
            ) {
                itemsIndexed(filteredItems) { listIndex, (originalIndex, data) ->
                    val (item, typeShort) = data // Deconstruct Pair to get name and short type
                    val id = myID.getOrNull(originalIndex) ?: return@itemsIndexed

                    Box(
                        modifier = Modifier.fillMaxWidth().wrapContentSize(Alignment.TopEnd)
                    ) {
                        HistoryCard(
                            title = item,
                            contractTypeShort = typeShort,
                            onClick = { navController.navigate("result_history/$id") },
                            onLongPress = {
                                // Show context menu/tooltip instead of immediate delete
                                selectedIdForAction = id
                                selectedNameForAction = item
                                expandedMenuIndex = listIndex
                            }
                        )
                        // Context Menu / Tooltip
                        DropdownMenu(
                            expanded = expandedMenuIndex == listIndex,
                            onDismissRequest = { expandedMenuIndex = null },
                            modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant) // Corrected background to surfaceVariant for better contrast/standard
                        ) {
                            // RENAME Option
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        "Rename",
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                },
                                onClick = {
                                    expandedMenuIndex = null
                                    showRenameDialog = true
                                }
                            )
                            // DELETE Option
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        "Delete",
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                },
                                onClick = {
                                    expandedMenuIndex = null
                                    showDeleteDialog = true
                                }
                            )
                        }
                    }
                }
            }
        }

        // ---

        // Delete confirmation dialog
        if (showDeleteDialog && selectedIdForAction != null) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false; selectedIdForAction = null; selectedNameForAction = null },
                containerColor = MaterialTheme.colorScheme.background,
                title = {
                    Text(
                        "Delete Contract?",
                        style = MaterialTheme.typography.headlineMedium
                    )
                },
                text = {
                    Text(
                        "Are you sure you want to delete the contract: \"${selectedNameForAction}\"?",
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.deleteContract(selectedIdForAction!!)
                        showDeleteDialog = false
                        selectedIdForAction = null
                        selectedNameForAction = null
                        snackbarMessage = "Contract deleted successfully" // trigger snackbar
                    }) {
                        Text(
                            "Delete",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false; selectedIdForAction = null; selectedNameForAction = null }) {
                        Text(
                            "Cancel",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            )
        }

        // ---

        // Rename dialog
        if (showRenameDialog && selectedIdForAction != null) {
            // FIX #1: LaunchedEffect synchronizes the TextField state with the latest selected name
            LaunchedEffect(selectedNameForAction) {
                newContractName = selectedNameForAction ?: ""
            }

            AlertDialog(
                onDismissRequest = {
                    showRenameDialog = false
                    selectedIdForAction = null
                    selectedNameForAction = null
                    newContractName = "" // Reset state on external dismissal
                },
                containerColor = MaterialTheme.colorScheme.background,
                title = {
                    Text(
                        "Rename Contract",
                        style = MaterialTheme.typography.headlineMedium
                    )
                },
                text = {
                    OutlinedTextField(
                        value = newContractName,
                        onValueChange = { newContractName = it },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            val idToRename = selectedIdForAction!!
                            val name = newContractName

                            viewModel.renameContract(idToRename, name) { success ->
                                showRenameDialog = false

                                // CRITICAL FIX #1: Explicitly nullify ID and Name to force the next
                                // long-press to pull the *new* name from the recomposed HistoryCard.
                                selectedIdForAction = null
                                selectedNameForAction = null
                                newContractName = ""

                                snackbarMessage = if (success) {
                                    "Contract renamed successfully to \"$name\""
                                } else {
                                    "Failed to rename contract. Please try again."
                                }
                            }
                        },
                        // Ensure the new name is different from the old one
                        enabled = newContractName.isNotBlank() && newContractName != selectedNameForAction
                    ) {
                        Text(
                            "Rename",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showRenameDialog = false
                        selectedIdForAction = null
                        selectedNameForAction = null
                        newContractName = "" // Reset state when hitting cancel
                    }) {
                        Text(
                            "Cancel",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            )
        }
    }
}
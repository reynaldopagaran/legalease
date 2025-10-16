package com.hcdc.legalease.ui.screens.history

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
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

    // Delete dialog state
    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedIdForDelete by remember { mutableStateOf<String?>(null) }

    // Snackbar
    val snackbarHostState = remember { SnackbarHostState() }
    var showSnackbar by remember { mutableStateOf(false) }
    LaunchedEffect(showSnackbar) {
        if (showSnackbar) {
            snackbarHostState.showSnackbar("Contract deleted successfully")
            showSnackbar = false
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
        val type = contractTypes.getOrNull(index) ?: ""
        val selectedFullName = filterMap[selectedFilter] ?: selectedFilter

        if (name.contains(searchQuery, ignoreCase = true) &&
            (selectedFullName == "All" || type == selectedFullName)
        ) {
            index to name
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

                var expanded by remember { mutableStateOf(false) }
                Box {
                    Button(
                        onClick = { expanded = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
                        modifier = Modifier.height(56.dp)
                    ) {
                        Text(text = selectedFilter)
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
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
                                    expanded = false
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
                itemsIndexed(filteredItems) { _, (index, item) ->
                    val id = myID.getOrNull(index) ?: return@itemsIndexed
                    HistoryCard(
                        title = item,
                        onClick = { navController.navigate("result_history/$id") },
                        onLongPress = {
                            selectedIdForDelete = id
                            showDeleteDialog = true
                        }
                    )
                }
            }
        }

        // Delete confirmation dialog
        if (showDeleteDialog && selectedIdForDelete != null) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = {
                    Text(
                        "Delete Contract?",
                        style = MaterialTheme.typography.headlineMedium
                    )
                },
                text = {
                    Text(
                        "Are you sure you want to delete this contract?",
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.deleteContract(selectedIdForDelete!!)
                        showDeleteDialog = false
                        showSnackbar = true // trigger snackbar
                    }) {
                        Text(
                            "Delete",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) {
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

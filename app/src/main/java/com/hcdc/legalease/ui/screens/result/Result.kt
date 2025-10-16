package com.hcdc.legalease.ui.screens.result

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.hcdc.legalease.ui.components.CustomLoading
import com.hcdc.legalease.ui.components.cards.EnforceCard
import com.hcdc.legalease.ui.components.cards.ResultCard.RiskCard
import com.hcdc.legalease.ui.components.cards.SummaryCard
import com.hcdc.legalease.ui.components.spacers.VerticalSpacer
import com.legalease.ui.theme.RiskColorHigh
import com.legalease.ui.theme.RiskColorLow


@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun ResultScreen(
    navController: NavController,
    ocrText: String,
    resultViewmodel: ResultViewmodel = viewModel()
) {
    val clauses = resultViewmodel.clauses.value
    val scanCompleted by resultViewmodel.scanCompleted.collectAsState()
    var launched by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val sharedPref = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    val uid = sharedPref.getString("uid", "") ?: ""

    if (!launched) {
        LaunchedEffect(Unit) {
            launched = true
            resultViewmodel.analyzeContract(ocrText)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (scanCompleted) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(horizontal = 20.dp, vertical = 20.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(0.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(.9f)) {
                        Text(
                            text = "Scan Complete",
                            style = MaterialTheme.typography.displayLarge.copy(
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                        VerticalSpacer(5.dp)
                        Text(
                            text = "Here’s What We Found.",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        )
                    }
                    Image(
                        painter = painterResource(R.drawable.complete),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .size(100.dp)
                            .weight(.3f)
                    )
                }

                VerticalSpacer(10.dp)
                HorizontalDivider(
                    color = Color.Gray.copy(alpha = .3f),
                    thickness = 1.dp,
                )
                VerticalSpacer(15.dp)

                clauses?.let { clauses ->
                    var showDialog by remember { mutableStateOf(false) }
                    var contractName by remember { mutableStateOf("") }

                    // 🔹 Save dialog
                    if (showDialog) {
                        AlertDialog(
                            onDismissRequest = { showDialog = false },
                            title = { Text("Save Contract") },
                            text = {
                                OutlinedTextField(
                                    value = contractName,
                                    onValueChange = { contractName = it },
                                    label = { Text("Enter contract name") }
                                )
                            },
                            confirmButton = {
                                TextButton(onClick = {
                                    if (contractName.isNotBlank()) {
                                        resultViewmodel.saveContract(contractName, uid) { success ->
                                            if (success) {
                                                Toast.makeText(context, "Save Successful", Toast.LENGTH_SHORT).show()
                                                showDialog = false
                                                navController.navigate("dashboard") {
                                                    popUpTo("result_screen") { inclusive = true } // optional: remove ResultScreen from back stack
                                                }
                                            } else {
                                                // optional: show a Snackbar or Toast for failure
                                            }
                                        }
                                    }
                                }) {
                                    Text("Save")
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = { showDialog = false }) {
                                    Text("Cancel")
                                }
                            }
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = clauses.contractName,
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                        Button(
                            onClick = { showDialog = true },
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text("Save")
                        }
                    }

                    VerticalSpacer(15.dp)
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(15.dp),
                    ) {
                        item {
                            SummaryCard(
                                modifier = Modifier,
                                text = clauses.summary
                            )
                            EnforceCard(
                                modifier = Modifier,
                                text = clauses.enforceability
                            )
                            if (clauses.acceptable.isNotEmpty()) {
                                RiskCard(
                                    riskLevel = "Acceptable",
                                    riskColor = RiskColorLow,
                                    points = clauses.acceptable
                                )
                            }
                            if (clauses.highRisk.isNotEmpty()) {
                                RiskCard(
                                    riskLevel = "High Risk",
                                    riskColor = RiskColorHigh,
                                    points = clauses.highRisk
                                )
                            }
                        }
                    }
                }
            }
        } else {
            CustomLoading()
        }
    }
}

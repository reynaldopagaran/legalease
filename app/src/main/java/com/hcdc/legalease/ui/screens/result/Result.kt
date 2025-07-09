package com.hcdc.legalease.ui.screens.result

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.hcdc.legalease.R
import com.hcdc.legalease.data.prompt.PromptProvider.buildPrompt
import com.hcdc.legalease.ui.components.CustomLoading
import com.hcdc.legalease.ui.components.cards.ResultCard.RiskCard
import com.hcdc.legalease.ui.components.cards.SummaryCard
import com.hcdc.legalease.ui.components.spacers.VerticalSpacer
import com.legalease.ui.theme.RiskColorHigh
import com.legalease.ui.theme.RiskColorLow
import com.legalease.ui.theme.RiskColorMedium


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

    if (!launched) {
    LaunchedEffect(Unit) {
        launched = true
        //Log.d("bamewst", ocrText)
       resultViewmodel.analyzePrompt(buildPrompt(ocrText))
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
                            text = "Insights from your past scan",
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
                    Text(
                        text = clauses.contractName,
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
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
                            if(clauses.acceptable.isEmpty()){
                                //
                            }else{
                                RiskCard(
                                    riskLevel = "Acceptable",
                                    riskColor = RiskColorLow,
                                    points = clauses.acceptable
                                )
                            }

                            if(clauses.moderateConcern.isEmpty()){
                                //
                            }else{
                                RiskCard(
                                    riskLevel = "Moderate Concern",
                                    riskColor = RiskColorMedium,
                                    points = clauses.moderateConcern
                                )
                            }

                            if(clauses.highRisk.isEmpty()){
                                //
                            }else{
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
        }else{
            CustomLoading()
        }
}}



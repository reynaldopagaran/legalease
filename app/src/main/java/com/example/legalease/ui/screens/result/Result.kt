package com.example.legalease.ui.screens.result

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.legalease.R
import com.example.legalease.ui.components.cards.HistoryCard.HistoryCard
import com.example.legalease.ui.components.cards.ResultCard.RiskCard
import com.example.legalease.ui.components.cards.SummaryCard
import com.example.legalease.ui.components.spacers.VerticalSpacer
import com.legalease.ui.theme.RiskColorHigh
import com.legalease.ui.theme.RiskColorLow
import com.legalease.ui.theme.RiskColorMedium

@Composable
fun ResultScreen(
    navController: NavController,
    resultViewmodel: ResultViewmodel
           ) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp, vertical = 20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(0.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(.9f)) {
                Text(
                    text = "Your Contract",
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
        Text(
            text = "Contract Name",
            style = MaterialTheme.typography.titleMedium.copy(
                color = MaterialTheme.colorScheme.primary
            )
        )
        VerticalSpacer(15.dp)

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(15.dp),
        ) {
            items(1) { item ->
                SummaryCard()
                RiskCard(
                    riskLevel = "Acceptable",
                    riskColor = RiskColorLow,
                    points = listOf("Point 1", "Point 2", "Point 3")
                )
                RiskCard(
                    riskLevel = "Moderate Concern",
                    riskColor = RiskColorMedium,
                    points = listOf("Point 1", "Point 2")
                )
                RiskCard(
                    riskLevel = "High Risk",
                    riskColor = RiskColorHigh,
                    points = listOf("Point 1", "Point 2", "Point 3", "Point 4")
                )
            }
        }
    }
}
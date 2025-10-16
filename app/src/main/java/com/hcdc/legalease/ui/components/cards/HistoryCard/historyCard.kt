package com.hcdc.legalease.ui.components.cards.HistoryCard

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
// Import for baselineShift:
import androidx.compose.ui.text.style.BaselineShift

@Composable
fun HistoryCard(
    title: String,
    // Modification Start: Add contractTypeShort parameter
    contractTypeShort: String,
    // Modification End
    onClick: () -> Unit,
    onLongPress: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { onClick() },         // handle regular tap
                    onLongPress = { onLongPress() } // handle long press
                )
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Modification Start: Use Annotated String for superscript effect
            val annotatedTitle = buildAnnotatedString {
                // Main title text
                withStyle(
                    style = MaterialTheme.typography.titleMedium.toSpanStyle().copy(
                        color = MaterialTheme.colorScheme.onBackground
                    )
                ) {
                    append(title)
                }

                if (contractTypeShort.isNotEmpty()) {
                    // Small space before the superscript
                    append(" ")

                    // Superscript style
                    withStyle(
                        style = SpanStyle(
                            color = MaterialTheme.colorScheme.primary, // Use primary color for visibility
                            fontSize = 12.sp, // Smaller font size
                            fontWeight = FontWeight.Bold,
                            baselineShift = BaselineShift.Superscript // Move baseline up
                        )
                    ) {
                        append(contractTypeShort)
                    }
                }
            }

            Text(
                text = annotatedTitle,
                modifier = Modifier.weight(1f)
            )
            // Modification End

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = "Next",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}
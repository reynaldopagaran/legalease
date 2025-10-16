package com.hcdc.legalease.ui.screens.dashboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.hcdc.legalease.R
import com.hcdc.legalease.ui.components.cards.DashboardCard
import com.hcdc.legalease.ui.components.spacers.OfflineInference
import com.hcdc.legalease.ui.components.spacers.VerticalSpacer

@Composable
fun DashboardScreen(navController: NavController) {
    val user = FirebaseAuth.getInstance().currentUser
    val displayName = user?.displayName ?: "Guest"
    val photoUrl = user?.photoUrl

    val dashboardViewModel : DashboardViewModel = viewModel()

    // 👇 State to control the dialog visibility
    var showLogoutDialog by remember { mutableStateOf(false) }
    val isLoading by dashboardViewModel.isLoading.collectAsState()
    val context = LocalContext.current
    val offlineInference = OfflineInference(context)


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp, vertical = 20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            if (photoUrl != null) {
                AsyncImage(
                    model = photoUrl,
                    contentDescription = "User Profile Image",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Image(
                    painter = androidx.compose.ui.res.painterResource(id = R.drawable.avatar),
                    contentDescription = "Default Avatar",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                )
            }

            IconButton(onClick = { showLogoutDialog = true }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Logout,
                    contentDescription = "Logout",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(30.dp)
                )
            }
        }

        VerticalSpacer(height = 15.dp)
        Text(
            text = displayName,
            style = MaterialTheme.typography.displayLarge.copy(
                color = MaterialTheme.colorScheme.primary
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth(1f)
        )

        VerticalSpacer(height = 5.dp)
        Text(
            text = "Ready to explore your documents?",
            style = MaterialTheme.typography.bodyLarge
        )
        VerticalSpacer(height = 30.dp)
        HorizontalDivider(
            color = Color.Gray.copy(alpha = .3f),
            thickness = 1.dp,
        )
        VerticalSpacer(height = 33.dp)
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            DashboardCard(
                title = "Upload PDF",
                subtitle = "Scan and analyze legal documents",
                imageRes = R.drawable.upload_pdf,
                onClick = { navController.navigate("pdf") },
                modifier = Modifier.weight(.5f)
            )
            DashboardCard(
                title = "Upload Image",
                subtitle = "Snap, upload, and understand",
                imageRes = R.drawable.upload_image,
                onClick = { navController.navigate("image") },
                modifier = Modifier.weight(.5f)
            )
            HorizontalDivider(
                color = Color.Gray.copy(alpha = .3f),
                thickness = 1.dp,
            )
            DashboardCard(
                title = "My Scans",
                subtitle = "History of your scanned files and reviews.",
                imageRes = R.drawable.history,
                onClick = { navController.navigate("history")
                },
                modifier = Modifier.weight(.5f)
            )
        }
    }

    if(!offlineInference.isModelInitialized()){
        dashboardViewModel.setIsLoading(true)
        LaunchedEffect(Unit) {
            offlineInference.addVSpace()
            dashboardViewModel.setIsLoading(false)
        }
    }else{
        dashboardViewModel.setIsLoading(false)
    }

    // 👇 Logout confirmation dialog
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = {
                Text(
                    "Logout",
                    style = MaterialTheme.typography.headlineMedium, // uses RobotoSlab Medium
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            text = {
                Text(
                    "Are you sure you want to logout?",
                    style = MaterialTheme.typography.bodyLarge, // Inter
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        FirebaseAuth.getInstance().signOut()
                        navController.navigate("home") {
                            popUpTo("dashboard") { inclusive = true }
                        }
                        showLogoutDialog = false
                    }
                ) {
                    Text(
                        "Yes",
                        style = MaterialTheme.typography.titleMedium, // RobotoSlab Medium
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showLogoutDialog = false }
                ) {
                    Text(
                        "Cancel",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,  // dialog background
            tonalElevation = 6.dp                               // subtle shadow
        )

        LoadingDialog(show = isLoading, message = "Loading model...")

    }
}

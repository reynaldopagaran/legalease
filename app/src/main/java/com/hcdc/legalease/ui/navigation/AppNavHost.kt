package com.hcdc.legalease.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.google.android.gms.common.api.internal.BaseImplementation.ResultHolder
import com.hcdc.legalease.ui.screens.dashboard.DashboardScreen
import com.hcdc.legalease.ui.screens.history.HistoryScreen
import com.hcdc.legalease.ui.screens.intro.IntroScreen
import com.hcdc.legalease.ui.screens.result.ResultScreen
import com.hcdc.legalease.ui.screens.resultHistory.ResultHistoryScreen
import com.hcdc.legalease.ui.screens.upload_image.ImageScreen
import com.hcdc.legalease.ui.screens.upload_pdf.PDFScreen

@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "home") {
        composable("home") { IntroScreen(navController) }
        composable("dashboard") { DashboardScreen(navController) }
        composable("pdf") { PDFScreen(navController) }
        composable("image") { ImageScreen(navController) }
        composable("history") { HistoryScreen(navController) }

        composable(
            route = "result_history/{myID}",
            arguments = listOf(navArgument("myID") {
                type = NavType.StringType
            })
        ) { backStackEntry ->
            val myID = backStackEntry.arguments?.getString("myID") ?: ""
            ResultHistoryScreen(navController = navController, myID = myID)
        }

        composable(
            route = "result/{ocrText}",
            arguments = listOf(navArgument("ocrText") {
                type = NavType.StringType
            })
        ) { backStackEntry ->
            val ocrText = backStackEntry.arguments?.getString("ocrText") ?: ""
            ResultScreen(navController = navController, ocrText = ocrText)
        }
    }
}

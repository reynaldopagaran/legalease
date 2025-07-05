package com.example.legalease.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.legalease.ui.screens.dashboard.DashboardScreen
import com.example.legalease.ui.screens.history.HistoryScreen
import com.example.legalease.ui.screens.intro.IntroScreen
import com.example.legalease.ui.screens.result.ResultScreen
import com.example.legalease.ui.screens.upload_image.ImageScreen
import com.example.legalease.ui.screens.upload_pdf.PDFScreen

@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "home") {
        composable("home") { IntroScreen(navController) }
        composable("dashboard") { DashboardScreen(navController) }
        composable("pdf") { PDFScreen(navController, viewModel()) }
        composable("image") { ImageScreen(navController, viewModel()) }
        composable("history") { HistoryScreen(navController, viewModel()) }
        composable("result") { ResultScreen(navController, viewModel()) }
    }
}

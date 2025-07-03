package com.example.legalease

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.legalease.ui.components.screens.intro.IntroScreen
import com.example.legalease.ui.components.screens.intro.IntroViewModel
import com.legalease.ui.theme.LegalEaseTheme
import com.legalease.viewmodel.BottomSheetViewModel

class MainActivity : ComponentActivity() {
    @SuppressLint("ViewModelConstructorInComposable")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LegalEaseTheme {
                val introViewModel: IntroViewModel = viewModel()
                val sheetViewModel: BottomSheetViewModel = viewModel()
                Scaffold { innerPadding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .padding(WindowInsets.systemBars.asPaddingValues())
                    ) {

                        IntroScreen(
                            viewModel = introViewModel,
                            sheetViewModel = sheetViewModel
                        )
                    }
                }

            }
        }
    }
}



package com.example.legalease.ui.components.bottomSheet

sealed class BottomSheetContent {
    object Login : BottomSheetContent()
    object Register : BottomSheetContent()
    object ForgotPassword : BottomSheetContent()
}
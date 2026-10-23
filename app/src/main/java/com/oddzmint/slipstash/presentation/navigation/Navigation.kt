package com.oddzmint.slipstash.presentation.navigation

import androidx.navigation.NavController

fun NavController.navigateToTab(destination: Destination) {
    navigate(destination.route) {
        popUpTo(Destination.Expenses.route) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}
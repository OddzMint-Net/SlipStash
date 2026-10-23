package com.oddzmint.slipstash.presentation.navigation

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

@Composable
fun SlipStashRoot() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val showBottomBar = TopLevelTab.entries.any { it.destination.route == currentRoute }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0),
        bottomBar = {
            if (showBottomBar) {
                SlipStashBottomBar(
                    currentRoute = currentRoute,
                    onTabSelected = { navController.navigateToTab(it) }
                )
            }
        }
    ) { innerPadding ->
        SlipStashNavHost(
            navController = navController,
            modifier = Modifier
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
        )
    }
}
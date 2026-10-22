package com.oddzmint.slipstash.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.oddzmint.slipstash.presentation.add.AddExpenseScreen
import com.oddzmint.slipstash.presentation.list.ExpenseListScreen
import com.oddzmint.slipstash.presentation.summary.SummaryScreen

private const val ROUTE_LIST = "list"
private const val ROUTE_ADD = "add"
private const val ROUTE_SUMMARY = "summary"

private data class Tab(val route: String, val label: String)

private val tabs = listOf(
    Tab(ROUTE_LIST, "Expenses"),
    Tab(ROUTE_SUMMARY, "Summary")
)

@Composable
fun SlipStashNavHost() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val showBottomBar = tabs.any { it.route == currentRoute }

    fun switchTab(route: String) {
        navController.navigate(route) {
            popUpTo(ROUTE_LIST) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    tabs.forEach { tab ->
                        NavigationBarItem(
                            selected = currentRoute == tab.route,
                            onClick = { if (currentRoute != tab.route) switchTab(tab.route) },
                            icon = { TabIcon(tab.route) },
                            label = { Text(tab.label) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = ROUTE_LIST,
            modifier = Modifier
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
        ) {
            composable(ROUTE_LIST) {
                ExpenseListScreen(onAddClick = { navController.navigate(ROUTE_ADD) })
            }

            composable(ROUTE_SUMMARY) {
                SummaryScreen(onViewQueue = { switchTab(ROUTE_LIST) })
            }
            composable(ROUTE_ADD) {
                AddExpenseScreen(onBack = { navController.popBackStack() })
            }
        }
    }
}

@Composable
private fun TabIcon(route: String) {
    if (route == ROUTE_LIST) {
        Icon(Icons.AutoMirrored.Filled.List, contentDescription = null)
    } else {
        BarsIcon()
    }
}

@Composable
private fun BarsIcon() {
    val color = LocalContentColor.current
    Row(
        modifier = Modifier
            .size(24.dp)
            .padding(horizontal = 3.dp),
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        listOf(10.dp, 18.dp, 14.dp).forEach { barHeight ->
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(barHeight)
                    .clip(RoundedCornerShape(1.dp))
                    .background(color)
            )
        }
    }
}
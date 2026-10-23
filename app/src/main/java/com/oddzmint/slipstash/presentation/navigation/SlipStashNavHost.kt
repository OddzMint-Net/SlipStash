package com.oddzmint.slipstash.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.oddzmint.slipstash.presentation.add.AddExpenseScreen
import com.oddzmint.slipstash.presentation.list.ExpenseListScreen
import com.oddzmint.slipstash.presentation.summary.SummaryScreen

@Composable
fun SlipStashNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Destination.Expenses.route,
        modifier = modifier
    ) {
        composable(Destination.Expenses.route) {
            ExpenseListScreen(
                onAddClick = { navController.navigate(Destination.AddExpense.route) }
            )
        }

        composable(Destination.Summary.route) {
            SummaryScreen(
                onViewQueue = { navController.navigateToTab(Destination.Expenses) }
            )
        }

        composable(Destination.AddExpense.route) {
            AddExpenseScreen(onBack = { navController.popBackStack() })
        }
    }
}
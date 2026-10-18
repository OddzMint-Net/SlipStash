package com.oddzmint.slipstash.presentation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.oddzmint.slipstash.presentation.add.AddExpenseScreen
import com.oddzmint.slipstash.presentation.list.ExpenseListScreen

private const val ROUTE_LIST = "list"
private const val ROUTE_ADD = "add"

@Composable
fun SlipStashNavHost() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = ROUTE_LIST) {
        composable(ROUTE_LIST) {
            ExpenseListScreen(onAddClick = { navController.navigate(ROUTE_ADD) })
        }
        composable(ROUTE_ADD) {
            AddExpenseScreen(onBack = { navController.popBackStack() })
        }
    }
}
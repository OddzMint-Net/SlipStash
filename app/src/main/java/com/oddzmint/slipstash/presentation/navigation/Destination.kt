package com.oddzmint.slipstash.presentation.navigation

sealed class Destination(val route: String) {
    data object Expenses : Destination("expenses")
    data object Summary : Destination("summary")
    data object AddExpense : Destination("add_expense")
}
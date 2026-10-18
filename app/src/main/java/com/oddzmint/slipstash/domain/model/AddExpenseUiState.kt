package com.oddzmint.slipstash.domain.model

data class AddExpenseUiState(
    val amountText: String = "",
    val category: Category = Category.GROCERIES,
    val note: String = "",
    val error: String? = null,
    val saved: Boolean = false
)
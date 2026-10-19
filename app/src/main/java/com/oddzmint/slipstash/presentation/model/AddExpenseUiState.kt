package com.oddzmint.slipstash.presentation.model

import com.oddzmint.slipstash.domain.model.Category

data class AddExpenseUiState(
    val amountText: String = "",
    val category: Category = Category.GROCERIES,
    val note: String = "",
    val receiptPath: String? = null,
    val error: String? = null,
    val saved: Boolean = false
)
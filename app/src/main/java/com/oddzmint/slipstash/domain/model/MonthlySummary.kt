package com.oddzmint.slipstash.domain.model

import java.time.YearMonth

data class CategoryTotal(
    val category: Category,
    val amountCents: Long,
    val percentage: Int
)

data class MonthlySummary(
    val month: YearMonth,
    val totalCents: Long,
    val expenseCount: Int,
    val receiptCount: Int,
    val syncedCount: Int,
    val waitingCount: Int,
    val categories: List<CategoryTotal>
)
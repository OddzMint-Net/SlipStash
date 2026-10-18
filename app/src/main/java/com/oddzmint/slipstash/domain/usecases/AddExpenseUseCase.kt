package com.oddzmint.slipstash.domain.usecases

import com.oddzmint.slipstash.domain.model.Category
import com.oddzmint.slipstash.domain.model.Expense
import com.oddzmint.slipstash.domain.model.SyncStatus
import com.oddzmint.slipstash.domain.repository.ExpenseRepository
import javax.inject.Inject

class AddExpenseUseCase @Inject constructor(
    private val repository: ExpenseRepository
) {
    suspend operator fun invoke(
        amountCents: Long,
        category: Category,
        note: String = "",
        receiptLocalPath: String? = null,
        dateMillis: Long = System.currentTimeMillis()
    ): Result<Unit> {
        if (amountCents <= 0) {
            return Result.failure(IllegalArgumentException("Amount must be greater than zero"))
        }

        val expense = Expense(
            amountCents = amountCents,
            category = category,
            note = note.trim(),
            receiptLocalPath = receiptLocalPath,
            dateMillis = dateMillis,
            syncStatus = SyncStatus.PENDING
        )
        return runCatching { repository.saveExpense(expense) }

    }
}
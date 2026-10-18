package com.oddzmint.slipstash.domain.usecases

import com.oddzmint.slipstash.domain.model.Expense
import com.oddzmint.slipstash.domain.repository.ExpenseRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveExpensesUseCase @Inject constructor(
    private val repository: ExpenseRepository
) {
    operator fun invoke(): Flow<List<Expense>> = repository.observeExpenses()
}
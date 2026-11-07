package com.oddzmint.slipstash.domain.usecases

import com.oddzmint.slipstash.domain.repository.ExpenseRepository
import com.oddzmint.slipstash.domain.repository.SyncScheduler
import javax.inject.Inject

class DeleteExpenseUseCase @Inject constructor(
    private val repository: ExpenseRepository,
    private val scheduler: SyncScheduler
) {

    suspend operator fun invoke(id: String) {
        repository.markDeleted(id)
        scheduler.schedule(id, hasReceipt = false)
    }
}
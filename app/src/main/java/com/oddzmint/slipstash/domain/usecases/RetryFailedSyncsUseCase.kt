package com.oddzmint.slipstash.domain.usecases

import com.oddzmint.slipstash.domain.repository.ExpenseRepository
import com.oddzmint.slipstash.domain.repository.SyncScheduler
import javax.inject.Inject

class RetryFailedSyncsUseCase @Inject constructor(
    private val repository: ExpenseRepository,
    private val scheduler: SyncScheduler
) {
    /** Re-queues every expense that is still PENDING or FAILED. Returns how many were checked. */
    suspend operator fun invoke(): Int {
        val unsynced = repository.getUnsyncedExpense()
        unsynced.forEach { expense ->
            // Only run the compress/upload chain if there is a photo that hasn't been uploaded yet
            val needsReceiptUpload = expense.receiptLocalPath != null && expense.receiptRemoteUrl == null
            scheduler.schedule(expense.id, hasReceipt = needsReceiptUpload)
        }
        return unsynced.size
    }
}
package com.oddzmint.slipstash.domain.usecases

import com.oddzmint.slipstash.domain.model.SyncStatus
import com.oddzmint.slipstash.domain.repository.AuthRepository
import com.oddzmint.slipstash.domain.repository.ExpenseRemoteRepository
import com.oddzmint.slipstash.domain.repository.ExpenseRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import javax.inject.Inject

class SyncExpenseUseCase @Inject constructor(
    private val repository: ExpenseRepository,
    private val remote: ExpenseRemoteRepository,
    private val auth: AuthRepository
) {
    suspend operator fun invoke(id: String): Result<Unit> {
        val expense = repository.getExpense(id) ?: return Result.success(Unit)
        repository.updateSyncStatus(id, SyncStatus.SYNCING)
        return runCatching {
            val userId = auth.ensureSignedIn()
            remote.upload(userId, expense)
        }.onSuccess {
            repository.updateSyncStatus(id, SyncStatus.SYNCED)
        }.onFailure { error ->
            if (error is CancellationException && error !is TimeoutCancellationException) throw error
            repository.updateSyncStatus(id, SyncStatus.FAILED)
        }
    }
}
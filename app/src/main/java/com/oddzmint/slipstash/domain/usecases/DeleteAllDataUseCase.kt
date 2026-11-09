package com.oddzmint.slipstash.domain.usecases

import com.oddzmint.slipstash.domain.repository.AccountDataRepository
import com.oddzmint.slipstash.domain.repository.SyncScheduler
import kotlinx.coroutines.TimeoutCancellationException
import java.util.concurrent.CancellationException
import javax.inject.Inject

class DeleteAllDataUseCase @Inject constructor(
    private val scheduler: SyncScheduler,
    private val accountData: AccountDataRepository
) {
    suspend operator fun invoke(): Result<Unit> {
        scheduler.cancelAll()
        val result = runCatching {
            accountData.deleteRemoteData()
            accountData.deleteLocalData()
        }.onFailure { error ->
            if (error is CancellationException && error !is TimeoutCancellationException) throw error
        }
        scheduler.schedulePeriodicRetry()
        return result
    }
}
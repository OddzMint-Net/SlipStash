package com.oddzmint.slipstash.data.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.oddzmint.slipstash.domain.usecases.SyncExpenseUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class SyncExpenseWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val syncExpense: SyncExpenseUseCase
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val id = inputData.getString(KEY_EXPENSE_ID) ?: return Result.failure()

        return syncExpense(id).fold(
            onSuccess = { Result.success() },
            onFailure = { if (runAttemptCount < MAX_ATTEMPT) Result.retry() else Result.failure() }
        )
    }

    companion object {
        const val KEY_EXPENSE_ID = "expense_id"
        private val MAX_ATTEMPT = 5
    }
}
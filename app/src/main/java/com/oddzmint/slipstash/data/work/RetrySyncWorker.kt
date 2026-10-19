package com.oddzmint.slipstash.data.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.oddzmint.slipstash.domain.usecases.RetryFailedSyncsUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class RetrySyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val retryFailedSyncs: RetryFailedSyncsUseCase
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        retryFailedSyncs()
        return Result.success()
    }
}
package com.oddzmint.slipstash.data.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.oddzmint.slipstash.domain.model.SyncStatus
import com.oddzmint.slipstash.domain.repository.AuthRepository
import com.oddzmint.slipstash.domain.repository.ExpenseRepository
import com.oddzmint.slipstash.domain.repository.ReceiptStorage
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import java.io.File

@HiltWorker
class UploadReceiptWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val repository: ExpenseRepository,
    private val receiptStorage: ReceiptStorage,
    private val auth: AuthRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val id = inputData.getString(SyncExpenseWorker.KEY_EXPENSE_ID) ?: return Result.failure()
        val path = inputData.getString(CompressReceiptWorker.KEY_COMPRESSED_PATH)
            ?: return Result.success()   // nothing to upload
        val file = File(path)
        if (!file.exists()) return Result.success()

        return runCatching {
            val userId = auth.ensureSignedIn()
            val reference = receiptStorage.upload(userId, id, file.readBytes())
            repository.updateReceiptRemoteUrl(id, reference)
            file.delete()
        }.fold(
            onSuccess = { Result.success() },
            onFailure = { error ->
                if (error is CancellationException && error !is TimeoutCancellationException) throw error
                if (runAttemptCount < MAX_ATTEMPTS) {
                    Result.retry()
                } else {
                    repository.updateSyncStatus(id, SyncStatus.FAILED)
                    Result.failure()
                }
            }
        )
    }

    private companion object {
        const val MAX_ATTEMPTS = 5
    }
}
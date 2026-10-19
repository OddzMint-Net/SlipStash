package com.oddzmint.slipstash.data.work

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.oddzmint.slipstash.domain.repository.SyncScheduler
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class WorkManagerSyncScheduler @Inject constructor(
    @ApplicationContext private val context: Context
) : SyncScheduler {

    override fun schedule(expenseId: String, hasReceipt: Boolean) {
        val idData = workDataOf(SyncExpenseWorker.KEY_EXPENSE_ID to expenseId)
        val needsNetwork = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val sync = OneTimeWorkRequestBuilder<SyncExpenseWorker>()
            .setInputData(idData)
            .setConstraints(needsNetwork)
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 30, TimeUnit.SECONDS)
            .build()

        val manager = WorkManager.getInstance(context)
        val uniqueName = "sync_$expenseId"

        if (!hasReceipt) {
            manager.enqueueUniqueWork(uniqueName, ExistingWorkPolicy.KEEP, sync)
            return
        }

        val compress = OneTimeWorkRequestBuilder<CompressReceiptWorker>()
            .setInputData(idData)
            .build()

        val upload = OneTimeWorkRequestBuilder<UploadReceiptWorker>()
            .setInputData(idData)
            .setConstraints(needsNetwork)
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 30, TimeUnit.SECONDS)
            .build()

        manager.beginUniqueWork(uniqueName, ExistingWorkPolicy.KEEP, compress)
            .then(upload)
            .then(sync)
            .enqueue()
    }
}
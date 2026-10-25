package com.oddzmint.slipstash.data.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.oddzmint.slipstash.domain.repository.ExpenseRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.io.File

@HiltWorker
class CompressReceiptWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val repository: ExpenseRepository,
    private val compressor: ReceiptCompressor
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val id = inputData.getString(SyncExpenseWorker.KEY_EXPENSE_ID) ?: return Result.failure()
        val expense = repository.getExpense(id) ?: return Result.failure()
        if (expense.receiptRemoteUrl != null) return Result.success() //already uploaded: nothing to compress
        val source = expense.receiptLocalPath ?: return Result.success()   // no photo: skip

        val target = File(applicationContext.filesDir, "receipts_compressed/$id.jpg")
        return if (compressor.compress(source, target)) {
            Result.success(workDataOf(KEY_COMPRESSED_PATH to target.absolutePath))
        } else {
            Result.success()   // unreadable photo: sync the expense without it
        }
    }

    companion object {
        const val KEY_COMPRESSED_PATH = "compressed_path"
    }
}
package com.oddzmint.slipstash

import com.oddzmint.slipstash.domain.model.Category
import com.oddzmint.slipstash.domain.model.Expense
import com.oddzmint.slipstash.domain.model.SyncStatus
import com.oddzmint.slipstash.domain.usecases.RetryFailedSyncsUseCase
import com.oddzmint.slipstash.fakes.FakeExpenseRepository
import com.oddzmint.slipstash.fakes.FakeSyncScheduler
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class RetryFailedSyncsUseCaseTest {
    private lateinit var repository: FakeExpenseRepository
    private lateinit var scheduler: FakeSyncScheduler
    private lateinit var retryFailedSyncs: RetryFailedSyncsUseCase

    @Before
    fun setUp() {
        repository = FakeExpenseRepository()
        scheduler = FakeSyncScheduler()
        retryFailedSyncs = RetryFailedSyncsUseCase(repository, scheduler)
    }

    private fun expense(
        status: SyncStatus,
        localPath: String? = null,
        removeUrl: String? = null
    ) = Expense(
        amountCents = 1_000,
        category = Category.OTHER,
        receiptLocalPath = localPath,
        receiptRemoteUrl = removeUrl,
        dateMillis = 0L,
        syncStatus = status,
        isDeleted = false
    )

    @Test
    fun `pending and failed expenses are re-queued`() = runTest {
        val pending = expense(SyncStatus.PENDING)
        val failed = expense(SyncStatus.FAILED)
        repository.saveExpense(pending)
        repository.saveExpense(failed)

        retryFailedSyncs()
        val queuedIds = scheduler.calls.map { it.expenseId }.toSet()
        assertEquals(setOf(pending.id, failed.id), queuedIds)
    }

    @Test
    fun `receipt chain is requested only when the photo has not been uploaded`() = runTest {
        val needsUpload = expense(SyncStatus.FAILED, localPath = "/files/a/jpeg")
        val alreadyUploaded = expense(
            SyncStatus.FAILED, localPath = "/files/b.jpg", removeUrl = "users/u/receipts/b"
        )
        val noPhoto = expense(SyncStatus.FAILED)
        listOf(needsUpload, alreadyUploaded, noPhoto).forEach { repository.saveExpense(it) }
        retryFailedSyncs()

        val byId = scheduler.calls.associateBy { it.expenseId }
        assertEquals(true, byId.getValue(needsUpload.id).hasReceipt)
        assertEquals(false, byId.getValue(alreadyUploaded.id).hasReceipt)
        assertEquals(false, byId.getValue(noPhoto.id).hasReceipt)
    }

    @Test
    fun `return how many expenses were checked`() = runTest {
        repository.saveExpense(expense(SyncStatus.PENDING))
        repository.saveExpense(expense(SyncStatus.FAILED))
        repository.saveExpense(expense(SyncStatus.SYNCED))

        assertEquals(2, retryFailedSyncs())
    }

    @Test
    fun `synced expense are left alone`() = runTest {
        repository.saveExpense(expense(SyncStatus.SYNCED))
        retryFailedSyncs()
        assertTrue(scheduler.calls.isEmpty())
    }

    @Test
    fun `interrupted SYNCING are re-queued`() = runTest {
        val stuck = expense(SyncStatus.SYNCING)
        repository.saveExpense(stuck)
        retryFailedSyncs()
        assertEquals(listOf(stuck.id), scheduler.calls.map { it.expenseId })


    }
}
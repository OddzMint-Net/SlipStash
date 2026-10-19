package com.oddzmint.slipstash

import com.oddzmint.slipstash.domain.model.Category
import com.oddzmint.slipstash.domain.model.Expense
import com.oddzmint.slipstash.domain.model.SyncStatus
import com.oddzmint.slipstash.domain.usecases.SyncExpenseUseCase
import com.oddzmint.slipstash.fakes.FakeAuthRepository
import com.oddzmint.slipstash.fakes.FakeExpenseRemoteRepository
import com.oddzmint.slipstash.fakes.FakeExpenseRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.experimental.theories.suppliers.TestedOn

class SyncExpenseUseCaseTest {

    private lateinit var repository: FakeExpenseRepository
    private lateinit var remote: FakeExpenseRemoteRepository
    private lateinit var expense: Expense

    @Before
    fun setUp() {
        repository = FakeExpenseRepository()
        remote = FakeExpenseRemoteRepository()
        expense = Expense(
            amountCents = 61_200,
            category = Category.GROCERIES,
            dateMillis = 0L,
            syncStatus = SyncStatus.PENDING
        )
    }

    private fun useCase(auth: FakeAuthRepository = FakeAuthRepository()) = SyncExpenseUseCase(repository, remote, auth)

    private suspend fun statusOf(id: String) = repository.getExpense(id)!!.syncStatus

    @Test
    fun `successful sync uploads the expense and marks it SYNCED`() = runTest {
        repository.saveExpense(expense)
        val result = useCase(FakeAuthRepository(userId = "user-42"))(expense.id)
        assertTrue(result.isSuccess)
        assertEquals(SyncStatus.SYNCED, statusOf(expense.id))
        val upload = remote.uploads.single()
        assertEquals("user-42", upload.userId)
        assertEquals(expense.id, upload.expense.id)
    }

    @Test
    fun `failed upload marks the expense FAILED and returns a failure`() = runTest {
        repository.saveExpense(expense)
        remote.onUpload = { throw RuntimeException("network down") }

        val result = useCase()(expense.id)

        assertTrue(result.isFailure)
        assertEquals(SyncStatus.FAILED, statusOf(expense.id))
    }

    @Test
    fun `failed sign-in marks the expense FAILED and never uploads`() = runTest {
        repository.saveExpense(expense)
        val result = useCase(FakeAuthRepository(failure = IllegalStateException("no user")))(expense.id)

        assertTrue(result.isFailure)
        assertEquals(SyncStatus.FAILED, statusOf(expense.id))
        assertTrue(remote.uploads.isEmpty())
    }

    @Test
    fun `expense is SYNCING while the upload is running`() = runTest {
        repository.saveExpense(expense)
        var statusDuringUpload: SyncStatus? = null
        remote.onUpload = { statusDuringUpload = statusOf(expense.id) }
        useCase()(expense.id)
        assertEquals(SyncStatus.SYNCING, statusDuringUpload)
    }

    @Test
    fun `cancellation is rethrown and not recorded as FAILED`() = runTest {
        repository.saveExpense(expense)
        remote.onUpload = { throw CancellationException("worker stopped") }
        var rethrown = false
        try {
            useCase()(expense.id)
        } catch (e: CancellationException) {
            rethrown = true
        }
        assertTrue(rethrown)
        assertEquals(SyncStatus.SYNCING, statusOf(expense.id))
    }
}
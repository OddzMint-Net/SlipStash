package com.oddzmint.slipstash

import com.oddzmint.slipstash.domain.model.Category
import com.oddzmint.slipstash.domain.model.Expense
import com.oddzmint.slipstash.domain.model.SyncStatus
import com.oddzmint.slipstash.domain.usecases.DeleteExpenseUseCase
import com.oddzmint.slipstash.fakes.FakeExpenseRepository
import com.oddzmint.slipstash.fakes.FakeSyncScheduler
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.Test

class DeleteExpenseUseCaseTest {

    private val repository = FakeExpenseRepository()
    private val scheduler = FakeSyncScheduler()
    private val deleteExpense = DeleteExpenseUseCase(repository, scheduler)

    private val expense = Expense(
        amountCents = 5_000,
        category = Category.BILLS,
        dateMillis = 0L,
        syncStatus = SyncStatus.SYNCED,
        isDeleted = false
    )

    @Test
    fun `delete marks the expense deleted, queues it and hides it from the list`() = runTest {
        repository.saveExpense(expense)

        deleteExpense(expense.id)
        val row = repository.getExpense(expense.id)!!
        assertTrue(row.isDeleted)
        assertEquals(SyncStatus.PENDING, row.syncStatus)
        assertEquals(listOf(expense.id), scheduler.calls.map { it.expenseId })
        assertEquals(false, scheduler.calls.single().hasReceipt)
    }

    @Test
    fun `deleting twice is harmless`() = runTest {
        repository.saveExpense(expense)

        deleteExpense(expense.id)
        deleteExpense(expense.id)

        assertTrue(repository.getExpense(expense.id)!!.isDeleted)
        assertEquals(1, repository.saved.size)
    }
}
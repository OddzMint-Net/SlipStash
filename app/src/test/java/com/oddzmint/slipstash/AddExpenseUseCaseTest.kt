package com.oddzmint.slipstash

import com.oddzmint.slipstash.domain.model.Category
import com.oddzmint.slipstash.domain.model.SyncStatus
import com.oddzmint.slipstash.domain.usecases.AddExpenseUseCase
import com.oddzmint.slipstash.fakes.FakeExpenseRepository
import com.oddzmint.slipstash.fakes.FakeSyncScheduler
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class AddExpenseUseCaseTest {

    private lateinit var repository: FakeExpenseRepository
    private lateinit var scheduler: FakeSyncScheduler
    private lateinit var addExpense: AddExpenseUseCase

    @Before
    fun setUp() {
        repository = FakeExpenseRepository()
        scheduler = FakeSyncScheduler()
        addExpense = AddExpenseUseCase(repository, scheduler)
    }

    @Test
    fun zero_amount_is_rejected_and_nothing_is_saved_or_scheduled() = runTest {
        val result = addExpense(amountCents = 0, category = Category.GROCERIES)

        Assert.assertTrue(result.isFailure)
        Assert.assertTrue(repository.saved.isEmpty())
        Assert.assertTrue(scheduler.calls.isEmpty())
    }

    @Test
    fun valid_expense_is_saved_as_PENDING_with_a_trimmed_note() = runTest {
        val result = addExpense(
            amountCents = 61_2000,
            category = Category.GROCERIES,
            note = "  Weekly shop "
        )
        Assert.assertTrue(result.isSuccess)
        val saved = repository.saved.single()
        Assert.assertEquals(61_2000L, saved.amountCents)
        Assert.assertEquals("Weekly shop", saved.note)
        Assert.assertEquals(SyncStatus.PENDING, saved.syncStatus)
    }

    @Test
    fun saving_schedules_a_sync_without_receipt_work_when_there_is_no_photo() = runTest {
        addExpense(amountCents = 5_000, category = Category.BILLS)
        val call = scheduler.calls.single()
        Assert.assertEquals(repository.saved.single().id, call.expenseId)
        Assert.assertEquals(false, call.hasReceipt)
    }

    @Test
    fun `saving with a photo schedules the receipt chain`() = runTest {
        addExpense(
            amountCents = 5_000,
            category = Category.BILLS,
            receiptLocalPath = "/files/receipt/a.jpg"
        )

        Assert.assertEquals(true, scheduler.calls.single().hasReceipt)
    }
}
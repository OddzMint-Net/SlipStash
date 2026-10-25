package com.oddzmint.slipstash

import com.oddzmint.slipstash.domain.model.Category
import com.oddzmint.slipstash.domain.model.Expense
import com.oddzmint.slipstash.domain.model.SyncStatus
import com.oddzmint.slipstash.domain.usecases.GetMonthlySummaryUseCase
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneOffset

class GetMonthlySummaryUseCaseTest {

    private val zone = ZoneOffset.UTC
    private val october = YearMonth.of(2026, 10)
    private val summary = GetMonthlySummaryUseCase()

    private fun expense(
        cents: Long,
        category: Category = Category.GROCERIES,
        date: LocalDate = LocalDate.of(2026, 10, 22),
        status: SyncStatus = SyncStatus.SYNCED,
        receipt: String? = null
    ) = Expense(
        amountCents = cents,
        category = category,
        receiptLocalPath = receipt,
        dateMillis = date.atStartOfDay(zone).toInstant().toEpochMilli(),
        syncStatus = status,
        isDeleted = false
    )

    @Test
    fun `empty month gives zeros and no categories`() {
        val result = summary(emptyList(), october, zone)

        assertEquals(0L, result.totalCents)
        assertEquals(0, result.expenseCount)
        assertTrue(result.categories.isEmpty())
    }

    @Test
    fun `only expenses from the chosen month are counted`() {
        val expenses = listOf(
            expense(1_000),
            expense(2_000, date = LocalDate.of(2026, 9, 30)),
            expense(2_000, date = LocalDate.of(2026, 11, 1))
        )
        val result = summary(expenses, october, zone)
        assertEquals(1_000L, result.totalCents)
        assertEquals(1, result.expenseCount)
    }

    @Test
    fun `categories are sorted from largest to smallest`() {
        val expenses = listOf(
            expense(1_000, Category.BILLS),
            expense(5_000, Category.GROCERIES),
            expense(3_000, Category.TRANSPORT)
        )

        val order = summary(expenses, october, zone).categories.map { it.category }
        assertEquals(listOf(Category.GROCERIES, Category.TRANSPORT, Category.BILLS), order)
    }

    @Test
    fun `percentage always add up to 100`() {
        val expenses = listOf(
            expense(1_000, Category.GROCERIES),
            expense(1_000, Category.BILLS),
            expense(1_000, Category.TRANSPORT)

        )
        val percents = summary(expenses, october, zone).categories.map { it.percentage }
        assertEquals(100, percents.sum())
        assertEquals(listOf(34, 33, 33), percents)
    }

    @Test
    fun `receipts and waiting expenses are counted`() {
        val expenses = listOf(
            expense(1_000, receipt = "/files/a.jpg"),
            expense(1_000, status = SyncStatus.PENDING),
            expense(1_000, status = SyncStatus.FAILED),
            expense(1_000, status = SyncStatus.SYNCED)
        )

        val result = summary(expenses, october, zone)
        assertEquals(2, result.waitingCount)
        assertEquals(2, result.syncedCount)
    }
}
package com.oddzmint.slipstash.data

import com.oddzmint.slipstash.util.range
import java.time.YearMonth

class ExpenseRepository(private val dao: ExpenseDao) {

    fun expenses() = dao.observeAll()
    fun unsyncedCount() = dao.observeUnsyncedCount()

    fun monthTotal(month: YearMonth) =
        month.range().let { (s, e) -> dao.observeMonthTotal(s, e) }

    fun categoryTotals(month: YearMonth) =
        month.range().let { (s, e) -> dao.observeCategoryTotals(s, e) }

    suspend fun add(title: String, amountCents: Long, category: Category, receiptPath: String?) {
        dao.insert(
            ExpenseEntity(
                title = title,
                amountCents = amountCents,
                category = category,
                expenseDate = System.currentTimeMillis(),
                receiptLocalPath = receiptPath
            )
        )
        // Step 2: enqueue the WorkManager chain here
    }

    suspend fun delete(id: String) = dao.delete(id)

    suspend fun debugSetStatus(id: String, status: SyncStatus) = dao.setStatus(id, status)
}
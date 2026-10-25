package com.oddzmint.slipstash.fakes

import com.oddzmint.slipstash.domain.model.Expense
import com.oddzmint.slipstash.domain.model.SyncStatus
import com.oddzmint.slipstash.domain.repository.ExpenseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class FakeExpenseRepository : ExpenseRepository {

    private val items = MutableStateFlow<List<Expense>>(emptyList())
    val saved: List<Expense> get() = items.value

    override fun observeExpenses(): Flow<List<Expense>> = items

    override suspend fun getExpense(id: String): Expense? = items.value.firstOrNull() { it.id == id }

    override suspend fun saveExpense(expense: Expense) {
        items.update { list -> list.filterNot { it.id == expense.id } + expense }
    }

    override suspend fun updateSyncStatus(id: String, status: SyncStatus) {
        items.update { list ->
            list.map { if (it.id == id) it.copy(syncStatus = status) else it }
        }
    }

    override suspend fun getUnsyncedExpense(): List<Expense> = items.value.filter {
        it.syncStatus != SyncStatus.SYNCED
    }

    override suspend fun updateReceiptRemoteUrl(id: String, url: String?) {
        items.update { list ->
            list.map { if (it.id == id) it.copy(receiptRemoteUrl = url) else it }
        }
    }
}
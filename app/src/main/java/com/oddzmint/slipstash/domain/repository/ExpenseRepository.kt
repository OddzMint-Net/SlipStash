package com.oddzmint.slipstash.domain.repository

import com.oddzmint.slipstash.domain.model.Expense
import com.oddzmint.slipstash.domain.model.SyncStatus
import kotlinx.coroutines.flow.Flow

interface ExpenseRepository {
    fun observeExpenses(): Flow<List<Expense>>
    suspend fun getExpense(id: String): Expense?
    suspend fun saveExpense(expense: Expense)
    suspend fun updateSyncStatus(id: String, status: SyncStatus)
    suspend fun getUnsyncedExpense(): List<Expense>
    suspend fun updateReceiptRemoteUrl(id: String, url: String?)
}
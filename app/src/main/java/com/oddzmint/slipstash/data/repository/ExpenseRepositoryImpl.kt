package com.oddzmint.slipstash.data.repository

import com.oddzmint.slipstash.data.local.ExpenseDao
import com.oddzmint.slipstash.data.local.toDomain
import com.oddzmint.slipstash.data.local.toEntity
import com.oddzmint.slipstash.domain.model.Expense
import com.oddzmint.slipstash.domain.model.SyncStatus
import com.oddzmint.slipstash.domain.repository.ExpenseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ExpenseRepositoryImpl @Inject constructor(
    private val dao: ExpenseDao
) : ExpenseRepository {

    override fun observeExpenses(): Flow<List<Expense>> =
        dao.observeAll().map { list -> list.map { it.toDomain() } }


    override suspend fun getExpense(id: String): Expense? =
        dao.getById(id)?.toDomain()

    override suspend fun saveExpense(expense: Expense) =
        dao.upsert(expense.toEntity())

    override suspend fun updateSyncStatus(id: String, status: SyncStatus) =
        dao.updateSyncStatus(id, status)

    override suspend fun getUnsyncedExpense(): List<Expense> =
        dao.getByStatuses(listOf(SyncStatus.PENDING, SyncStatus.SYNCING, SyncStatus.FAILED))
            .map { it.toDomain() }

    override suspend fun updateReceiptRemoteUrl(id: String, url: String?) =
        dao.updateReceiptRemoteUrl(id, url)

    override suspend fun markDeleted(id: String) = dao.markDeleted(id, SyncStatus.PENDING, System.currentTimeMillis())

    override suspend fun purge(id: String) = dao.deletedById(id)
}
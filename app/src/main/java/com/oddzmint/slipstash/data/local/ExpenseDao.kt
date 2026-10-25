package com.oddzmint.slipstash.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.oddzmint.slipstash.domain.model.SyncStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {

    @Upsert
    suspend fun upsert(expense: ExpenseEntity)

    @Query("SELECT * FROM expenses WHERE isDeleted = 0 ORDER BY dateMillis DESC")
    fun observeAll(): Flow<List<ExpenseEntity>>

    @Query("SELECT * FROM expenses WHERE id = :id")
    suspend fun getById(id: String): ExpenseEntity?

    @Query("UPDATE expenses SET syncStatus = :status WHERE id = :id")
    suspend fun updateSyncStatus(id: String, status: SyncStatus)

    @Query("SELECT * FROM expenses WHERE syncStatus IN (:statuses)")
    suspend fun getByStatuses(statuses: List<SyncStatus>): List<ExpenseEntity>

    @Query("UPDATE expenses SET receiptRemoteUrl = :url WHERE id = :id")
    suspend fun updateReceiptRemoteUrl(id: String, url: String?)

    @Query("UPDATE expenses SET isDeleted = 1, syncStatus = :status, updatedAtMillis = :updatedAt WHERE id = :id")
    suspend fun markDeleted(id: String, status: SyncStatus, updatedAt: Long)

    @Query("DELETE FROM expenses WHERE id = :id")
    suspend fun deletedById(id: String)
}
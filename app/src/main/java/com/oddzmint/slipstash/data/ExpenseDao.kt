package com.oddzmint.slipstash.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

data class CategoryTotal(
    val category: Category,
    val totalCents: Long,
    val count: Int
)

@Dao
interface ExpenseDao {

    @Insert
    suspend fun insert(expense: ExpenseEntity)

    @Query("SELECT * FROM expenses ORDER BY expenseDate DESC, createdAt DESC")
    fun observeAll(): Flow<List<ExpenseEntity>>

    @Query(
        """
            SELECT category, SUM(amountCents) AS totalCents, COUNT(*) AS count
            FROM expenses
            WHERE expenseDate >= :startMillis AND expenseDate < :endMillis
            GROUP BY category
            ORDER BY totalCents DESC
        """
    )
    fun observeCategoryTotals(startMillis: Long, endMillis: Long): Flow<List<CategoryTotal>>

    @Query(
        """
        SELECT COALESCE(SUM(amountCents), 0) FROM expenses
        WHERE expenseDate >= :startMillis AND expenseDate < :endMillis
        """
    )
    fun observeMonthTotal(startMillis: Long, endMillis: Long): Flow<Long>

    @Query("SELECT COUNT(*) FROM expenses WHERE syncStatus != 'SYNCED'")
    fun observeUnsyncedCount(): Flow<Int>

    @Query("UPDATE expenses SET syncStatus = :status, updatedAt = :now WHERE id = :id")
    suspend fun setStatus(id: String, status: SyncStatus, now: Long = System.currentTimeMillis())

    @Query("DELETE FROM expenses WHERE id = :id")
    suspend fun delete(id: String)
}
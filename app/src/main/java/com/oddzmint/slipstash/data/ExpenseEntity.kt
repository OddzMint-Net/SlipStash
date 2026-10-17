package com.oddzmint.slipstash.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

enum class SyncStatus { PENDING, SYNCING, SYNCED, FAILED }

enum class Category { GROCERIES, BILLS, TRANSPORT, EATING_OUT, ENTERTAINMENT, OTHER }

@Entity(
    tableName = "expenses",
    indices = [Index("expenseDate"), Index("syncStatus")]
)
data class ExpenseEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val title: String,
    val amountCents: Long,
    val category: Category,
    val expenseDate: Long,
    val receiptLocalPath: String? = null,
    val receiptRemoteUrl: String? = null,
    val syncStatus: SyncStatus = SyncStatus.PENDING,
    val attemptCount: Int = 0,
    val lastError: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
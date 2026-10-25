package com.oddzmint.slipstash.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.oddzmint.slipstash.domain.model.Category
import com.oddzmint.slipstash.domain.model.SyncStatus

@Entity(
    tableName = "expenses"
)
data class ExpenseEntity(
    @PrimaryKey val id: String,
    val amountCents: Long,
    val category: Category,
    val note: String,
    val receiptLocalPath: String?,
    val receiptRemoteUrl: String?,
    val dateMillis: Long,
    val syncStatus: SyncStatus,
    @ColumnInfo(defaultValue = "0") val isDeleted: Boolean,
    @ColumnInfo(defaultValue = "0") val updatedAtMillis: Long
)
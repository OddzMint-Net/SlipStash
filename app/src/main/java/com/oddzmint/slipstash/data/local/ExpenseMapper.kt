package com.oddzmint.slipstash.data.local

import com.oddzmint.slipstash.domain.model.Expense

fun Expense.toEntity() = ExpenseEntity(
    id = id,
    amountCents = amountCents,
    category = category,
    note = note,
    receiptLocalPath = receiptLocalPath,
    receiptRemoteUrl = receiptRemoteUrl,
    dateMillis = dateMillis,
    syncStatus = syncStatus,
    isDeleted = isDeleted,
    updatedAtMillis = updatedAtMillis
)

fun ExpenseEntity.toDomain() = Expense(
    id = id,
    amountCents = amountCents,
    category = category,
    note = note,
    receiptLocalPath = receiptLocalPath,
    receiptRemoteUrl = receiptRemoteUrl,
    dateMillis = dateMillis,
    syncStatus = syncStatus,
    isDeleted = isDeleted,
    updatedAtMillis = updatedAtMillis
)
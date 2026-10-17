package com.oddzmint.slipstash.domain.model

import java.util.UUID

data class Expense(
    val id: String = UUID.randomUUID().toString(),
    val amountCents: Long,
    val category: Category,
    val note: String = "",
    val receiptLocalPath: String? = null,
    val receiptRemoteUrl: String? = null,
    val dateMillis: Long,
    val syncStatus: SyncStatus = SyncStatus.PENDING

)
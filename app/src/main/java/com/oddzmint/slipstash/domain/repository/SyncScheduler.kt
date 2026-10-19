package com.oddzmint.slipstash.domain.repository

interface SyncScheduler {
    fun schedule(expenseId: String, hasReceipt: Boolean)
}
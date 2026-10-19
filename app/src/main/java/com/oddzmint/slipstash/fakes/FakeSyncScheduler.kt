package com.oddzmint.slipstash.fakes

import com.oddzmint.slipstash.domain.repository.SyncScheduler

class FakeSyncScheduler : SyncScheduler {

    data class Call(val expenseId: String, val hasReceipt: Boolean)

    val calls = mutableListOf<Call>()

    override fun schedule(expenseId: String, hasReceipt: Boolean) {
        calls += Call(expenseId, hasReceipt)
    }

    override fun schedulePeriodicRetry() = Unit

}
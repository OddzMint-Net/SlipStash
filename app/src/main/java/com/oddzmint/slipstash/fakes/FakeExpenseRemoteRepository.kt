package com.oddzmint.slipstash.fakes

import com.oddzmint.slipstash.domain.model.Expense
import com.oddzmint.slipstash.domain.repository.ExpenseRemoteRepository

class FakeExpenseRemoteRepository : ExpenseRemoteRepository {

    data class Upload(val userId: String, val expense: Expense)

    val uploads = mutableListOf<Upload>()

    /** Runs at the moment of upload; the tests use it to inspect state or to throw. */

    var onUpload: suspend () -> Unit = {}
    override suspend fun upload(userId: String, expense: Expense) {
        onUpload()
        uploads += Upload(userId, expense)
    }
}
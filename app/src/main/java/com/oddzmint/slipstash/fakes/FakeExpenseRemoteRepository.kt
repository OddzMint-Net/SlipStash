package com.oddzmint.slipstash.fakes

import com.oddzmint.slipstash.domain.model.Expense
import com.oddzmint.slipstash.domain.repository.ExpenseRemoteRepository

class FakeExpenseRemoteRepository : ExpenseRemoteRepository {

    data class Upload(val userId: String, val expense: Expense)

    val uploads = mutableListOf<Upload>()
    val deletes = mutableListOf<String>()

    val documents = mutableMapOf<String, Expense>()
    var onUpload: suspend () -> Unit = {}

    override suspend fun upload(userId: String, expense: Expense) {
        onUpload()
        uploads += Upload(userId, expense)
        documents["$userId/${expense.id}"] = expense
    }

    override suspend fun delete(userId: String, expenseId: String) {
        onUpload()
        deletes += expenseId
        documents.remove("$userId/$expenseId")

    }
}
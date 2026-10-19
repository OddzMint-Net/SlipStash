package com.oddzmint.slipstash.domain.repository

import com.oddzmint.slipstash.domain.model.Expense

interface ExpenseRemoteRepository {
    suspend fun upload(userId: String, expense: Expense)
}
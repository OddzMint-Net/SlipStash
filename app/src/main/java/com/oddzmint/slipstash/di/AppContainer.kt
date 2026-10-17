package com.oddzmint.slipstash.di

import android.content.Context
import com.oddzmint.slipstash.data.AppDatabase

class AppContainer(context: Context) {
    private val db = AppDatabase.build(context)
    val repository = ExpenseRepository(db.expenseDao())
}
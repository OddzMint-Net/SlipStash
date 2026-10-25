package com.oddzmint.slipstash.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [ExpenseEntity::class],
    version = 2,
    exportSchema = true
)
abstract class SlipStashDatabase : RoomDatabase() {
    abstract fun expenseDao(): ExpenseDao
}
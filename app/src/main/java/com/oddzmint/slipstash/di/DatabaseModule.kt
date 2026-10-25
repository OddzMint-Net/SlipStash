package com.oddzmint.slipstash.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.oddzmint.slipstash.data.local.ExpenseDao
import com.oddzmint.slipstash.data.local.SlipStashDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    private val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE expenses ADD COLUMN isDeleted INTEGER NOT NULL DEFAULT 0")
            db.execSQL("ALTER TABLE expenses ADD COLUMN updatedAtMillis INTEGER NOT NULL DEFAULT 0")
            db.execSQL("UPDATE expenses SET updatedAtMillis = dateMillis")
        }
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): SlipStashDatabase =
        Room.databaseBuilder(context, SlipStashDatabase::class.java, "slipstash_db")
            .addMigrations(MIGRATION_1_2)
            .build()

    @Provides
    fun provideExpenseDao(database: SlipStashDatabase): ExpenseDao = database.expenseDao()
}
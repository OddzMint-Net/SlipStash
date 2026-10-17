package com.oddzmint.slipstash.di

import android.content.Context
import androidx.room.Room
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
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): SlipStashDatabase =
        Room.databaseBuilder(context, SlipStashDatabase::class.java, "slipstash_db").build()

    @Provides
    fun provideExpenseDao(database: SlipStashDatabase): ExpenseDao = database.expenseDao()
}
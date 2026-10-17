package com.oddzmint.slipstash.di

import com.oddzmint.slipstash.data.repository.ExpenseRepositoryImpl
import com.oddzmint.slipstash.domain.repository.ExpenseRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindExpenseRepository(expenseRepositoryImpl: ExpenseRepositoryImpl): ExpenseRepository
}
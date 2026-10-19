package com.oddzmint.slipstash.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.oddzmint.slipstash.data.remote.FirestoreReceiptStorage
import com.oddzmint.slipstash.data.repository.AuthRepositoryImpl
import com.oddzmint.slipstash.data.repository.ExpenseRemoteRepositoryImpl
import com.oddzmint.slipstash.domain.repository.AuthRepository
import com.oddzmint.slipstash.domain.repository.ExpenseRemoteRepository
import com.oddzmint.slipstash.domain.repository.ReceiptStorage
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseProvidersModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()
}

@Module
@InstallIn(SingletonComponent::class)
abstract class FirebaseBindingsModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindExpenseRemoteRepository(
        impl: ExpenseRemoteRepositoryImpl
    ): ExpenseRemoteRepository

    @Binds
    @Singleton
    abstract fun bindReceiptStorage(impl: FirestoreReceiptStorage): ReceiptStorage
}
package com.oddzmint.slipstash.domain.repository

interface AccountDataRepository {
    // Delete everything stored in the cloud
    suspend fun deleteRemoteData()
    suspend fun deleteLocalData()
}
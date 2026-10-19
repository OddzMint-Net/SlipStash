package com.oddzmint.slipstash.domain.repository

interface AuthRepository {
    suspend fun ensureSignedIn(): String
}
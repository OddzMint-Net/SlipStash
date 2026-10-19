package com.oddzmint.slipstash.fakes

import com.oddzmint.slipstash.domain.repository.AuthRepository

class FakeAuthRepository(
    private val userId: String = "user-1",
    private val failure: Throwable? = null
) : AuthRepository {
    override suspend fun ensureSignedIn(): String {
        failure?.let { throw it }
        return userId
    }
}
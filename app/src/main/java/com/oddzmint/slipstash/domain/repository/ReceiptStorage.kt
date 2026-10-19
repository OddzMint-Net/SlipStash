package com.oddzmint.slipstash.domain.repository

interface ReceiptStorage {

    suspend fun upload(userId: String, expenseId: String, jpegBytes: ByteArray): String
}
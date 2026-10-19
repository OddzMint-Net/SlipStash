package com.oddzmint.slipstash.data.remote

import com.google.firebase.firestore.Blob
import com.google.firebase.firestore.FirebaseFirestore
import com.oddzmint.slipstash.domain.repository.ReceiptStorage
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

class FirestoreReceiptStorage @Inject constructor(
    private val firestore: FirebaseFirestore
) : ReceiptStorage {

    override suspend fun upload(
        userId: String,
        expenseId: String,
        jpegBytes: ByteArray
    ): String {
        val path = "users/$userId/receipts/$expenseId"
        val data = mapOf(
            "image" to Blob.fromBytes(jpegBytes),
            "sizeBytes" to jpegBytes.size,
            "createdAt" to System.currentTimeMillis()
        )
        withTimeout(30_000.milliseconds) {
            firestore.document(path).set(data).await()
        }
        return path
    }

}
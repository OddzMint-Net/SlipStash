package com.oddzmint.slipstash.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.oddzmint.slipstash.domain.model.Expense
import com.oddzmint.slipstash.domain.repository.ExpenseRemoteRepository
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

class ExpenseRemoteRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : ExpenseRemoteRepository {

    override suspend fun upload(userId: String, expense: Expense) {
        val data = mapOf(
            "id" to expense.id,
            "amountCents" to expense.amountCents,
            "category" to expense.category.name,
            "note" to expense.note,
            "receiptUrl" to expense.receiptRemoteUrl,
            "dateMills" to expense.dateMillis
        )
        withTimeout(20_000.milliseconds){
            firestore.collection("users").document(userId)
                .collection("expenses").document(expense.id)
                .set(data)
                .await()
        }
    }
}
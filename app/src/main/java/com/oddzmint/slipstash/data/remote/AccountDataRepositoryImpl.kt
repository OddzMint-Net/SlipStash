package com.oddzmint.slipstash.data.remote

import android.content.Context
import android.nfc.tech.MifareUltralight.PAGE_SIZE
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import com.oddzmint.slipstash.data.local.ExpenseDao
import com.oddzmint.slipstash.domain.repository.AccountDataRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import java.io.File
import javax.inject.Inject

class AccountDataRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val dao: ExpenseDao,
    @ApplicationContext private val context: Context
) : AccountDataRepository {

    override suspend fun deleteRemoteData() {
        val user = auth.currentUser ?: return
        val userDoc = firestore.collection("users").document(user.uid)

        deleteCollection(userDoc.collection("expenses"))
        deleteCollection(userDoc.collection("receipt"))

        runCatching { withTimeout(20_000) { user.delete().await() } }
            .onFailure { auth.signOut() }
    }

    override suspend fun deleteLocalData() {
        withContext(Dispatchers.IO) {
            dao.deleteAll()
            File(context.filesDir, "receipts").deleteRecursively()
            File(context.filesDir, "receipts compressed").deleteRecursively()
        }
    }

    private suspend fun deleteCollection(collection: CollectionReference) {
        while (true) {
            val page = withTimeout(30_000) {
                collection.limit(PAGE_SIZE).get(Source.SERVER).await()
            }
            if (page.isEmpty) return
            val batch = firestore.batch()
            page.documents.forEach { batch.delete(it.reference) }
            withTimeout(30_000) { batch.commit().await() }
        }
    }

    private companion object {
        const val PAGE_SIZE = 50L
    }
}
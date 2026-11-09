package com.oddzmint.slipstash.fakes

import com.oddzmint.slipstash.domain.repository.AccountDataRepository

class FakeAccountDataRepository(
    private val events: MutableList<String>
) : AccountDataRepository {
    //
    var onRemote: suspend () -> Unit = {}

    override suspend fun deleteRemoteData() {
        events += "remote"
        onRemote()
    }

    override suspend fun deleteLocalData() {
        events += "local"
    }
}
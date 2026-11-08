package com.oddzmint.slipstash

import com.oddzmint.slipstash.domain.usecases.DeleteAllDataUseCase
import com.oddzmint.slipstash.fakes.FakeAccountDataRepository
import com.oddzmint.slipstash.fakes.FakeSyncScheduler
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withTimeout
import org.junit.Test

class DeleteAllDataUseCaseTest {

    private val events = mutableListOf<String>()
    private val scheduler = FakeSyncScheduler(events)
    private val accountData = FakeAccountDataRepository(events)
    private val deleteAll = DeleteAllDataUseCase(scheduler, accountData)

    @Test
    fun `success cancels jobs, deletes cloud then local, then restores the sweep`() = runTest {
        val result = deleteAll()
        assertTrue(result.isSuccess)
        assertEquals(listOf("cancelAll", "remote", "local", "sweep"), events)
    }

    @Test
    fun `cloud failure keeps local data and still restores the sweep`() = runTest {
        accountData.onRemote = { throw RuntimeException("offline") }
        val result = deleteAll()
        assertTrue(result.isFailure)
        assertEquals(listOf("cancelAll", "remote", "sweep"), events)
    }

    @Test
    fun `a timeout is a failure and not a cancellation`() = runTest {
        accountData.onRemote = { withTimeout(50) { awaitCancellation() } }
        val result = deleteAll()
        assertTrue(result.isFailure)
        assertTrue(!events.contains("local"))
    }

    @Test
    fun `real cancellation is rethrow`() = runTest {
        accountData.onRemote = { throw CancellationException("stopped") }
        var rethrow = false
        try {
            deleteAll()
        } catch (e: CancellationException) {
            rethrow = true
        }

        assertTrue(rethrow)
    }
}
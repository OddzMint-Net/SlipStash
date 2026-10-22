package com.oddzmint.slipstash.domain.usecases

import androidx.compose.ui.util.fastFilter
import com.oddzmint.slipstash.domain.model.*
import java.time.Instant
import java.time.YearMonth
import java.time.ZoneId
import javax.inject.Inject

class GetMonthlySummaryUseCase @Inject constructor() {
    operator fun invoke(
        expenses: List<Expense>,
        month: YearMonth,
        zone: ZoneId = ZoneId.systemDefault()
    ): MonthlySummary {
        val inMonth = expenses.fastFilter {
            YearMonth.from(Instant.ofEpochMilli(it.dateMillis).atZone(zone)) == month
        }
        val total = inMonth.sumOf { it.amountCents }
        val byCategory = inMonth
            .groupBy { it.category }
            .mapValues { (_, list) -> list.sumOf { it.amountCents } }
            .filterValues { it > 0L }

        return MonthlySummary(
            month = month,
            totalCents = total,
            expenseCount = inMonth.size,
            receiptCount = inMonth.count {
                it.receiptLocalPath != null || it.receiptRemoteUrl != null
            },
            syncedCount = inMonth.count { it.syncStatus == SyncStatus.SYNCED },
            waitingCount = inMonth.count { it.syncStatus != SyncStatus.SYNCED },
            categories = toCategoryTotals(byCategory, total)
        )
    }

    private fun toCategoryTotals(
        byCategory: Map<Category, Long>,
        total: Long
    ): List<CategoryTotal> {
        if (total <= 0L) return emptyList()

        val sorted = byCategory.entries.sortedWith(
            compareByDescending<Map.Entry<Category, Long>> {
                it.value
            }
                .thenBy { it.key.ordinal }
        )
        val floors = sorted.map { (it.value * 100) / total }
        val remainders = sorted.map { (it.value * 100) % total }

        val leftover = (100 - floors.sum()).toInt()
        val getsExtra = remainders.indices
            .sortedByDescending { remainders[it] }
            .take(leftover)
            .toSet()

        return sorted.mapIndexed { index, entry ->
            CategoryTotal(
                category = entry.key,
                amountCents = entry.value,
                percentage = (floors[index] + if (index in getsExtra) 1 else 0).toInt()
            )
        }
    }
}
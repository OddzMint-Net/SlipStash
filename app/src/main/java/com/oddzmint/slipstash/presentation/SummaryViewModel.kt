package com.oddzmint.slipstash.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oddzmint.slipstash.domain.model.Expense
import com.oddzmint.slipstash.domain.usecases.GetMonthlySummaryUseCase
import com.oddzmint.slipstash.domain.usecases.ObserveExpensesUseCase
import com.oddzmint.slipstash.presentation.model.SummaryUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import java.time.Instant
import java.time.YearMonth
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class SummaryViewModel @Inject constructor(
    observeExpenses: ObserveExpensesUseCase,
    private val getMonthlySummary: GetMonthlySummaryUseCase
) : ViewModel() {

    private val selectedMonth = MutableStateFlow(YearMonth.now())
    val state: StateFlow<SummaryUiState> = combine(
        observeExpenses(),
        selectedMonth
    ) { expense, month ->
        val earliest = earliestMonth(expense)
        SummaryUiState(
            summary = getMonthlySummary(expense, month),
            canGoPrevious = earliest != null && month.isAfter(earliest),
            canGoNext = month.isBefore(YearMonth.now())
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SummaryUiState(
            summary = getMonthlySummary(emptyList(), YearMonth.now()),
            canGoPrevious = false,
            canGoNext = false
        )
    )

    fun previousMonth() {
        if (state.value.canGoPrevious) selectedMonth.update { it.minusMonths(1) }
    }

    fun nextMonth() {
        if (state.value.canGoNext) selectedMonth.update { it.plusMonths(1) }
    }

    private fun earliestMonth(expense: List<Expense>): YearMonth? {
        val zone = ZoneId.systemDefault()
        return expense.minOfOrNull {
            YearMonth.from(Instant.ofEpochMilli(it.dateMillis).atZone(zone))
        }
    }

}
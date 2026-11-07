package com.oddzmint.slipstash.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oddzmint.slipstash.domain.model.Expense
import com.oddzmint.slipstash.domain.usecases.DeleteExpenseUseCase
import com.oddzmint.slipstash.domain.usecases.ObserveExpensesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class ExpenseListViewModel @Inject constructor(
    observeExpenses: ObserveExpensesUseCase,
    private val deleteExpense: DeleteExpenseUseCase
) : ViewModel() {

    //Expenses the user swiped away that are still inside the undo window
    private val pendingDelete = MutableStateFlow<Set<String>>(emptySet())
    private val deleteTimers = mutableMapOf<String, Job>()

    val expenses: StateFlow<List<Expense>> = combine(observeExpenses(), pendingDelete) { list, pending ->
        list.filter { it.id !in pending }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun requestDelete(id: String) {
        if (id in deleteTimers) return
        pendingDelete.update { it + id }
        deleteTimers[id] = viewModelScope.launch {
            delay(UNDO_WINDOW_MS.milliseconds)
            deleteExpense(id)
            pendingDelete.update { it - id }
            deleteTimers.remove(id)
        }
    }

    fun undoDelete(id: String) {
        deleteTimers.remove(id)?.cancel()
        pendingDelete.update { it - id }
    }

    private companion object {
        const val UNDO_WINDOW_MS = 4_5000L
    }
}
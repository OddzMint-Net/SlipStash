package com.oddzmint.slipstash.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oddzmint.slipstash.domain.model.Category
import com.oddzmint.slipstash.domain.model.Expense
import com.oddzmint.slipstash.domain.usecases.AddExpenseUseCase
import com.oddzmint.slipstash.domain.usecases.ObserveExpensesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExpenseListViewModel @Inject constructor(
    observeExpenses: ObserveExpensesUseCase,
    private val addExpense: AddExpenseUseCase
) : ViewModel() {

    val expenses: StateFlow<List<Expense>> = observeExpenses()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun addSampleExpense() {
        viewModelScope.launch {
            addExpense(
                amountCents = (5_000..90_000).random().toLong(),
                category = Category.entries.random(),
                note = "Sample"
            )
        }
    }
}
package com.oddzmint.slipstash.presentation.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oddzmint.slipstash.domain.model.AddExpenseUiState
import com.oddzmint.slipstash.domain.model.Category
import com.oddzmint.slipstash.domain.usecases.AddExpenseUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddExpenseViewModel @Inject constructor(
    private val addExpense: AddExpenseUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(AddExpenseUiState())
    val state: StateFlow<AddExpenseUiState> = _state.asStateFlow()

    fun onAmountChange(value: String) {
        if (value.matches(Regex("""\d{0,7}([.,]\d{0,2})?"""))) {
            _state.update { it.copy(amountText = value, error = null) }
        }
    }

    fun onCategoryChange(category: Category) = _state.update { it.copy(category = category) }

    fun onNoteChange(value: String) = _state.update { it.copy(note = value.take(100)) }

    fun onSave() {
        val current = _state.value
        val cents = parseCents(current.amountText)
        if (cents == null) {
            _state.update { it.copy(error = "Enter an amount") }
            return
        }
        viewModelScope.launch {
            addExpense(
                amountCents = cents,
                category = current.category,
                note = current.note
            )
                .onSuccess { _state.update { it.copy(saved = true) } }
                .onFailure { e -> _state.update { it.copy(error = e.message) } }
        }
    }

    private fun parseCents(text:String):Long? =
        text.replace(',','.')
            .toBigDecimalOrNull()
            ?.movePointRight(2)
            ?.toLong()
}
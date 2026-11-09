package com.oddzmint.slipstash.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oddzmint.slipstash.domain.usecases.DeleteAllDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val deleteAllData: DeleteAllDataUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsUiState())
    val state: StateFlow<SettingsUiState> = _state.asStateFlow()

    fun deleteEverything() {
        if (_state.value.isDeleting) return
        _state.update { it.copy(isDeleting = true, message = null) }
        viewModelScope.launch {
            deleteAllData()

                .onSuccess {
                    _state.update {
                        SettingsUiState(message = "All your data has been deleted.")
                    }
                }
                .onFailure {
                    _state.update {
                        SettingsUiState(
                            message = "Couldn't delete your data. Check your connection and try again. Nothing was removed from this phone.",
                            isError = true
                        )
                    }
                }
        }
    }
}
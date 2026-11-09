package com.oddzmint.slipstash.presentation.settings

data class SettingsUiState(
    val isDeleting: Boolean = false,
    val message: String? = null,
    val isError: Boolean = false
)
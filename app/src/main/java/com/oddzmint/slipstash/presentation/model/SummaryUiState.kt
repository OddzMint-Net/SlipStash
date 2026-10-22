package com.oddzmint.slipstash.presentation.model

import com.oddzmint.slipstash.domain.model.MonthlySummary

data class SummaryUiState(
    val summary: MonthlySummary,
    val canGoPrevious: Boolean,
    val canGoNext: Boolean
)
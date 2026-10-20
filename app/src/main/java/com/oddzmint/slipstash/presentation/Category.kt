package com.oddzmint.slipstash.presentation

import androidx.compose.ui.graphics.Color
import com.oddzmint.slipstash.domain.model.Category

internal fun Category.label(): String = name.lowercase().replace('_',' ').replaceFirstChar { it.uppercase() }

internal fun Category.color(): Color = when (this){
    Category.GROCERIES -> Color(0xFF378ADD)
    Category.BILLS -> Color(0xFF1D9E75)
    Category.TRANSPORT -> Color(0xFFD85A30)
    Category.EATING_OUT -> Color(0xFFDD77A0)
    Category.ENTERTAINMENT ->Color(0xFFDE20B0)
    Category.OTHER -> Color(0xFFEED20E)
}
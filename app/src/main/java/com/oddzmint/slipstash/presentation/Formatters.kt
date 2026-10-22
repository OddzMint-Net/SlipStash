package com.oddzmint.slipstash.presentation

import java.util.Locale

internal fun formatAmount(cents: Long): String = if (cents % 100 == 0L) {
    String.format(Locale.getDefault(), "R %,d", cents / 100)
} else {
    String.format(Locale.getDefault(), "R %,.2f", cents / 100.0)
}
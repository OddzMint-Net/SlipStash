package com.oddzmint.slipstash.util

import java.math.RoundingMode
import java.time.YearMonth
import java.time.ZoneId
import java.util.Locale
import java.util.TimeZone

object Money {
    fun parseToCents(input: String): Long? = input.trim().replace(',', '.').toBigDecimalOrNull()
        ?.takeIf { it.signum() > 0 }
        ?.movePointRight(2)
        ?.setScale(0, RoundingMode.HALF_UP)
        ?.toLong()

    fun format(cents: Long): String = "R %,.2f".format(Locale.US, cents / 100.0)
}

fun YearMonth.range(zone: ZoneId = ZoneId.systemDefault()): Pair<Long, Long> =
    atDay(1).atStartOfDay(zone).toInstant().toEpochMilli() to
            plusMonths(1).atDay(1).atStartOfDay(zone).toInstant().toEpochMilli()
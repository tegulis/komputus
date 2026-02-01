package net.tegulis.komputus.units

import java.math.BigDecimal
import net.tegulis.komputus.amount.Amount
import net.tegulis.komputus.divideWithMathContext
import net.tegulis.komputus.multiplyWithMathContext
import net.tegulis.komputus.prefixes.NoScalingPrefix
import net.tegulis.komputus.prefixes.Prefix
import net.tegulis.komputus.prefixes.SI

/**
 * Time dimension and units.
 *
 * Only seconds, minutes, hours, days, and weeks are provided with classic 60/60/24/7 multipliers. These units provide a
 * simpler version for measuring time compared to [java.time.Duration] or [java.time.Period].
 *
 * See why: https://en.wikipedia.org/wiki/Orders_of_magnitude_(time)
 * - Seconds align to major SI prefixes by default.
 * - All other units have [NoScalingPrefix] by default.
 *
 * @see Amount.prefixFilter
 * @see Amount.majorPrefixFilter
 * @see Prefix.isMajor
 *
 * TODO: Implement convenience functions to convert from [Amount] to [java.time.Duration], [java.time.Period] and
 *   vice-versa
 */
object Time : Dimension {
    override val units: List<UnitOfMeasurement> by lazy { listOf(Second, Minute, Hour, Day, Week) }
    override val baseUnit: UnitOfMeasurement
        get() = Second

    sealed class TimeUnit(override val name: String, override val pluralName: String, override val symbol: String) :
        UnitOfMeasurement {
        override val dimension: Dimension
            get() = Time
    }

    const val DAYS_IN_A_WEEK = 7
    const val HOURS_IN_A_DAY = 24
    const val HOURS_IN_A_WEEK = HOURS_IN_A_DAY * DAYS_IN_A_WEEK
    const val MINUTES_IN_AN_HOUR = 60
    const val MINUTES_IN_A_DAY = MINUTES_IN_AN_HOUR * HOURS_IN_A_DAY
    const val MINUTES_IN_A_WEEK = MINUTES_IN_A_DAY * DAYS_IN_A_WEEK
    const val SECONDS_IN_A_MINUTE = 60
    const val SECONDS_IN_AN_HOUR = SECONDS_IN_A_MINUTE * MINUTES_IN_AN_HOUR
    const val SECONDS_IN_A_DAY = SECONDS_IN_A_MINUTE * MINUTES_IN_A_DAY
    const val SECONDS_IN_A_WEEK = SECONDS_IN_A_MINUTE * MINUTES_IN_A_WEEK

    object Second : TimeUnit("second", "seconds", "s") {
        override val conversions: Set<UnitConversion> by lazy {
            setOf(
                Minute to { it.divideWithMathContext(SECONDS_IN_A_MINUTE) },
                Hour to { it.divideWithMathContext(SECONDS_IN_AN_HOUR) },
                Day to { it.divideWithMathContext(SECONDS_IN_A_DAY) },
                Week to { it.divideWithMathContext(SECONDS_IN_A_WEEK) },
            )
        }

        override fun amountOf(amount: Number, prefix: Prefix): Amount =
            super.amountOf(amount, prefix).apply { prefixFilter = Amount.majorPrefixFilter }

        override fun amountOf(amount: BigDecimal, prefix: Prefix): Amount =
            super.amountOf(amount, prefix).apply { prefixFilter = Amount.majorPrefixFilter }
    }

    object Minute : TimeUnit("minute", "minutes", "min") {
        override val conversions: Set<UnitConversion> by lazy {
            setOf(
                Second to { it.multiplyWithMathContext(SECONDS_IN_A_MINUTE) },
                Hour to { it.divideWithMathContext(MINUTES_IN_AN_HOUR) },
                Day to { it.divideWithMathContext(MINUTES_IN_A_DAY) },
                Week to { it.divideWithMathContext(MINUTES_IN_A_WEEK) },
            )
        }
    }

    object Hour : TimeUnit("hour", "hours", "h") {
        override val conversions: Set<UnitConversion> by lazy {
            setOf(
                Second to { it.multiplyWithMathContext(SECONDS_IN_AN_HOUR) },
                Minute to { it.multiplyWithMathContext(MINUTES_IN_AN_HOUR) },
                Day to { it.divideWithMathContext(HOURS_IN_A_DAY) },
                Week to { it.divideWithMathContext(HOURS_IN_A_WEEK) },
            )
        }
    }

    object Day : TimeUnit("day", "days", "d") {
        override val conversions: Set<UnitConversion> by lazy {
            setOf(
                Second to { it.multiplyWithMathContext(SECONDS_IN_A_DAY) },
                Minute to { it.multiplyWithMathContext(MINUTES_IN_A_DAY) },
                Hour to { it.multiplyWithMathContext(HOURS_IN_A_DAY) },
                Week to { it.divideWithMathContext(DAYS_IN_A_WEEK) },
            )
        }
    }

    object Week : TimeUnit("week", "weeks", "w") {
        override val conversions: Set<UnitConversion> by lazy {
            setOf(
                Second to { it.multiplyWithMathContext(SECONDS_IN_A_WEEK) },
                Minute to { it.multiplyWithMathContext(MINUTES_IN_A_WEEK) },
                Hour to { it.multiplyWithMathContext(HOURS_IN_A_WEEK) },
                Day to { it.multiplyWithMathContext(DAYS_IN_A_WEEK) },
            )
        }
    }
}

//
// Convenience functions for creating time amounts
//

fun Amount.Companion.ofSeconds(seconds: Number, prefix: Prefix = SI.noScalingPrefix): Amount =
    Time.Second.amountOf(seconds, prefix)

fun Amount.Companion.ofSeconds(seconds: BigDecimal, prefix: Prefix = SI.noScalingPrefix): Amount =
    Time.Second.amountOf(seconds, prefix)

fun Amount.Companion.ofMinutes(minutes: Number, prefix: Prefix = NoScalingPrefix): Amount =
    Time.Minute.amountOf(minutes, prefix)

fun Amount.Companion.ofMinutes(minutes: BigDecimal, prefix: Prefix = NoScalingPrefix): Amount =
    Time.Minute.amountOf(minutes, prefix)

fun Amount.Companion.ofHours(hours: Number, prefix: Prefix = NoScalingPrefix): Amount =
    Time.Hour.amountOf(hours, prefix)

fun Amount.Companion.ofHours(hours: BigDecimal, prefix: Prefix = NoScalingPrefix): Amount =
    Time.Hour.amountOf(hours, prefix)

fun Amount.Companion.ofDays(days: Number, prefix: Prefix = NoScalingPrefix): Amount = Time.Day.amountOf(days, prefix)

fun Amount.Companion.ofDays(days: BigDecimal, prefix: Prefix = NoScalingPrefix): Amount =
    Time.Day.amountOf(days, prefix)

fun Amount.Companion.ofWeeks(weeks: Number, prefix: Prefix = NoScalingPrefix): Amount =
    Time.Week.amountOf(weeks, prefix)

fun Amount.Companion.ofWeeks(weeks: BigDecimal, prefix: Prefix = NoScalingPrefix): Amount =
    Time.Week.amountOf(weeks, prefix)

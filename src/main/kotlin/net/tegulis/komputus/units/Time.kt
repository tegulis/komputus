package net.tegulis.komputus.units

import java.math.BigDecimal
import net.tegulis.komputus.amount.Amount
import net.tegulis.komputus.divideWithMathContext
import net.tegulis.komputus.multiplyWithMathContext
import net.tegulis.komputus.prefixes.NotScalingPrefix
import net.tegulis.komputus.prefixes.Prefix
import net.tegulis.komputus.prefixes.SI

/**
 * Time dimension and units.
 *
 * Only seconds, minutes, hours, days, and weeks are provided with classic 60/60/24/7 multipliers. These units provide a
 * simpler version for measuring time compared to [java.time.Duration] or [java.time.Period].
 *
 * See why: https://en.wikipedia.org/wiki/Orders_of_magnitude_(time)
 * - Seconds align to major SI prefixes for values below 1 only.
 * - All other units have [NotScalingPrefix] by default.
 *
 * @see UnitOfMeasurement.prefixFilter
 * @see Prefix.Companion.majorPrefixFilter
 * @see Prefix.isMajor
 *
 * TODO: Implement convenience functions to convert from [Amount] to [java.time.Duration], [java.time.Period] and
 *   vice-versa
 */
object Time : Dimension {
    override val units: List<UnitOfMeasurement> by lazy { listOf(Second, Minute, Hour, Day, Week) }
    override val baseUnit: UnitOfMeasurement
        get() = Second

    sealed class TimeUnit(
        override val name: String,
        override val pluralName: String,
        override val symbol: String,
        secondsPerUnit: Int = 1,
    ) : UnitOfMeasurement {
        override val dimension: Dimension
            get() = Time

        override val prefixFilter: (Prefix) -> Boolean = Prefix.notScalingPrefixFilter
        override val toBase: (BigDecimal) -> BigDecimal = { it.multiplyWithMathContext(secondsPerUnit) }
        override val fromBase: (BigDecimal) -> BigDecimal = { it.divideWithMathContext(secondsPerUnit) }
    }

    const val DAYS_IN_A_WEEK = 7
    const val HOURS_IN_A_DAY = 24
    const val MINUTES_IN_AN_HOUR = 60
    const val MINUTES_IN_A_DAY = MINUTES_IN_AN_HOUR * HOURS_IN_A_DAY
    const val MINUTES_IN_A_WEEK = MINUTES_IN_A_DAY * DAYS_IN_A_WEEK
    const val SECONDS_IN_A_MINUTE = 60
    const val SECONDS_IN_AN_HOUR = SECONDS_IN_A_MINUTE * MINUTES_IN_AN_HOUR
    const val SECONDS_IN_A_DAY = SECONDS_IN_A_MINUTE * MINUTES_IN_A_DAY
    const val SECONDS_IN_A_WEEK = SECONDS_IN_A_MINUTE * MINUTES_IN_A_WEEK

    object Second : TimeUnit("second", "seconds", "s") {
        override val prefixFilter: (Prefix) -> Boolean = { it.power <= 0 && it.isMajor }
    }

    object Minute : TimeUnit("minute", "minutes", "min", SECONDS_IN_A_MINUTE)

    object Hour : TimeUnit("hour", "hours", "h", SECONDS_IN_AN_HOUR)

    object Day : TimeUnit("day", "days", "d", SECONDS_IN_A_DAY)

    object Week : TimeUnit("week", "weeks", "w", SECONDS_IN_A_WEEK)

    /**
     * Align by switching to the largest unit that yields a [Amount.magnitude] of at least one. For example, 3600
     * seconds align to 1 hour, 90 seconds to 1.5 minutes.
     *
     * Amounts below one minute are converted to [Second] and aligned with [Amount.alignPrefix]. For example, 0.5
     * seconds align to 500 milliseconds.
     */
    override fun align(amount: Amount): Amount {
        require(amount.unit.dimension == this) { "Cannot align an amount of ${amount.unit.dimension} with $this" }
        for (unit in listOf(Week, Day, Hour, Minute)) {
            val converted = amount.convertTo(unit)
            if (converted.magnitude.abs() >= BigDecimal.ONE) {
                return converted
            }
        }
        return amount.convertTo(Second).alignPrefix()
    }
}

//
// Convenience functions for creating time amounts
//

fun Amount.Companion.ofSeconds(seconds: Number, prefix: Prefix = SI.defaultNotScalingPrefix): Amount =
    Time.Second.amountOf(seconds, prefix)

fun Amount.Companion.ofSeconds(seconds: BigDecimal, prefix: Prefix = SI.defaultNotScalingPrefix): Amount =
    Time.Second.amountOf(seconds, prefix)

fun Amount.Companion.ofMinutes(minutes: Number, prefix: Prefix = NotScalingPrefix): Amount =
    Time.Minute.amountOf(minutes, prefix)

fun Amount.Companion.ofMinutes(minutes: BigDecimal, prefix: Prefix = NotScalingPrefix): Amount =
    Time.Minute.amountOf(minutes, prefix)

fun Amount.Companion.ofHours(hours: Number, prefix: Prefix = NotScalingPrefix): Amount =
    Time.Hour.amountOf(hours, prefix)

fun Amount.Companion.ofHours(hours: BigDecimal, prefix: Prefix = NotScalingPrefix): Amount =
    Time.Hour.amountOf(hours, prefix)

fun Amount.Companion.ofDays(days: Number, prefix: Prefix = NotScalingPrefix): Amount = Time.Day.amountOf(days, prefix)

fun Amount.Companion.ofDays(days: BigDecimal, prefix: Prefix = NotScalingPrefix): Amount =
    Time.Day.amountOf(days, prefix)

fun Amount.Companion.ofWeeks(weeks: Number, prefix: Prefix = NotScalingPrefix): Amount =
    Time.Week.amountOf(weeks, prefix)

fun Amount.Companion.ofWeeks(weeks: BigDecimal, prefix: Prefix = NotScalingPrefix): Amount =
    Time.Week.amountOf(weeks, prefix)

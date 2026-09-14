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
 * - Seconds align to major SI prefixes for values below 1 only, and admit no other prefix group.
 * - All other units have [NotScalingPrefix] by default and never scale (there are no kilominutes).
 *
 * Because [Second] rejects every non-SI prefix, any time amount converted to seconds resets into the SI group and can
 * therefore align down to milliseconds - even one that started in a group with no sub-one prefixes, such as
 * `Amount.ofMinutes(0.001).align()`, which yields 60 ms.
 *
 * @see UnitOfMeasurement.prefixFilter
 * @see Prefix.Companion.isMajorPrefixFilter
 * @see Prefix.isMajor
 *
 * Months and years are intentionally not units: they are not fixed lengths (28-31 days, leap years). Calendar
 * arithmetic belongs to the Java Date and Time API - compute a [java.time.Period]'s actual span there and bridge it
 * with the [java.time.Duration] conversions.
 *
 * Conversions to and from [java.time.Duration] and [kotlin.time.Duration] are provided in
 * net.tegulis.komputus.Duration.kt ([net.tegulis.komputus.toAmount], [net.tegulis.komputus.toJavaDuration],
 * [net.tegulis.komputus.toKotlinDuration]).
 *
 * TODO: Implement convenience functions to convert between [Amount] and [java.time.Period]
 */
object Time : Dimension {
    override val units: List<TimeUnit> by lazy { listOf(Second, Minute, Hour, Day, Week) }
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

        /**
         * Minutes, hours, days and weeks take no prefix at all - there are no kilominutes. [Second] overrides this.
         *
         * [NotScalingPrefix] is matched by identity rather than with [Prefix.Companion.isNotScalingPrefixFilter]: that
         * would also admit the no-scaling prefix of the other groups (`SI.NONE`, `IEC.NONE`), which have no sub-one
         * prefix for a time unit to scale with - so an amount could strand there and never align into milliseconds.
         */
        override val prefixFilter: (Prefix) -> Boolean = { it == NotScalingPrefix }
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
        /**
         * Seconds admit **only** [SI] prefixes, and only major ones that scale below one (milli, micro, nano, ...).
         *
         * Rejecting the other prefix groups is what makes sub-second alignment work: [NotScalingPrefix] (carried by
         * minutes, hours, days and weeks) and [net.tegulis.komputus.prefixes.IEC] prefixes both belong to groups
         * without sub-one prefixes, so an amount stuck in them could never align down to milliseconds. Because they are
         * rejected here, converting to [Second] resets the prefix to [defaultPrefix] - and [SI] can scale down.
         */
        override val prefixFilter: (Prefix) -> Boolean = { it.prefixGroup == SI && it.power <= 0 && it.isMajor }
        override val defaultPrefix: Prefix = SI.NONE
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

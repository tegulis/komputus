package net.tegulis.komputus

import java.math.BigDecimal
import java.math.RoundingMode
import java.time.Duration
import kotlin.time.toJavaDuration
import kotlin.time.toKotlinDuration
import net.tegulis.komputus.amount.Amount
import net.tegulis.komputus.prefixes.SI
import net.tegulis.komputus.units.Time

/** Convenience function to get the total number of seconds (including fractions) in a [Duration]. */
fun Duration.getTotalSeconds(): BigDecimal =
    this.nano.toBigDecimalWithMathContext().movePointLeft(9).add(this.seconds.toBigDecimalWithMathContext())

/** Convert a [Duration] to an [Amount] of [Time.Second]s with SI prefix. */
fun Duration.toAmount(): Amount = Amount(this.getTotalSeconds(), SI.defaultNotScalingPrefix, Time.Second)

/**
 * Convert a [kotlin.time.Duration] to an [Amount] of [Time.Second]s with SI prefix.
 *
 * @throws IllegalArgumentException if this duration is infinite
 */
fun kotlin.time.Duration.toAmount(): Amount {
    require(this.isFinite()) { "Cannot convert an infinite duration to an Amount" }
    return this.toJavaDuration().toAmount()
}

/**
 * Convert this amount of [Time] to a [Duration].
 *
 * The conversion goes through [Amount.baseMagnitude] (seconds), so it is exact for all [Time] units. Fractions below
 * [Duration]'s nanosecond resolution are rounded with [Amount.defaultRoundingMode].
 *
 * @throws IllegalArgumentException if this amount is not of the [Time] dimension
 * @throws ArithmeticException if the number of seconds does not fit a [Long]
 */
fun Amount.toJavaDuration(): Duration {
    require(unit.dimension == Time) { "Cannot convert an amount of ${unit.dimension} to a duration" }
    val seconds = baseMagnitude.setScale(0, RoundingMode.DOWN)
    val nanos = baseMagnitude.subtract(seconds).movePointRight(9).setScale(0, Amount.defaultRoundingMode)
    return Duration.ofSeconds(seconds.longValueExact(), nanos.longValueExact())
}

/**
 * Convert this amount of [Time] to a [kotlin.time.Duration].
 *
 * Same behaviour as [toJavaDuration], with [kotlin.time.Duration]'s own limits on top: values lose sub-millisecond
 * precision beyond ~146 years and saturate to infinite beyond ~146 million years.
 *
 * @throws IllegalArgumentException if this amount is not of the [Time] dimension
 * @throws ArithmeticException if the number of seconds does not fit a [Long]
 */
fun Amount.toKotlinDuration(): kotlin.time.Duration = this.toJavaDuration().toKotlinDuration()

// TODO: convert Period to Amount

package net.tegulis.komputus

import java.math.BigDecimal
import java.time.Duration
import net.tegulis.komputus.amount.Amount
import net.tegulis.komputus.prefixes.SI
import net.tegulis.komputus.units.Time

/** Convenience function to get the total number of seconds (including fractions) in a [Duration]. */
fun Duration.getTotalSeconds(): BigDecimal =
    this.nano.toBigDecimalWithMathContext().movePointLeft(9).add(this.seconds.toBigDecimalWithMathContext())

/** Convert a [Duration] to an [Amount] of [Time.Second]s with SI prefix. */
fun Duration.toAmount(): Amount = Amount(this.getTotalSeconds(), SI.noScalingPrefix, Time.Second)

// TODO: convert Period to Amount

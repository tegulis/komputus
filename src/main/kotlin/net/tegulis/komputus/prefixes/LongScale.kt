package net.tegulis.komputus.prefixes

import java.math.BigDecimal

/**
 * Long scale prefixes, written out in full: thousand, million, milliard, billion, billiard, ...
 *
 * See: https://en.wikipedia.org/wiki/Long_and_short_scales
 *
 * In the long scale - continental Europe (including Hungary), Latin America and much of the rest of the world - each
 * new *-illion* is a million times the previous one, and the halfway steps are the *-illiards*. A billion is therefore
 * 10^12, not 10^9.
 *
 * This is precisely why it is a separate [PrefixGroup] from [ShortScale]: [LongScale.BILLION] is a thousand times
 * [ShortScale.BILLION], and treating them as one set of prefixes would silently misreport budgets by three orders of
 * magnitude.
 *
 * | Quantity | [ShortScale] | [LongScale] |
 * |----------|--------------|-------------|
 * | 10^9     | billion      | milliard    |
 * | 10^12    | trillion     | billion     |
 * | 10^15    | quadrillion  | billiard    |
 * | 10^18    | quintillion  | trillion    |
 *
 * The symbols are the words themselves rather than letters; an AmountFormatter can translate them per locale.
 *
 * Only non-negative powers are defined, so money never scales *down*: there are no millidollars, and aligning 0.5
 * dollars leaves it at [NONE].
 */
object LongScale : PrefixGroup() {
    override val multiplier: Int = 10
    override val prefixes: List<Prefix> by lazy {
        listOf(NONE, THOUSAND, MILLION, MILLIARD, BILLION, BILLIARD, TRILLION, TRILLIARD, QUADRILLION, QUADRILLIARD)
    }
    override val defaultNotScalingPrefix: Prefix
        get() = NONE

    sealed class LongScalePrefix(override val symbol: String, override val power: Int) : Prefix() {
        override val prefixGroup: PrefixGroup
            get() = LongScale

        override val value: BigDecimal = valueFromPowerOfMultiplier(multiplier, power)
        override val isMajor: Boolean = true
    }

    object NONE : LongScalePrefix("", 0)

    object THOUSAND : LongScalePrefix("thousand", 3)

    object MILLION : LongScalePrefix("million", 6)

    object MILLIARD : LongScalePrefix("milliard", 9)

    object BILLION : LongScalePrefix("billion", 12)

    object BILLIARD : LongScalePrefix("billiard", 15)

    object TRILLION : LongScalePrefix("trillion", 18)

    object TRILLIARD : LongScalePrefix("trilliard", 21)

    object QUADRILLION : LongScalePrefix("quadrillion", 24)

    object QUADRILLIARD : LongScalePrefix("quadrilliard", 27)
}

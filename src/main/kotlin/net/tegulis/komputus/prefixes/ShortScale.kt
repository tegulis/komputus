package net.tegulis.komputus.prefixes

import java.math.BigDecimal

/**
 * Short scale prefixes, written out in full: thousand, million, billion, trillion, ...
 *
 * See: https://en.wikipedia.org/wiki/Long_and_short_scales
 *
 * In the short scale - the English-speaking world, and international finance - each new name is a thousand times the
 * previous one, so a billion is 10^9. [LongScale] names the very same magnitudes differently (its billion is 10^12),
 * which is why the two are separate [PrefixGroup]s rather than one shared set: [ShortScale.BILLION] and
 * [LongScale.BILLION] are not the same number, and nothing should let them be confused.
 *
 * The symbols are the words themselves rather than letters, because the letters are ambiguous across dimensions ("B"
 * would be a billion here but a byte elsewhere). An AmountFormatter can translate them per locale.
 *
 * Only non-negative powers are defined, so money never scales *down*: there are no millidollars, and aligning 0.5
 * dollars leaves it at [NONE].
 */
object ShortScale : PrefixGroup() {
    override val multiplier: Int = 10
    override val prefixes: List<Prefix> by lazy {
        listOf(NONE, THOUSAND, MILLION, BILLION, TRILLION, QUADRILLION, QUINTILLION, SEXTILLION, SEPTILLION, OCTILLION)
    }
    override val defaultNotScalingPrefix: Prefix
        get() = NONE

    sealed class ShortScalePrefix(override val symbol: String, override val power: Int) : Prefix() {
        override val prefixGroup: PrefixGroup
            get() = ShortScale

        override val value: BigDecimal = valueFromPowerOfMultiplier(multiplier, power)
        override val isMajor: Boolean = true
    }

    object NONE : ShortScalePrefix("", 0)

    object THOUSAND : ShortScalePrefix("thousand", 3)

    object MILLION : ShortScalePrefix("million", 6)

    object BILLION : ShortScalePrefix("billion", 9)

    object TRILLION : ShortScalePrefix("trillion", 12)

    object QUADRILLION : ShortScalePrefix("quadrillion", 15)

    object QUINTILLION : ShortScalePrefix("quintillion", 18)

    object SEXTILLION : ShortScalePrefix("sextillion", 21)

    object SEPTILLION : ShortScalePrefix("septillion", 24)

    object OCTILLION : ShortScalePrefix("octillion", 27)
}

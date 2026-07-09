package net.tegulis.komputus.prefixes

import java.math.BigDecimal

/** Metric prefixes based on the International System of Units (SI). See: https://en.wikipedia.org/wiki/Metric_prefix */
object SI : PrefixGroup() {
    override val multiplier: Int = 10
    override val prefixes: List<Prefix> by lazy {
        listOf(
            QUECTO,
            RONTO,
            YOCTO,
            ZEPTO,
            ATTO,
            FEMTO,
            PICO,
            NANO,
            MICRO,
            MILLI,
            CENTI,
            DECI,
            NONE,
            DECA,
            HECTO,
            KILO,
            MEGA,
            GIGA,
            TERA,
            PETA,
            EXA,
            ZETTA,
            YOTTA,
            RONNA,
            QUETTA,
        )
    }
    override val defaultNotScalingPrefix: Prefix
        get() = NONE

    sealed class SIPrefix(override val symbol: String, override val power: Int) : Prefix() {
        override val prefixGroup: PrefixGroup
            get() = SI

        override val value: BigDecimal = valueFromPowerOfMultiplier(multiplier, power)
        override val isMajor: Boolean = (power % 3 == 0)
    }

    object QUECTO : SIPrefix("q", -30)

    object RONTO : SIPrefix("r", -27)

    object YOCTO : SIPrefix("y", -24)

    object ZEPTO : SIPrefix("z", -21)

    object ATTO : SIPrefix("a", -18)

    object FEMTO : SIPrefix("f", -15)

    object PICO : SIPrefix("p", -12)

    object NANO : SIPrefix("n", -9)

    object MICRO : SIPrefix("μ", -6)

    object MILLI : SIPrefix("m", -3)

    object CENTI : SIPrefix("c", -2)

    object DECI : SIPrefix("d", -1)

    object NONE : SIPrefix("", 0)

    object DECA : SIPrefix("da", 1)

    object HECTO : SIPrefix("h", 2)

    object KILO : SIPrefix("k", 3)

    object MEGA : SIPrefix("M", 6)

    object GIGA : SIPrefix("G", 9)

    object TERA : SIPrefix("T", 12)

    object PETA : SIPrefix("P", 15)

    object EXA : SIPrefix("E", 18)

    object ZETTA : SIPrefix("Z", 21)

    object YOTTA : SIPrefix("Y", 24)

    object RONNA : SIPrefix("R", 27)

    object QUETTA : SIPrefix("Q", 30)
}

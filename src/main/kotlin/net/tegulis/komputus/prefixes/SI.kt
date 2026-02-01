package net.tegulis.komputus.prefixes

import java.math.BigDecimal

/** Metric prefixes based on the International System of Units (SI). See: https://en.wikipedia.org/wiki/Metric_prefix */
object SI : PrefixGroup() {
    override val multiplier: Int = 10
    override val prefixes: List<Prefix> by lazy {
        listOf(
            QUETTA,
            RONNA,
            YOTTA,
            ZETTA,
            EXA,
            PETA,
            TERA,
            GIGA,
            MEGA,
            KILO,
            HECTO,
            DECA,
            NONE,
            DECI,
            CENTI,
            MILLI,
            MICRO,
            NANO,
            PICO,
            FEMTO,
            ATTO,
            ZEPTO,
            YOCTO,
            RONTO,
            QUECTO,
        )
    }
    override val noScalingPrefix: Prefix
        get() = NONE

    sealed class SIPrefix(override val symbol: String, override val power: Int) : Prefix() {
        override val prefixGroup: PrefixGroup
            get() = SI

        override val value: BigDecimal = valueFromPowerOfMultiplier(multiplier, power)
        override val isMajor: Boolean = (power % 3 == 0)
    }

    object QUETTA : SIPrefix("Q", 30)

    object RONNA : SIPrefix("R", 27)

    object YOTTA : SIPrefix("Y", 24)

    object ZETTA : SIPrefix("Z", 21)

    object EXA : SIPrefix("E", 18)

    object PETA : SIPrefix("P", 15)

    object TERA : SIPrefix("T", 12)

    object GIGA : SIPrefix("G", 9)

    object MEGA : SIPrefix("M", 6)

    object KILO : SIPrefix("k", 3)

    object HECTO : SIPrefix("h", 2)

    object DECA : SIPrefix("da", 1)

    object NONE : SIPrefix("", 0)

    object DECI : SIPrefix("d", -1)

    object CENTI : SIPrefix("c", -2)

    object MILLI : SIPrefix("m", -3)

    object MICRO : SIPrefix("μ", -6)

    object NANO : SIPrefix("n", -9)

    object PICO : SIPrefix("p", -12)

    object FEMTO : SIPrefix("f", -15)

    object ATTO : SIPrefix("a", -18)

    object ZEPTO : SIPrefix("z", -21)

    object YOCTO : SIPrefix("y", -24)

    object RONTO : SIPrefix("r", -27)

    object QUECTO : SIPrefix("q", -30)
}

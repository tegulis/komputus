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

    sealed class SIPrefix(override val name: String, override val symbol: String, override val power: Int) : Prefix() {
        override val prefixGroup: PrefixGroup
            get() = SI

        override val value: BigDecimal = valueFromPowerOfMultiplier(multiplier, power)
        override val isMajor: Boolean = (power % 3 == 0)
    }

    object QUECTO : SIPrefix("quecto", "q", -30)

    object RONTO : SIPrefix("ronto", "r", -27)

    object YOCTO : SIPrefix("yocto", "y", -24)

    object ZEPTO : SIPrefix("zepto", "z", -21)

    object ATTO : SIPrefix("atto", "a", -18)

    object FEMTO : SIPrefix("femto", "f", -15)

    object PICO : SIPrefix("pico", "p", -12)

    object NANO : SIPrefix("nano", "n", -9)

    object MICRO : SIPrefix("micro", "μ", -6)

    object MILLI : SIPrefix("milli", "m", -3)

    object CENTI : SIPrefix("centi", "c", -2)

    object DECI : SIPrefix("deci", "d", -1)

    object NONE : SIPrefix("", "", 0)

    object DECA : SIPrefix("deca", "da", 1)

    object HECTO : SIPrefix("hecto", "h", 2)

    object KILO : SIPrefix("kilo", "k", 3)

    object MEGA : SIPrefix("mega", "M", 6)

    object GIGA : SIPrefix("giga", "G", 9)

    object TERA : SIPrefix("tera", "T", 12)

    object PETA : SIPrefix("peta", "P", 15)

    object EXA : SIPrefix("exa", "E", 18)

    object ZETTA : SIPrefix("zetta", "Z", 21)

    object YOTTA : SIPrefix("yotta", "Y", 24)

    object RONNA : SIPrefix("ronna", "R", 27)

    object QUETTA : SIPrefix("quetta", "Q", 30)
}

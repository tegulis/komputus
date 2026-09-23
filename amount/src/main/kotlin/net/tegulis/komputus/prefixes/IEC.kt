package net.tegulis.komputus.prefixes

import java.math.BigDecimal

/** Binary prefixes following the IEC standard. See: https://en.wikipedia.org/wiki/Binary_prefix */
object IEC : PrefixGroup() {
    override val multiplier: Int = 1024
    override val prefixes: List<Prefix> by lazy {
        listOf(NONE, KIBI, MEBI, GIBI, TEBI, PEBI, EXBI, ZEBI, YOBI, ROBI, QUEBI)
    }
    override val defaultNotScalingPrefix: Prefix
        get() = NONE

    sealed class IECPrefix(override val name: String, override val symbol: String, override val power: Int) : Prefix() {
        override val prefixGroup: PrefixGroup
            get() = IEC

        override val value: BigDecimal = valueFromPowerOfMultiplier(multiplier, power)
        override val isMajor: Boolean = true
    }

    object NONE : IECPrefix("", "", 0)

    object KIBI : IECPrefix("kibi", "Ki", 1)

    object MEBI : IECPrefix("mebi", "Mi", 2)

    object GIBI : IECPrefix("gibi", "Gi", 3)

    object TEBI : IECPrefix("tebi", "Ti", 4)

    object PEBI : IECPrefix("pebi", "Pi", 5)

    object EXBI : IECPrefix("exbi", "Ei", 6)

    object ZEBI : IECPrefix("zebi", "Zi", 7)

    object YOBI : IECPrefix("yobi", "Yi", 8)

    object ROBI : IECPrefix("robi", "Ri", 9)

    object QUEBI : IECPrefix("quebi", "Qi", 10)
}

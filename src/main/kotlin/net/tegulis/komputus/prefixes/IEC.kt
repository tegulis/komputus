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

    sealed class IECPrefix(override val symbol: String, override val power: Int) : Prefix() {
        override val prefixGroup: PrefixGroup
            get() = IEC

        override val value: BigDecimal = valueFromPowerOfMultiplier(multiplier, power)
        override val isMajor: Boolean = true
    }

    object NONE : IECPrefix("", 0)

    object KIBI : IECPrefix("Ki", 1)

    object MEBI : IECPrefix("Mi", 2)

    object GIBI : IECPrefix("Gi", 3)

    object TEBI : IECPrefix("Ti", 4)

    object PEBI : IECPrefix("Pi", 5)

    object EXBI : IECPrefix("Ei", 6)

    object ZEBI : IECPrefix("Zi", 7)

    object YOBI : IECPrefix("Yi", 8)

    object ROBI : IECPrefix("Ri", 9)

    object QUEBI : IECPrefix("Qi", 10)
}

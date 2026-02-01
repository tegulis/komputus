package net.tegulis.komputus.prefixes

import java.math.BigDecimal
import net.tegulis.komputus.toBigDecimalWithMathContext

abstract class Prefix {
    abstract val prefixGroup: PrefixGroup
    abstract val symbol: String
    abstract val power: Int
    abstract val value: BigDecimal
    open val isMajor: Boolean = true

    override fun toString(): String = """$prefixGroup.${this::class.simpleName ?: super.toString()}"""

    companion object {
        fun valueFromPowerOfMultiplier(multiplier: Int, power: Int): BigDecimal =
            when {
                power < 0 -> BigDecimal.ONE.divide(multiplier.toBigDecimalWithMathContext().pow(-power))
                power == 0 -> BigDecimal.ONE
                else -> multiplier.toBigDecimalWithMathContext().pow(power)
            }
    }
}

object NoScalingPrefix : Prefix() {
    override val prefixGroup = NoScalingPrefixGroup
    override val symbol = ""
    override val power = 0
    override val value: BigDecimal = BigDecimal.ONE
}

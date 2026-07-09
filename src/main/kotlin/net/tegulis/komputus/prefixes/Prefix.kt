package net.tegulis.komputus.prefixes

import java.math.BigDecimal
import net.tegulis.komputus.amount.Amount
import net.tegulis.komputus.toBigDecimalWithMathContext

/**
 * Prefixes are used to scale [Amount]s' values to a more convenient range. See [Amount.alignPrefix] for details.
 *
 * A prefix belongs to a [PrefixGroup] that defines its [PrefixGroup.multiplier]. The prefix's [value] is then
 * calculated from the multiplier and the prefix's [power].
 *
 * Prefixes can be [isMajor] to control prefix selection. [PrefixGroup]s define a [PrefixGroup.defaultNotScalingPrefix],
 * but more than one prefix can be [isNotScaling] in the same [PrefixGroup].
 *
 * Prefixes can be used with or without [net.tegulis.komputus.units.UnitOfMeasurement]s.
 *
 * @see Amount
 * @see PrefixGroup
 * @see net.tegulis.komputus.units.UnitOfMeasurement
 */
abstract class Prefix {
    abstract val prefixGroup: PrefixGroup
    abstract val symbol: String
    abstract val power: Int
    abstract val value: BigDecimal
    open val isMajor: Boolean = true
    val isNotScaling: Boolean
        get() = value.compareTo(BigDecimal.ONE) == 0

    override fun toString(): String = """$prefixGroup.${this::class.simpleName ?: super.toString()}"""

    companion object {
        val majorPrefixFilter: (Prefix) -> Boolean = { it.isMajor }
        val notScalingPrefixFilter: (Prefix) -> Boolean = { it.isNotScaling }

        fun valueFromPowerOfMultiplier(multiplier: Int, power: Int): BigDecimal =
            when {
                power < 0 -> BigDecimal.ONE.divide(multiplier.toBigDecimalWithMathContext().pow(-power))
                power == 0 -> BigDecimal.ONE
                else -> multiplier.toBigDecimalWithMathContext().pow(power)
            }
    }
}

object NotScalingPrefix : Prefix() {
    override val prefixGroup = NotScalingPrefixGroup
    override val symbol = ""
    override val power = 0
    override val value: BigDecimal = BigDecimal.ONE
}

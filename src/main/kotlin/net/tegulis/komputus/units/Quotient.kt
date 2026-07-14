package net.tegulis.komputus.units

import java.math.BigDecimal
import net.tegulis.komputus.amount.Amount
import net.tegulis.komputus.divideWithMathContext
import net.tegulis.komputus.multiplyWithMathContext
import net.tegulis.komputus.prefixes.Prefix

/**
 * A first-order quotient of two other dimensions. For example, [BinaryInformation] over [Time] (bandwidth), [Time] per
 * [BinaryInformation] (transfer time per size).
 *
 * Quotient dimensions are structural values:
 * - Two quotient dimensions are equal when their components are equal (bit/second and byte/hour share one dimension).
 * - Nothing is simplified or cancelled: byte/byte is not [NoDimension], and a quotient dimension is never equal to a
 *   named dimension modelling the same physical quantity.
 *
 * [units] is the cross product of the component dimensions' units; [baseUnit] is the quotient of the component
 * dimensions' base units. Alignment keeps the [Dimension.align] default: only the prefix is aligned, the quotient unit
 * is never switched.
 */
data class QuotientDimension(val numerator: Dimension, val denominator: Dimension) : Dimension {
    override val units: List<UnitOfMeasurement> by lazy {
        numerator.units.flatMap { numeratorUnit ->
            denominator.units.map { denominatorUnit -> QuotientUnit(numeratorUnit, denominatorUnit) }
        }
    }
    override val baseUnit: UnitOfMeasurement by lazy { QuotientUnit(numerator.baseUnit, denominator.baseUnit) }
}

/**
 * A [UnitOfMeasurement] expressing [numerator] units per one [denominator] unit, e.g. bits per second.
 *
 * An [Amount] in a quotient unit is a rate: its [Amount.magnitude] means "numerator units per one denominator unit",
 * and its single [Amount.prefix] reads as scaling the numerator (an amount of 100 with
 * [net.tegulis.komputus.prefixes.SI.MEGA] in bit/second formats as `100 Mb/s`). [prefixFilter] therefore delegates to
 * the numerator's filter.
 *
 * Quotient units are structural values (data classes), so independently created instances of the same quotient are
 * equal and convert into each other like any other unit of a shared dimension.
 *
 * [toBase] and [fromBase] compose the component units' conversions and assume they are linear scalings (all built-in
 * units are). Divisions with non-terminating decimal expansions are rounded to [Amount.defaultNonTerminatingPrecision],
 * and the rounding compounds when an already rounded rate is converted further.
 *
 * NOTE: Component units with blank [UnitOfMeasurement.symbol]s or names produce awkward quotient symbols and names.
 */
data class QuotientUnit(val numerator: UnitOfMeasurement, val denominator: UnitOfMeasurement) : UnitOfMeasurement {
    override val name: String = "${numerator.name} per ${denominator.name}"
    override val pluralName: String = "${numerator.pluralName} per ${denominator.name}"
    override val symbol: String = "${numerator.symbolInQuotient()}/${denominator.symbolInQuotient()}"
    override val dimension: Dimension = QuotientDimension(numerator.dimension, denominator.dimension)
    override val prefixFilter: (Prefix) -> Boolean = numerator.prefixFilter
    override val defaultPrefix: Prefix = numerator.defaultPrefix
    override val toBase: (BigDecimal) -> BigDecimal = { denominator.fromBase(numerator.toBase(it)) }
    override val fromBase: (BigDecimal) -> BigDecimal = { denominator.toBase(numerator.fromBase(it)) }
}

/** Parenthesize nested quotient symbols: bit/second/second reads as `(b/s)/s`. */
private fun UnitOfMeasurement.symbolInQuotient(): String = if (this is QuotientUnit) "($symbol)" else symbol

/**
 * Divide this amount by [other].
 *
 * - If [other] is a rate whose numerator dimension matches this amount's dimension, the quotient cancels: `1 GiB / (10
 *   Mb/s)` yields the transfer time in seconds.
 * - Otherwise the result is a rate in the structural [QuotientUnit] of the two units, expressed per one unit of
 *   [other]: `100 Mb / 3 h` yields `33.33 Mb/h`.
 *
 * The prefix follows the library's carry-over policy, [Amount.copy]: kept when the result unit admits it, reset to the
 * prefix group's default otherwise.
 *
 * @throws ArithmeticException if [other] is zero
 */
operator fun Amount.div(other: Amount): Amount {
    val otherUnit = other.unit
    if (otherUnit is QuotientUnit && unit.dimension == otherUnit.numerator.dimension) {
        val magnitudeInNumeratorUnit = convertTo(otherUnit.numerator).magnitude
        return copy(
            magnitude = magnitudeInNumeratorUnit.divideWithMathContext(other.magnitude),
            unit = otherUnit.denominator,
        )
    }
    return copy(magnitude = magnitude.divideWithMathContext(other.magnitude), unit = QuotientUnit(unit, other.unit))
}

/**
 * Multiply this amount by [other]. Only cancellation of a quotient is supported: one operand must be a rate and the
 * other an amount of its denominator dimension, e.g. `100 Mb/s * 2 min = 12 Gb` (in either order). All other products
 * would need a product dimension, which komputus deliberately does not model.
 *
 * The prefix follows the library's carry-over policy, [Amount.copy]: kept when the result unit admits it, reset to the
 * prefix group's default otherwise.
 *
 * @throws IllegalArgumentException if neither operand is a quotient matching the other's dimension
 */
operator fun Amount.times(other: Amount): Amount {
    val thisUnit = unit
    if (thisUnit is QuotientUnit && other.unit.dimension == thisUnit.denominator.dimension) {
        val magnitudeInDenominatorUnit = other.convertTo(thisUnit.denominator).magnitude
        return copy(
            magnitude = magnitude.multiplyWithMathContext(magnitudeInDenominatorUnit),
            unit = thisUnit.numerator,
        )
    }
    val otherUnit = other.unit
    if (otherUnit is QuotientUnit && unit.dimension == otherUnit.denominator.dimension) {
        return other * this
    }
    throw IllegalArgumentException(
        "Cannot multiply ${unit.dimension} by ${other.unit.dimension}: " +
            "one operand must be a quotient and the other an amount of its denominator dimension"
    )
}

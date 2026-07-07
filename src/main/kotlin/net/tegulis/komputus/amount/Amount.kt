package net.tegulis.komputus.amount

import java.math.BigDecimal
import java.math.BigInteger
import java.math.MathContext
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.Objects
import net.tegulis.komputus.divideWithPrefix
import net.tegulis.komputus.multiplyWithPrefix
import net.tegulis.komputus.prefixes.NotScalingPrefix
import net.tegulis.komputus.prefixes.Prefix
import net.tegulis.komputus.toBigDecimalWithMathContext
import net.tegulis.komputus.units.NoUnit
import net.tegulis.komputus.units.UnitOfMeasurement

/**
 * [Amount] allows the specification of a decimal value with a [Prefix] and [UnitOfMeasurement]. The unscaled /
 * unprefixed value is stored in [magnitude].
 *
 * This class provides various functions to work with this amount:
 * - [getScaledValue] to get the value in the desired prefix
 * - [alignPrefix] to find the right prefix to use for formatting the value
 * - [format] to format the amount using the currently set prefix and unit of measurement
 */
class Amount : Comparable<Amount> {

    /** Always stores the unprefixed value. */
    val magnitude: BigDecimal

    /** The *preferred* prefix to use for formatting and retrieving the scaled value. */
    val prefix: Prefix

    /** The unit of measurement. */
    val unit: UnitOfMeasurement

    /**
     * Creates an instance with a [magnitude] that is the [value] scaled with the given [prefix] (e.g. `Amount(1,
     * SI.KILO).magnitude == 1000`).
     *
     * When the prefix is not specified, [NotScalingPrefix] is used.
     *
     * To avoid scaling, but still specify the [net.tegulis.komputus.prefixes.PrefixGroup], use the
     * [net.tegulis.komputus.prefixes.PrefixGroup.noScalingPrefix] of the group.
     */
    constructor(value: BigDecimal, prefix: Prefix = NotScalingPrefix, unit: UnitOfMeasurement = NoUnit) {
        this.prefix = prefix
        this.unit = unit
        this.magnitude = value.multiplyWithPrefix(prefix)
    }

    /**
     * Convenience constructor to make sure [Number]s are converted to [BigDecimal]s with [toBigDecimalWithMathContext].
     */
    constructor(
        value: Number,
        prefix: Prefix = NotScalingPrefix,
        unit: UnitOfMeasurement = NoUnit,
    ) : this(value.toBigDecimalWithMathContext(), prefix, unit)

    /**
     * Creates a copy of [source] instance with a different *preferred* [prefix]. The [magnitude] is carried over
     * without any scaling.
     */
    private constructor(source: Amount, prefix: Prefix) {
        this.magnitude = source.magnitude
        this.prefix = prefix
        this.unit = source.unit
    }

    /**
     * Return a copy of this instance with [prefix] as the preferred prefix. The [magnitude] is carried over without any
     * scaling.
     *
     * Returns `this` when the prefix is already set.
     */
    fun withPrefix(prefix: Prefix): Amount = if (prefix == this.prefix) this else Amount(this, prefix)

    /**
     * Get the [magnitude] scaled with the current or desired prefix.
     *
     * @param prefix The desired prefix to return the value in.
     */
    fun getScaledValue(prefix: Prefix = this.prefix): BigDecimal = magnitude.divideWithPrefix(prefix)

    /**
     * Find the right prefix from the same [net.tegulis.komputus.prefixes.PrefixGroup], that scales this amount above
     * zero, but below the next bigger prefix - and return a new amount with the new prefix.
     *
     * Prefixes are filtered according to the [unit]'s [UnitOfMeasurement.prefixFilter] unless a different filter is
     * provided.
     */
    fun alignPrefix(prefixFilter: (Prefix) -> Boolean = unit.prefixFilter): Amount {
        if (magnitude.abs().compareTo(BigDecimal.ZERO) == 0) {
            return withPrefix(prefix.prefixGroup.noScalingPrefix)
        }
        for (candidatePrefix in prefix.prefixGroup.prefixes.filter(prefixFilter).sortedByDescending { it.value }) {
            if (magnitude.divideWithPrefix(candidatePrefix).abs() >= BigDecimal.ONE) {
                return withPrefix(candidatePrefix)
            }
        }
        return this
    }

    /**
     * Return the full formatting of this amount using the set prefix and unit.
     *
     * @param numberFormat The [DecimalFormat] used to format the amount.
     * @param prefixAndUnitFormatString The [String.format] used to format the amount, prefix, and unit.
     * @return The full formatting of this amount without aligning the prefix first.
     */
    fun format(
        numberFormat: NumberFormat = defaultNumberFormatProvider(),
        prefixAndUnitFormatString: String = defaultPrefixAndUnitFormatString,
    ): String {
        val formattedValue = numberFormat.format(getScaledValue())
        val valueAsFormatted = BigDecimal(formattedValue)
        val unitSymbolPadding = if (prefix.symbol.isNotBlank()) " " else ""
        var unitSymbol = unit.symbol
        if (unitSymbol.isBlank() && unit.name.isNotBlank() && unit.pluralName.isNotBlank()) {
            unitSymbol =
                unitSymbolPadding +
                    if (valueAsFormatted.unscaledValue().abs().compareTo(BigInteger.ONE) == 0) {
                        unit.name
                    } else {
                        unit.pluralName
                    }
        }
        if (unitSymbol.isBlank() && unit.name.isNotBlank() && unit.pluralName.isBlank()) {
            unitSymbol = unitSymbolPadding + unit.name
        }
        return prefixAndUnitFormatString.format(formattedValue, prefix.symbol, unitSymbol)
    }

    /**
     * Align prefix (unless [stickyPrefix] is `true`) and return the full formatting of this amount. Equivalent to
     * [alignPrefix] + [format].
     *
     * @return The full formatting of this amount after aligning the prefix.
     */
    override fun toString(): String = format()

    fun convertTo(unit: UnitOfMeasurement = this.unit): Amount = TODO()

    /** TODO: different units? */
    override fun compareTo(other: Amount): Int = magnitude.compareTo(other.magnitude)

    /**
     * Two instances are equal if they have the same magnitude and unit.
     *
     * TODO: or can be converted to the same unit (eg. 60 seconds == 1 minute)
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Amount) return false
        return magnitude.compareTo(other.magnitude) == 0 && unit == other.unit
    }

    override fun hashCode(): Int = Objects.hash(magnitude.stripTrailingZeros(), unit)

    companion object {

        var defaultPrecision: Int = 0
        var defaultNonTerminatingPrecision: Int = 10
        var defaultRoundingMode: RoundingMode = RoundingMode.HALF_DOWN
        val defaultMathContext: MathContext
            get() = MathContext(defaultPrecision, defaultRoundingMode)

        /**
         * Provides a new instance of [NumberFormat] for default formatting.
         *
         * See the
         * [Synchronization section in NumberFormat](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/text/NumberFormat.html#synchronization):
         * > Number formats are generally not synchronized. It is recommended to create separate format instances for
         * > each thread. If multiple threads access a format concurrently, it must be synchronized externally.
         *
         * See [NumberFormat] and [DecimalFormat].
         */
        var defaultNumberFormatProvider: () -> NumberFormat = {
            DecimalFormat.getInstance().apply { maximumFractionDigits = 2 }
        }
        var defaultPrefixAndUnitFormatString: String = $$"%1$s %2$s%3$s"
        /*
            fun sumForEntities(entityStream: Stream<out JEntity>, componentClass: Class<out Component>): BigDecimal {
                return entityStream.map { entity -> getForEntity(entity, componentClass) }
                    .reduce(BigDecimal::add).orElse(BigDecimal.ZERO)
            }

            fun getForEntity(entity: JEntity, componentClass: Class<out Component>): BigDecimal {
                // TODO: nulls
                /*
                if (entity.hasComponent(componentClass)) {
                    return entity.getComponent(componentClass).rawAmount
                } else {
                    return BigDecimal.ZERO
                }*/
                return BigDecimal.ZERO
            }

        */
    }
}

package net.tegulis.komputus.amount

import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.Locale
import java.util.Objects
import net.tegulis.komputus.amount.Amount.Companion.defaultNonTerminatingPrecision
import net.tegulis.komputus.divideWithMathContext
import net.tegulis.komputus.divideWithPrefix
import net.tegulis.komputus.multiplyWithMathContext
import net.tegulis.komputus.multiplyWithPrefix
import net.tegulis.komputus.prefixes.Prefix
import net.tegulis.komputus.prefixes.PrefixGroup
import net.tegulis.komputus.toBigDecimalWithMathContext
import net.tegulis.komputus.units.Dimension
import net.tegulis.komputus.units.NoUnit
import net.tegulis.komputus.units.UnitOfMeasurement

/**
 * [Amount] allows the specification of a decimal value with a [Prefix] and [UnitOfMeasurement]. The unprefixed,
 * unscaled, actual value is stored in [magnitude]. The scaled value is available in [scaledValue].
 *
 * [Amount] is immutable. Every operation returns a new instance of [Amount]. IF the operation has no effect, the same
 * instance is returned.
 *
 * The class provides various functions to work with this amount:
 * - [getScaledValue] to get the value in the desired [Prefix]
 * - [alignPrefix] to find the right [Prefix] to use for formatting the value
 * - [align] to optionally change the unit of measurement before aligning the [Prefix]
 * - [copy] to create a copy of the instance with modified values
 * - [format] (or [toString]) to format the amount using the currently set [Prefix] and [UnitOfMeasurement]
 *
 * The most powerful part of this class is comparisons and conversions:
 * - [align] can change the unit of measurement to find the best in the same [Dimension]
 * - [compareTo] can compare amounts in the same [Dimension], even in different units
 * - [convertTo] can convert amounts to other units within the same [Dimension]
 *
 * @see PrefixGroup
 * @see UnitOfMeasurement
 */
class Amount : Comparable<Amount> {

    /** Unprefixed, unscaled, actual value (i.e. magnitude) of this amount. */
    val magnitude: BigDecimal

    /** The prefix to use for formatting and retrieving the scaled value. */
    val prefix: Prefix

    /** The unit of measurement. */
    val unit: UnitOfMeasurement

    /** The [magnitude] expressed in the [unit]'s [Dimension]'s [Dimension.baseUnit]. */
    val baseMagnitude: BigDecimal

    /**
     * Create an instance with a [magnitude] = [value] * [prefix]. For example: `Amount(1, SI.KILO).magnitude == 1000`
     *
     * To create a "raw" amount without scaling the provided [value] with the prefix, set [raw] to `true`. For example:
     * `Amount(1000, SI.KILO, raw = true).magnitude == 1000`
     *
     * To specify the [PrefixGroup], use the [PrefixGroup.defaultNotScalingPrefix] of the group.
     *
     * @param value to be scaled with [prefix] to get the desired [magnitude]
     * @param prefix the desired prefix to scale [value] with
     * @param unit the unit of measurement
     * @param raw if set to `true`, [magnitude] will be set to [value] without scaling
     */
    constructor(value: BigDecimal, prefix: Prefix? = null, unit: UnitOfMeasurement = NoUnit, raw: Boolean = false) {
        this.magnitude = if (raw) value else value.multiplyWithPrefix(prefix ?: unit.defaultPrefix)
        this.prefix = prefix ?: unit.defaultPrefix
        this.unit = unit
        this.baseMagnitude = unit.toBase(magnitude)
    }

    /**
     * Convenience constructor to make sure [Number]s are converted to [BigDecimal]s with [toBigDecimalWithMathContext].
     * Equivalent to `Amount(value.toBigDecimalWithMathContext(), prefix, unit, raw)`.
     */
    constructor(
        value: Number,
        prefix: Prefix? = null,
        unit: UnitOfMeasurement = NoUnit,
        raw: Boolean = false,
    ) : this(value.toBigDecimalWithMathContext(), prefix, unit, raw)

    /**
     * Create a copy of this instance with the given [magnitude], [prefix] and [unit]. All parameters default to the
     * current values of the instance, except [prefix]:
     * - if [prefix] is not `null` it is always set
     * - if [unit] is unchanged, the current prefix is kept
     * - if [unit] is changed and the [UnitOfMeasurement.prefixFilter] admits it, the current prefix is kept
     * - otherwise, the default prefix of [unit] is used
     */
    fun copy(
        magnitude: BigDecimal = this.magnitude,
        prefix: Prefix? = null,
        unit: UnitOfMeasurement = this.unit,
    ): Amount {
        val newPrefix =
            when {
                prefix != null -> prefix
                unit == this.unit -> this.prefix
                unit.prefixFilter(this.prefix) -> this.prefix
                else -> unit.defaultPrefix
            }
        return Amount(magnitude, newPrefix, unit, true)
    }

    /**
     * Get the [magnitude] scaled with the current or desired prefix.
     *
     * @param prefix The desired prefix to return the value in.
     */
    fun getScaledValue(prefix: Prefix = this.prefix): BigDecimal = magnitude.divideWithPrefix(prefix)

    /** Shorthand for [getScaledValue]. */
    val scaledValue: BigDecimal
        get() = getScaledValue()

    /**
     * Align this amount using its dimension's alignment strategy by calling [Dimension.align].
     *
     * [Dimension.align] may switch to a different unit (e.g. 3600 seconds align to 1 hour). Use [alignPrefix] to align
     * only the prefix within the current unit.
     */
    fun align(): Amount = unit.dimension.align(this)

    /**
     * Find the right prefix from a [net.tegulis.komputus.prefixes.PrefixGroup], that scales this amount above zero, but
     * below the next bigger prefix - and return a new amount with the new prefix.
     *
     * Prefixes are filtered according to the [unit]'s [UnitOfMeasurement.prefixFilter] unless a different filter is
     * provided. Alignment happens within the group of the current [prefix], unless the filter does not admit that
     * prefix at all - then it happens within the group of the [unit]'s [UnitOfMeasurement.defaultPrefix], so an amount
     * can never get stuck in a group that offers it no fitting prefix.
     */
    fun alignPrefix(prefixFilter: (Prefix) -> Boolean = unit.prefixFilter): Amount {
        val groupPrefix = if (prefixFilter(prefix)) prefix else unit.defaultPrefix
        if (magnitude.abs().compareTo(BigDecimal.ZERO) == 0) {
            if (prefix == groupPrefix.prefixGroup.defaultNotScalingPrefix) return this
            return copy(prefix = groupPrefix.prefixGroup.defaultNotScalingPrefix)
        }
        for (candidatePrefix in groupPrefix.prefixGroup.prefixes.filter(prefixFilter).sortedByDescending { it.value }) {
            if (magnitude.divideWithPrefix(candidatePrefix).abs() >= BigDecimal.ONE) {
                if (candidatePrefix == prefix) return this
                return copy(prefix = candidatePrefix)
            }
        }
        return if (prefix == groupPrefix) this else copy(prefix = groupPrefix)
    }

    /**
     * Return the full formatting of this amount using the set prefix and unit.
     *
     * When the unit has no symbol, [UnitOfMeasurement.name] or [UnitOfMeasurement.pluralName] is chosen based on the
     * *displayed* value: the singular form is used exactly when the value formats to the same string as one does (e.g.
     * `1.0001` with two fraction digits displays as `1` and is singular).
     *
     * Passing [prefix] overrides the prefix used for scaling and display without creating a new instance, so a value
     * can be forced into a specific prefix (e.g. format bits as kilobits with `format(prefix = SI.KILO)`). The override
     * is not filtered by [UnitOfMeasurement.prefixFilter]: any prefix is honoured.
     *
     * @param numberFormat The [DecimalFormat] used to format the amount.
     * @param prefixAndUnitFormatString The [String.format] used to format the amount, prefix, and unit.
     * @param prefix The prefix to scale and display the value with; defaults to this amount's [prefix].
     * @return The full formatting of this amount without aligning the prefix first.
     *
     * TODO: ability to change if unit symbol or name is used
     */
    fun format(
        numberFormat: NumberFormat = defaultNumberFormatProvider(),
        prefixAndUnitFormatString: String = defaultPrefixAndUnitFormatString,
        prefix: Prefix = this.prefix,
    ): String {
        val scaledValue = getScaledValue(prefix)
        val formattedValue = numberFormat.format(scaledValue)
        val unitSymbolPadding = if (prefix.symbol.isNotBlank()) " " else ""
        var unitSymbol = unit.symbol
        if (unitSymbol.isBlank() && unit.name.isNotBlank() && unit.pluralName.isNotBlank()) {
            val displaysAsOne = numberFormat.format(scaledValue.abs()) == numberFormat.format(BigDecimal.ONE)
            unitSymbol = unitSymbolPadding + if (displaysAsOne) unit.name else unit.pluralName
        }
        if (unitSymbol.isBlank() && unit.name.isNotBlank() && unit.pluralName.isBlank()) {
            unitSymbol = unitSymbolPadding + unit.name
        }
        return prefixAndUnitFormatString.format(formattedValue, prefix.symbol, unitSymbol)
    }

    /**
     * Return the full formatting of this amount using the prefix, without aligning it. Use [alignPrefix] + [format] to
     * align.
     *
     * Equivalent to [format] with default arguments.
     */
    override fun toString(): String = format()

    /**
     * Compare this instance with another [Amount] of the same dimension for order. Amounts are compared by their
     * magnitude in the dimension's base unit, so 60 seconds compare equal to 1 minute. Returned value follows
     * conventions in [Comparable.compareTo].
     *
     * @throws IllegalArgumentException if the dimensions are different
     */
    override fun compareTo(other: Amount): Int {
        require(unit.dimension == other.unit.dimension) {
            @Suppress("GrazieInspection")
            "Cannot compare amounts of different dimensions: ${unit.dimension} != ${other.unit.dimension}"
        }
        return baseMagnitude.compareTo(other.baseMagnitude)
    }

    /**
     * Two instances are equal if they have the same dimension and represent the same magnitude in the dimension's base
     * unit (e.g. 60 seconds == 1 minute).
     *
     * Unlike [compareTo], equals never throws: amounts of different dimensions are simply not equal.
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Amount) return false
        return unit.dimension == other.unit.dimension && baseMagnitude.compareTo(other.baseMagnitude) == 0
    }

    override fun hashCode(): Int = Objects.hash(baseMagnitude.stripTrailingZeros(), unit.dimension)

    /**
     * Convert this amount to another [newUnit] of the same dimension, going through the dimension's base unit
     * ([UnitOfMeasurement.toBase] + [UnitOfMeasurement.fromBase]).
     *
     * The prefix is carried over when the target [newUnit]'s [UnitOfMeasurement.prefixFilter] admits it, and reset to
     * the prefix group's [net.tegulis.komputus.prefixes.PrefixGroup.defaultNotScalingPrefix] otherwise.
     *
     * Returns `this` when [newUnit] is already set. Divisions with non-terminating decimal expansions are rounded to
     * [defaultNonTerminatingPrecision], so converting back and forth can be approximate.
     *
     * @throws IllegalArgumentException if the dimensions differ
     */
    fun convertTo(newUnit: UnitOfMeasurement): Amount {
        if (newUnit == this.unit) {
            return this
        }
        require(newUnit.dimension == this.unit.dimension) {
            "Cannot convert between dimensions: ${this.unit.dimension} (${this.unit.name}) and " +
                "${newUnit.dimension} (${newUnit.name})"
        }
        return copy(magnitude = newUnit.fromBase(baseMagnitude), unit = newUnit)
    }

    /**
     * Add [other] to this amount. [other] is converted to this amount's unit first with [convertTo]. The result keeps
     * this instance's unit and prefix.
     *
     * @throws IllegalArgumentException if the dimensions differ
     */
    operator fun plus(other: Amount): Amount = copy(magnitude.add(other.convertTo(unit).magnitude))

    /**
     * Subtract [other] from this amount. [other] is converted to this amount's unit first with [convertTo]. The result
     * keeps this instance's unit and prefix.
     *
     * @throws IllegalArgumentException if the dimensions differ
     */
    operator fun minus(other: Amount): Amount = copy(magnitude.subtract(other.convertTo(unit).magnitude))

    /** Multiply this amount by a dimensionless [factor], keeping unit and prefix. */
    operator fun times(factor: Number): Amount = copy(magnitude.multiplyWithMathContext(factor))

    /**
     * Divide this amount by a dimensionless [divisor], keeping unit and prefix. Divisions with non-terminating decimal
     * expansions are rounded to [defaultNonTerminatingPrecision].
     */
    operator fun div(divisor: Number): Amount = copy(magnitude.divideWithMathContext(divisor))

    /** Negate this amount, keeping unit and prefix. */
    operator fun unaryMinus(): Amount = copy(magnitude.negate())

    companion object {

        // TODO: automatic alignment
        var autoAlignPrefix: Boolean = false
        var autoAlign: Boolean = false
            set(value) {
                field = value
                if (value) autoAlignPrefix = true
            }

        // TODO: make this val and allow changing the precision per instance / arithmetic
        var defaultPrecision: Int = 0
        var defaultNonTerminatingPrecision: Int = 10
        var defaultRoundingMode: RoundingMode = RoundingMode.HALF_DOWN
        val defaultMathContext: MathContext
            get() = MathContext(defaultPrecision, defaultRoundingMode)

        // TODO: make this a configuration data class?
        /**
         * Provides a new instance of [NumberFormat] for default formatting.
         *
         * The default uses [Locale.ROOT] so formatting does not depend on the machine's locale: it always groups with a
         * comma and uses a dot for the decimal point. Replace this provider to format for a specific locale.
         *
         * See the
         * [Synchronization section in NumberFormat](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/text/NumberFormat.html#synchronization):
         * > Number formats are generally not synchronized. It is recommended to create separate format instances for
         * > each thread. If multiple threads access a format concurrently, it must be synchronized externally.
         *
         * See [NumberFormat] and [DecimalFormat].
         */
        var defaultNumberFormatProvider: () -> NumberFormat = {
            DecimalFormat.getInstance(Locale.ROOT).apply { maximumFractionDigits = 2 }
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

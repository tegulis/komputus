package net.tegulis.komputus.units

import java.math.BigDecimal
import net.tegulis.komputus.amount.Amount
import net.tegulis.komputus.prefixes.NotScalingPrefix
import net.tegulis.komputus.prefixes.Prefix

/** Named [UnitOfMeasurement] so it does not collide with Kotlin's [Unit]. */
interface UnitOfMeasurement {
    val name: String
    val pluralName: String
    val symbol: String
    val dimension: Dimension
    /** Filters for the [Prefix]es that are customary with this unit (convention, not math). */
    val prefixFilter: (Prefix) -> Boolean
        get() = { true }

    /**
     * The prefix an amount takes on when it is reset to this unit without an explicit prefix - for example by
     * [Amount.copy] (and therefore [Amount.convertTo] and the quotient operators) when the current prefix is not
     * admitted by [prefixFilter]. It also fixes the [net.tegulis.komputus.prefixes.PrefixGroup] the amount then aligns
     * within, so it decides SI-vs-IEC for a converted amount (e.g. a bandwidth cancelled to [Time.Second]s aligns in
     * SI). Must be admitted by [prefixFilter].
     */
    val defaultPrefix: Prefix
        get() = NotScalingPrefix

    /**
     * Convert a magnitude expressed in this unit into the [dimension]'s [Dimension.baseUnit].
     *
     * Base units should be chosen so that [toBase] multiplies by a terminating decimal wherever possible: comparisons
     * between amounts ([Amount.equals], [Amount.compareTo]) go through [toBase] and are exact when it is.
     */
    val toBase: (BigDecimal) -> BigDecimal
        get() = { it }

    /**
     * Convert a magnitude expressed in the [dimension]'s [Dimension.baseUnit] into this unit.
     *
     * Inverse of [toBase]. Divisions with non-terminating decimal expansions are rounded to
     * [Amount.defaultNonTerminatingPrecision], so round trips through [fromBase] can be approximate.
     */
    val fromBase: (BigDecimal) -> BigDecimal
        get() = { it }

    fun amountOf(amount: Number, prefix: Prefix = defaultPrefix) = Amount(amount, prefix, this)

    fun amountOf(amount: BigDecimal, prefix: Prefix = defaultPrefix) = Amount(amount, prefix, this)
}

object NoUnit : UnitOfMeasurement {
    override val name: String = ""
    override val pluralName: String = ""
    override val symbol: String = ""
    override val dimension: Dimension = NoDimension
}

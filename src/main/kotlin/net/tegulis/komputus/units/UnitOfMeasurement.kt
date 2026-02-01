package net.tegulis.komputus.units

import java.math.BigDecimal
import net.tegulis.komputus.amount.Amount
import net.tegulis.komputus.prefixes.Prefix

/** Named [UnitOfMeasurement] so it does not collide with Kotlin's [Unit]. */
interface UnitOfMeasurement {
    val name: String
    val pluralName: String
    val symbol: String
    val dimension: Dimension
    val conversions: Set<UnitConversion>

    fun amountOf(amount: Number, prefix: Prefix) = Amount(amount, prefix, this)

    fun amountOf(amount: BigDecimal, prefix: Prefix) = Amount(amount, prefix, this)
}

typealias UnitConversion = Pair<UnitOfMeasurement, (BigDecimal) -> BigDecimal>

object NoUnit : UnitOfMeasurement {
    override val name: String = ""
    override val pluralName: String = ""
    override val symbol: String = ""
    override val dimension: Dimension = NoDimension
    override val conversions: Set<UnitConversion> = emptySet()
}

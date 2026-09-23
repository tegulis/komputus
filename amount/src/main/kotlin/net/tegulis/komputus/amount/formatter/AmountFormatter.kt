package net.tegulis.komputus.amount.formatter

import java.text.NumberFormat
import net.tegulis.komputus.amount.Amount
import net.tegulis.komputus.amount.DefaultAmountFormatter

/**
 * Formats an [Amount] as text.
 *
 * Implementations MUST be immutable and thread-safe.
 */
interface AmountFormatter {
    fun getNumberFormat(): NumberFormat

    fun format(amount: Amount): String

    companion object {
        val DEFAULT: DefaultAmountFormatter = DefaultAmountFormatter()
    }
}

/** Alignment a formatter applies before formatting. */
enum class Alignment {
    /** Use the prefix and unit the amount carries. */
    NONE,

    /** Align with [Amount.alignPrefix]. */
    PREFIX,

    /** Use [Amount.aligned]. */
    UNIT_AND_PREFIX,
}

/** Whether prefixes and units are written as symbols (e.g. "2.5 kB") or as names (e.g. "2.5 kilobytes"). */
enum class UnitNotation {
    /**
     * Use names (e.g. "2.5 kilobytes"). If the unit has no name, the prefix will be written as a symbol (e.g. "2.5 k"
     * instead of "2.5 kilo").
     */
    NAMES,
    SYMBOLS,
}

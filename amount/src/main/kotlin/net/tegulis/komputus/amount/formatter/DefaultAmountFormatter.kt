package net.tegulis.komputus.amount

import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Locale
import net.tegulis.komputus.amount.formatter.Alignment
import net.tegulis.komputus.amount.formatter.AmountFormatter
import net.tegulis.komputus.amount.formatter.UnitNotation
import net.tegulis.komputus.prefixes.Prefix
import net.tegulis.komputus.units.Currency

/**
 * The default and general formatter for [Amount]. You can derive other variants of this with [copy].
 *
 * The output is formatted as follows:
 * - [alignment] is applied first, by default a full alignment with prefix and unit
 * - [forcedPrefix] replaces the prefix if set
 * - a [NumberFormat] is acquired from [getNumberFormat]
 * - if [forceCurrencyFractionDigits] is `true`, the fraction digits are forced to
 *   [Currency.CurrencyUnit.fractionDigits]
 * - the formatted value is [Amount.scaledValue]
 * - unit and prefix names are used if [unitNotation] is [UnitNotation.NAMES] or symbols are not available in the unit
 * - if names are used, the prefix is separated from the unit by a space (e.g. "1.5 million EUR").
 * - singular unit name is used if the displayed absolute value equals one: `1.0001` nibbles can becomes "1 nibble",
 *   depending on the fractional digits.
 * - no unit or prefix is added if they are both blank (e.g. `Amount(5)` formats as "5").
 *
 * TODO: Implement formatting of currencies with symbols (e.g. "50€" or "50 ¥" or "$150").
 * TODO: Implement translations of written scales.
 */
data class DefaultAmountFormatter(
    val alignment: Alignment = Alignment.UNIT_AND_PREFIX,
    val unitNotation: UnitNotation = UnitNotation.SYMBOLS,
    /** Drives how [NumberFormat] is created. */
    val locale: Locale = Locale.getDefault(),
    val numberFormatProvider: (Locale) -> NumberFormat = {
        NumberFormat.getInstance(it).apply { maximumFractionDigits = 2 }
    },
    /** Format the amounts in this prefix, applied after [alignment]. `null` keeps the amount's own prefix. */
    val forcedPrefix: Prefix? = null,
    /**
     * Force the fraction digits to [Currency.CurrencyUnit.fractionDigits] when formatting currencies. When `false`,
     * [NumberFormat] alone decides.
     */
    val forceCurrencyFractionDigits: Boolean = false,
) : AmountFormatter {

    override fun getNumberFormat(): NumberFormat = numberFormatProvider(locale)

    override fun format(amount: Amount): String {
        val alignedAmount =
            when (alignment) {
                Alignment.NONE -> amount
                Alignment.PREFIX -> amount.alignPrefix()
                Alignment.UNIT_AND_PREFIX -> amount.align()
            }
        val displayedPrefix = forcedPrefix ?: alignedAmount.prefix
        val scaledValue = alignedAmount.getScaledValue(displayedPrefix)
        val unit = alignedAmount.unit
        val numberFormat = getNumberFormat()
        if (forceCurrencyFractionDigits && unit is Currency.CurrencyUnit) {
            numberFormat.minimumFractionDigits = unit.fractionDigits ?: 0
            numberFormat.maximumFractionDigits = unit.fractionDigits ?: 0
        }
        val formattedValue = numberFormat.format(scaledValue)
        val displaysAsOne = numberFormat.format(scaledValue.abs()) == numberFormat.format(BigDecimal.ONE)
        val useNames = unitNotation == UnitNotation.NAMES || (unit.symbol.isBlank() && unit.name.isNotBlank())
        val unitText =
            if (useNames) {
                if (displaysAsOne || unit.pluralName.isBlank()) unit.name else unit.pluralName
            } else {
                unit.symbol
            }
        val prefixIsWord = displayedPrefix.symbol == displayedPrefix.name
        val prefixText =
            when {
                displayedPrefix.symbol.isBlank() -> ""
                prefixIsWord -> displayedPrefix.symbol
                useNames -> displayedPrefix.name
                else -> displayedPrefix.symbol
            }
        val separator = if (prefixIsWord && prefixText.isNotBlank() && unitText.isNotBlank()) " " else ""
        val prefixAndUnit = prefixText + separator + unitText
        return if (prefixAndUnit.isBlank()) formattedValue else "$formattedValue $prefixAndUnit"
    }
}

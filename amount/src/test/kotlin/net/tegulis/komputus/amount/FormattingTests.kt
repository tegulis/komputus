package net.tegulis.komputus.amount

import com.google.common.truth.Truth.assertThat
import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Locale
import net.tegulis.komputus.amount.formatter.Alignment
import net.tegulis.komputus.amount.formatter.AmountFormatter
import net.tegulis.komputus.amount.formatter.UnitNotation
import net.tegulis.komputus.prefixes.IEC
import net.tegulis.komputus.prefixes.LongScale
import net.tegulis.komputus.prefixes.NotScalingPrefix
import net.tegulis.komputus.prefixes.SI
import net.tegulis.komputus.units.BinaryInformation
import net.tegulis.komputus.units.Currency
import net.tegulis.komputus.units.Dimension
import net.tegulis.komputus.units.NoDimension
import net.tegulis.komputus.units.UnitOfMeasurement
import net.tegulis.komputus.units.money
import net.tegulis.komputus.units.ofBytes
import net.tegulis.komputus.units.ofSeconds
import org.junit.jupiter.api.Test

class FormattingTests {

    object NoSymbolTestUnit : UnitOfMeasurement {
        override val name: String = "test"
        override val pluralName: String = "tests"
        override val symbol: String = ""
        override val dimension: Dimension = NoDimension
    }

    private fun withFractionDigits(maximum: Int): DefaultAmountFormatter =
        AmountFormatter.DEFAULT.copy(
            numberFormatProvider = {
                AmountFormatter.DEFAULT.getNumberFormat().apply {
                    minimumFractionDigits = 0
                    maximumFractionDigits = maximum
                }
            }
        )

    @Test
    fun `UnitOfMeasurement#name and #pluralName are used when #symbol is blank`() {
        val precise = withFractionDigits(10)
        val lossy = withFractionDigits(0)
        val zero = Amount(0, NotScalingPrefix, NoSymbolTestUnit)
        assertFormatting(zero, "0 tests", formatter = precise)
        assertFormatting(zero, "0 tests", formatter = lossy)
        val some = Amount(0.0001, NotScalingPrefix, NoSymbolTestUnit)
        assertFormatting(some, "0.0001 tests", formatter = precise)
        assertFormatting(some, "0 tests", formatter = lossy)
        val negativeSome = Amount(-0.0001, NotScalingPrefix, NoSymbolTestUnit)
        assertFormatting(negativeSome, "-0.0001 tests", formatter = precise)
        assertFormatting(negativeSome, "-0 tests", formatter = lossy)
        val one = Amount(1, NotScalingPrefix, NoSymbolTestUnit)
        assertFormatting(one, "1 test", formatter = precise)
        assertFormatting(one, "1 test", formatter = lossy)
        val negativeOne = Amount(-1, NotScalingPrefix, NoSymbolTestUnit)
        assertFormatting(negativeOne, "-1 test", formatter = precise)
        assertFormatting(negativeOne, "-1 test", formatter = lossy)
        val more = Amount(1.0001, NotScalingPrefix, NoSymbolTestUnit)
        assertFormatting(more, "1.0001 tests", formatter = precise)
        assertFormatting(more, "1 test", formatter = lossy)
        val negativeMore = Amount(-1.0001, NotScalingPrefix, NoSymbolTestUnit)
        assertFormatting(negativeMore, "-1.0001 tests", formatter = precise)
        assertFormatting(negativeMore, "-1 test", formatter = lossy)
    }

    @Test
    fun `values that do not display as one are plural`() {
        val formatter = withFractionDigits(2)
        assertFormatting(Amount(0.1, NotScalingPrefix, NoSymbolTestUnit), "0.1 tests", formatter = formatter)
        assertFormatting(Amount(0.01, NotScalingPrefix, NoSymbolTestUnit), "0.01 tests", formatter = formatter)
        assertFormatting(Amount(-0.1, NotScalingPrefix, NoSymbolTestUnit), "-0.1 tests", formatter = formatter)
        assertFormatting(Amount(10, NotScalingPrefix, NoSymbolTestUnit), "10 tests", formatter = formatter)
        // Values that round to one in this format are displayed as one, and are therefore singular
        assertFormatting(Amount(0.999, NotScalingPrefix, NoSymbolTestUnit), "1 test", formatter = formatter)
    }

    @Test
    fun `verify formatting assumptions of NumberFormat`() {
        assertThat(NumberFormat.getInstance(Locale.US).format(1500)).isEqualTo("1,500")
        assertThat(NumberFormat.getInstance(Locale.GERMAN).format(1500)).isEqualTo("1.500")
    }

    @Test
    fun `the locale drives grouping and decimal separators`() {
        val us = AmountFormatter.DEFAULT.copy(locale = Locale.US)
        val german = AmountFormatter.DEFAULT.copy(locale = Locale.GERMANY)
        val thousands = Amount(1500, NotScalingPrefix, NoSymbolTestUnit)
        assertFormatting(thousands, "1,500 tests", formatter = us)
        assertFormatting(thousands, "1.500 tests", formatter = german)
        val fraction = Amount(1.5, NotScalingPrefix, NoSymbolTestUnit)
        assertFormatting(fraction, "1.5 tests", formatter = us)
        assertFormatting(fraction, "1,5 tests", formatter = german)
    }

    @Test
    fun `a prefix override is honoured without applying prefix filters`() {
        val precise = withFractionDigits(3)
        val twoThousand = Amount(2000, NotScalingPrefix, NoSymbolTestUnit)
        // The stored value is unchanged; only its displayed scaling and prefix change
        assertFormatting(twoThousand, "2 kilotests", formatter = precise.copy(forcedPrefix = SI.KILO))
        assertFormatting(twoThousand, "0.002 megatests", formatter = precise.copy(forcedPrefix = SI.MEGA))
        // Plurality follows the displayed value: forced to display as one, so singular
        assertFormatting(
            Amount(1000, NotScalingPrefix, NoSymbolTestUnit),
            "1 kilotest",
            formatter = precise.copy(forcedPrefix = SI.KILO),
        )
        // Any prefix is honoured, even one from another group
        assertFormatting(Amount.ofBytes(1, IEC.TEBI), "1,099.512 GB", formatter = precise.copy(forcedPrefix = SI.GIGA))
    }

    @Test
    fun `a unit with a symbol takes the prefix symbol, a unit with only a name takes the prefix name`() {
        assertFormatting(Amount.ofBytes(2, IEC.KIBI), "2 KiB")
        assertFormatting(Amount(2, SI.KILO, BinaryInformation.Nibble), "2 kilonibbles")
        // A unit with neither keeps the prefix symbol, and nothing follows a bare number
        assertFormatting(Amount(2, SI.KILO), "2 k")
        assertFormatting(Amount(5), "5")
    }

    @Test
    fun `NAME notation writes prefix and unit out`() {
        val names = AmountFormatter.DEFAULT.copy(alignment = Alignment.NONE, unitNotation = UnitNotation.NAMES)
        assertFormatting(Amount.ofBytes(1, IEC.KIBI), "1 kibibyte", formatter = names)
        assertFormatting(Amount.ofBytes(1.5, IEC.KIBI), "1.5 kibibytes", formatter = names)
        assertFormatting(Amount.ofSeconds(90), "90 seconds", formatter = names)
        assertFormatting(Amount(2, SI.KILO), "2 kilo", formatter = names)
    }

    @Test
    fun `written scales are separated from the unit by a space`() {
        assertFormatting(1500.money(Currency.EUR).align(), "1.5 thousand EUR")
        assertFormatting(Amount(1_500_000_000, LongScale.NONE, Currency.EUR).align(), "1.5 milliard EUR")
        val names = AmountFormatter.DEFAULT.copy(unitNotation = UnitNotation.NAMES)
        assertFormatting(2_500_000.money(Currency.EUR).align(), "2.5 million Euros", formatter = names)
    }

    @Test
    fun `unit fraction digits are fixed for unscaled amounts only`() {
        val fractionFormatter = AmountFormatter.DEFAULT.copy(forceCurrencyFractionDigits = true)
        assertFormatting(5.money(Currency.EUR), "5.00 EUR", formatter = fractionFormatter)
        assertFormatting(5.money(Currency.JPY), "5 JPY", formatter = fractionFormatter)
        assertFormatting(5.money(Currency.BHD), "5.000 BHD", formatter = fractionFormatter)
        // The digits round, they are not just padding
        assertFormatting(BigDecimal("1.999").money(Currency.EUR), "2.00 EUR", formatter = fractionFormatter)
        // No customary digits: the number format decides
        assertFormatting(5.money(), "5 ¤")
        assertFormatting(Amount.ofSeconds(5), "5 s")
        // Scaled money is written the way it is read, without the minor unit digits
        assertFormatting(1500.money(Currency.EUR).align(), "1.5 thousand EUR")
        // The rule can be switched off
        val free = AmountFormatter.DEFAULT.copy(forceCurrencyFractionDigits = false)
        assertFormatting(5.money(Currency.EUR), "5 EUR", formatter = free)
    }

    @Test
    fun `the formatter can align for display`() {
        val seconds = Amount.ofSeconds(3600)
        assertFormatting(seconds, "3,600 s", formatter = AmountFormatter.DEFAULT.copy(alignment = Alignment.NONE))
        assertFormatting(seconds, "1 h")
        val bytes = Amount.ofBytes(2048)
        assertFormatting(bytes, "2,048 B", formatter = AmountFormatter.DEFAULT.copy(alignment = Alignment.NONE))
        assertFormatting(bytes, "2 KiB", formatter = AmountFormatter.DEFAULT.copy(alignment = Alignment.PREFIX))
        // The prefix override is applied after alignment
        assertFormatting(bytes, "2.05 kB", formatter = AmountFormatter.DEFAULT.copy(forcedPrefix = SI.KILO))
        // The amounts themselves are unchanged
        assertThat(seconds.prefix).isSameInstanceAs(SI.NONE)
        assertThat(bytes.prefix).isSameInstanceAs(IEC.NONE)
    }

    @Test
    fun `toString() uses the default formatter, which can be replaced process-wide`() {
        val original = Amount.defaultFormatter
        try {
            assertThat(Amount(1500, NotScalingPrefix, NoSymbolTestUnit).toString()).isEqualTo("1,500 tests")
            Amount.defaultFormatter = AmountFormatter.DEFAULT.copy(locale = Locale.GERMANY)
            assertThat(Amount(1500, NotScalingPrefix, NoSymbolTestUnit).toString()).isEqualTo("1.500 tests")
        } finally {
            Amount.defaultFormatter = original
        }
    }

    companion object {
        fun assertFormatting(amount: Amount, expected: String, formatter: AmountFormatter = AmountFormatter.DEFAULT) {
            assertThat(amount.format(formatter)).isEqualTo(expected)
        }

        fun assertFormatting(pair: Pair<Amount, String>, formatter: AmountFormatter = AmountFormatter.DEFAULT) =
            assertFormatting(pair.first, pair.second, formatter)
    }
}

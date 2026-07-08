package net.tegulis.komputus.amount

import com.google.common.truth.Truth.assertThat
import java.text.DecimalFormat
import java.util.Locale
import net.tegulis.komputus.prefixes.NotScalingPrefix
import net.tegulis.komputus.units.Dimension
import net.tegulis.komputus.units.NoDimension
import net.tegulis.komputus.units.UnitConversion
import net.tegulis.komputus.units.UnitOfMeasurement
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class FormatTests {

    object NoSymbolTestUnit : UnitOfMeasurement {
        override val name: String = "test"
        override val pluralName: String = "tests"
        override val symbol: String = ""
        override val dimension: Dimension = NoDimension
        override val conversions: Set<UnitConversion> = emptySet()
    }

    @Test
    @DisplayName("UnitOfMeasurement.name and .pluralName are used when .symbol is blank")
    @Suppress("GrazieStyle")
    fun `UnitOfMeasurement#name and #pluralName are used when #symbol is blank`() {
        val preciseNumberFormat =
            DecimalFormat.getInstance().apply {
                minimumFractionDigits = 0
                maximumFractionDigits = 10
            }
        val lossyNumberFormat =
            DecimalFormat.getInstance().apply {
                minimumFractionDigits = 0
                maximumFractionDigits = 0
            }
        val zero = Amount(0, NotScalingPrefix, NoSymbolTestUnit)
        assertThat(zero.format(preciseNumberFormat)).isEqualTo("0 tests")
        assertThat(zero.format(lossyNumberFormat)).isEqualTo("0 tests")
        val some = Amount(0.0001, NotScalingPrefix, NoSymbolTestUnit)
        assertThat(some.format(preciseNumberFormat)).isEqualTo("0.0001 tests")
        assertThat(some.format(lossyNumberFormat)).isEqualTo("0 tests")
        val negativeSome = Amount(-0.0001, NotScalingPrefix, NoSymbolTestUnit)
        assertThat(negativeSome.format(preciseNumberFormat)).isEqualTo("-0.0001 tests")
        assertThat(negativeSome.format(lossyNumberFormat)).isEqualTo("-0 tests")
        val one = Amount(1, NotScalingPrefix, NoSymbolTestUnit)
        assertThat(one.format(preciseNumberFormat)).isEqualTo("1 test")
        assertThat(one.format(lossyNumberFormat)).isEqualTo("1 test")
        val negativeOne = Amount(-1, NotScalingPrefix, NoSymbolTestUnit)
        assertThat(negativeOne.format(preciseNumberFormat)).isEqualTo("-1 test")
        assertThat(negativeOne.format(lossyNumberFormat)).isEqualTo("-1 test")
        val more = Amount(1.0001, NotScalingPrefix, NoSymbolTestUnit)
        assertThat(more.format(preciseNumberFormat)).isEqualTo("1.0001 tests")
        assertThat(more.format(lossyNumberFormat)).isEqualTo("1 test")
        val negativeMore = Amount(-1.0001, NotScalingPrefix, NoSymbolTestUnit)
        assertThat(negativeMore.format(preciseNumberFormat)).isEqualTo("-1.0001 tests")
        assertThat(negativeMore.format(lossyNumberFormat)).isEqualTo("-1 test")
    }

    @Test
    @DisplayName("Values that do not display as one are plural")
    fun `values that do not display as one are plural`() {
        val numberFormat =
            DecimalFormat.getInstance(Locale.US).apply {
                minimumFractionDigits = 0
                maximumFractionDigits = 2
            }
        assertThat(Amount(0.1, NotScalingPrefix, NoSymbolTestUnit).format(numberFormat)).isEqualTo("0.1 tests")
        assertThat(Amount(0.01, NotScalingPrefix, NoSymbolTestUnit).format(numberFormat)).isEqualTo("0.01 tests")
        assertThat(Amount(-0.1, NotScalingPrefix, NoSymbolTestUnit).format(numberFormat)).isEqualTo("-0.1 tests")
        assertThat(Amount(10, NotScalingPrefix, NoSymbolTestUnit).format(numberFormat)).isEqualTo("10 tests")
        // Values that round to one in this format are displayed as one, and are therefore singular
        assertThat(Amount(0.999, NotScalingPrefix, NoSymbolTestUnit).format(numberFormat)).isEqualTo("1 test")
    }

    @Test
    @DisplayName("format() supports locales with grouping and comma decimal separators")
    fun `format() supports locales with grouping and comma decimal separators`() {
        val usNumberFormat = DecimalFormat.getInstance(Locale.US).apply { maximumFractionDigits = 2 }
        val germanNumberFormat = DecimalFormat.getInstance(Locale.GERMANY).apply { maximumFractionDigits = 2 }
        val thousands = Amount(1500, NotScalingPrefix, NoSymbolTestUnit)
        assertThat(thousands.format(usNumberFormat)).isEqualTo("1,500 tests")
        assertThat(thousands.format(germanNumberFormat)).isEqualTo("1.500 tests")
        val fraction = Amount(1.5, NotScalingPrefix, NoSymbolTestUnit)
        assertThat(fraction.format(usNumberFormat)).isEqualTo("1.5 tests")
        assertThat(fraction.format(germanNumberFormat)).isEqualTo("1,5 tests")
    }

    @Test
    @DisplayName("toString() supports number formats with grouping separators")
    fun `toString() supports number formats with grouping separators`() {
        val originalProvider = Amount.defaultNumberFormatProvider
        try {
            Amount.defaultNumberFormatProvider = {
                DecimalFormat.getInstance(Locale.US).apply { maximumFractionDigits = 2 }
            }
            assertThat(Amount(1500, NotScalingPrefix, NoSymbolTestUnit).toString()).isEqualTo("1,500 tests")
        } finally {
            Amount.defaultNumberFormatProvider = originalProvider
        }
    }
}

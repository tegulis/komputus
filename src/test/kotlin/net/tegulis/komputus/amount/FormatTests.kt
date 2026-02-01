package net.tegulis.komputus.amount

import com.google.common.truth.Truth.assertThat
import java.text.DecimalFormat
import net.tegulis.komputus.prefixes.NoScalingPrefix
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
        val zero = Amount(0, NoScalingPrefix, NoSymbolTestUnit)
        assertThat(zero.format(preciseNumberFormat)).isEqualTo("0 tests")
        assertThat(zero.format(lossyNumberFormat)).isEqualTo("0 tests")
        val some = Amount(0.0001, NoScalingPrefix, NoSymbolTestUnit)
        assertThat(some.format(preciseNumberFormat)).isEqualTo("0.0001 test")
        assertThat(some.format(lossyNumberFormat)).isEqualTo("0 tests")
        val negativeSome = Amount(-0.0001, NoScalingPrefix, NoSymbolTestUnit)
        assertThat(negativeSome.format(preciseNumberFormat)).isEqualTo("-0.0001 test")
        assertThat(negativeSome.format(lossyNumberFormat)).isEqualTo("-0 tests")
        val one = Amount(1, NoScalingPrefix, NoSymbolTestUnit)
        assertThat(one.format(preciseNumberFormat)).isEqualTo("1 test")
        assertThat(one.format(lossyNumberFormat)).isEqualTo("1 test")
        val negativeOne = Amount(-1, NoScalingPrefix, NoSymbolTestUnit)
        assertThat(negativeOne.format(preciseNumberFormat)).isEqualTo("-1 test")
        assertThat(negativeOne.format(lossyNumberFormat)).isEqualTo("-1 test")
        val more = Amount(1.0001, NoScalingPrefix, NoSymbolTestUnit)
        assertThat(more.format(preciseNumberFormat)).isEqualTo("1.0001 tests")
        assertThat(more.format(lossyNumberFormat)).isEqualTo("1 test")
        val negativeMore = Amount(-1.0001, NoScalingPrefix, NoSymbolTestUnit)
        assertThat(negativeMore.format(preciseNumberFormat)).isEqualTo("-1.0001 tests")
        assertThat(negativeMore.format(lossyNumberFormat)).isEqualTo("-1 test")
    }
}

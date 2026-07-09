package net.tegulis.komputus

import com.google.common.truth.Truth.assertThat
import java.math.BigDecimal
import net.tegulis.komputus.amount.Amount
import org.junit.jupiter.api.Test

class BigDecimalTests {

    @Test
    @Suppress("USELESS_CAST")
    fun numbersConvertToBigDecimal() {
        val expectedFraction = BigDecimal("1.23")
        val actual = BigDecimal("1.23").toBigDecimalWithMathContext()
        assertThat(actual).isEquivalentAccordingToCompareTo(expectedFraction)
        assertThat((1.23 as Double).toBigDecimalWithMathContext()).isEquivalentAccordingToCompareTo(expectedFraction)
        assertThat((1.23f as Float).toBigDecimalWithMathContext()).isEquivalentAccordingToCompareTo(expectedFraction)
        val expectedWhole = BigDecimal("123.00")
        assertThat((123L as Long).toBigDecimalWithMathContext()).isEquivalentAccordingToCompareTo(expectedWhole)
        @Suppress("REDUNDANT_CALL_OF_CONVERSION_METHOD")
        assertThat((123.toInt()).toBigDecimalWithMathContext()).isEquivalentAccordingToCompareTo(expectedWhole)
        assertThat((123.toShort()).toBigDecimalWithMathContext()).isEquivalentAccordingToCompareTo(expectedWhole)
        assertThat((123.toByte()).toBigDecimalWithMathContext()).isEquivalentAccordingToCompareTo(expectedWhole)
        assertThat((123 as Number).toBigDecimalWithMathContext()).isEquivalentAccordingToCompareTo(expectedWhole)
    }

    @Test
    fun `non-terminating decimal expansions are equal`() {
        // 6 / 7 = 0.857142857142...
        val six = BigDecimal("6")
        val seven = BigDecimal("7")
        val sixOverSevenExpected = "0.857142857142857142857143".substring(0, Amount.defaultNonTerminatingPrecision + 2)
        assertThat(sixOverSevenExpected.length).isEqualTo(Amount.defaultNonTerminatingPrecision + 2)
        val sixOverSevenActual = six.divideWithMathContext(seven)
        assertThat(sixOverSevenActual.toString()).isEqualTo(sixOverSevenExpected)
        // 3 / 7 = 0.428571428571...
        // TODO: Rounding error hits here
        // val three = BigDecimal("3")
        // val threeOverSevenExpected =
        // "0.428571428571428571428571".substring(0, Amount.defaultNonTerminatingPrecision + 2)
        // assertThat(threeOverSevenExpected.length).isEqualTo(Amount.defaultNonTerminatingPrecision + 2)
        // val threeOverSevenActual = three.divideWithMathContext(seven)
        // assertThat(threeOverSevenActual.toString()).isEqualTo(threeOverSevenExpected)
    }
}

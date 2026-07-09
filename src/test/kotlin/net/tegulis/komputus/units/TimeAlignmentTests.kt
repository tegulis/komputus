package net.tegulis.komputus.units

import com.google.common.truth.Truth.assertThat
import java.math.BigDecimal
import java.text.DecimalFormat
import java.util.Locale
import net.tegulis.komputus.amount.Amount
import net.tegulis.komputus.prefixes.SI
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

class TimeAlignmentTests {

    @ParameterizedTest(name = "{0} aligns to {2} × {1}")
    @MethodSource("timeAlignments")
    @DisplayName("Time.align switches to the largest unit with a magnitude of at least one")
    fun `Time#align switches to the largest unit with a magnitude of at least one`(
        amount: Amount,
        expectedUnit: UnitOfMeasurement,
        expectedMagnitude: BigDecimal,
    ) {
        val aligned = amount.align()
        assertThat(aligned.unit).isEqualTo(expectedUnit)
        assertThat(aligned.magnitude).isEquivalentAccordingToCompareTo(expectedMagnitude)
    }

    @Test
    @DisplayName("Time.align falls back to prefix alignment below one minute")
    fun `Time#align falls back to prefix alignment below one minute`() {
        val aligned = Amount.ofSeconds(0.5).align()
        assertThat(aligned.unit).isEqualTo(Time.Second)
        assertThat(aligned.prefix).isEqualTo(SI.MILLI)
        assertThat(aligned.getScaledValue()).isEquivalentAccordingToCompareTo(BigDecimal("500"))
    }

    @Test
    @DisplayName("Amount.alignPrefix respects the second's prefix filter")
    fun `Amount#alignPrefix respects the second's prefix filter`() {
        // Seconds admit only major prefixes with power <= 0, so 5000 seconds stay at SI.NONE...
        val longTime = Amount(5000, SI.NONE, Time.Second).alignPrefix()
        assertThat(longTime.prefix).isEqualTo(SI.NONE)
        // ...and 0.5 seconds align to 500 milliseconds, skipping DECI and CENTI
        val shortTime = Amount(0.5, SI.NONE, Time.Second).alignPrefix()
        assertThat(shortTime.prefix).isEqualTo(SI.MILLI)
        assertThat(shortTime.getScaledValue()).isEquivalentAccordingToCompareTo(BigDecimal("500"))
    }

    @Test
    @DisplayName("Time.align rejects amounts of other dimensions")
    fun `Time#align rejects amounts of other dimensions`() {
        assertThrows<IllegalArgumentException> { Time.align(Amount.ofBytes(5)) }
    }

    @Test
    @DisplayName("Aligned time amounts format naturally")
    fun `aligned time amounts format naturally`() {
        val numberFormat = DecimalFormat.getInstance(Locale.US).apply { maximumFractionDigits = 2 }
        assertThat(Amount.ofSeconds(3600).align().format(numberFormat)).isEqualTo("1 h")
        assertThat(Amount.ofSeconds(90).align().format(numberFormat)).isEqualTo("1.5 min")
        assertThat(Amount.ofSeconds(0.5).align().format(numberFormat)).isEqualTo("500 ms")
    }

    companion object {
        @JvmStatic
        fun timeAlignments(): List<Arguments> =
            listOf(
                Arguments.of(Amount.ofSeconds(60), Time.Minute, BigDecimal("1")),
                Arguments.of(Amount.ofSeconds(90), Time.Minute, BigDecimal("1.5")),
                Arguments.of(Amount.ofSeconds(3600), Time.Hour, BigDecimal("1")),
                Arguments.of(Amount.ofSeconds(7200), Time.Hour, BigDecimal("2")),
                Arguments.of(Amount.ofSeconds(86400), Time.Day, BigDecimal("1")),
                Arguments.of(Amount.ofSeconds(604800), Time.Week, BigDecimal("1")),
                Arguments.of(Amount.ofSeconds(1209600), Time.Week, BigDecimal("2")),
                Arguments.of(Amount.ofSeconds(59), Time.Second, BigDecimal("59")),
                Arguments.of(Amount.ofSeconds(0), Time.Second, BigDecimal("0")),
                Arguments.of(Amount.ofSeconds(-3600), Time.Hour, BigDecimal("-1")),
                Arguments.of(Amount.ofMinutes(120), Time.Hour, BigDecimal("2")),
                Arguments.of(Amount.ofDays(14), Time.Week, BigDecimal("2")),
            )
    }
}

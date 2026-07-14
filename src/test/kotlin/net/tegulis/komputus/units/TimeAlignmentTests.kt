package net.tegulis.komputus.units

import com.google.common.truth.Truth.assertThat
import java.math.BigDecimal
import java.text.DecimalFormat
import java.util.Locale
import net.tegulis.komputus.amount.Amount
import net.tegulis.komputus.prefixes.IEC
import net.tegulis.komputus.prefixes.NotScalingPrefix
import net.tegulis.komputus.prefixes.SI
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

class TimeAlignmentTests {

    @ParameterizedTest(name = "{0} aligns to {2} × {1}")
    @MethodSource("timeAlignments")
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
    fun `Time#align falls back to prefix alignment below one minute`() {
        val aligned = Amount.ofSeconds(0.5).align()
        assertThat(aligned.unit).isEqualTo(Time.Second)
        assertThat(aligned.prefix).isEqualTo(SI.MILLI)
        assertThat(aligned.getScaledValue()).isEquivalentAccordingToCompareTo(BigDecimal("500"))
    }

    @Test
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
    fun `seconds admit SI prefixes only`() {
        assertThat(Time.Second.prefixFilter(SI.NONE)).isTrue()
        assertThat(Time.Second.prefixFilter(SI.MILLI)).isTrue()
        // Scaling up, and minor prefixes, remain rejected
        assertThat(Time.Second.prefixFilter(SI.KILO)).isFalse()
        assertThat(Time.Second.prefixFilter(SI.CENTI)).isFalse()
        // Other prefix groups are rejected, so converting to Second escapes them (see the alignment test below)
        assertThat(Time.Second.prefixFilter(NotScalingPrefix)).isFalse()
        assertThat(Time.Second.prefixFilter(IEC.NONE)).isFalse()
    }

    @Test
    fun `Time#align reaches milliseconds from units outside the SI prefix group`() {
        val numberFormat = DecimalFormat.getInstance(Locale.US).apply { maximumFractionDigits = 2 }
        // Minutes carry NotScalingPrefix, whose group has no sub-one prefix to scale into. Second rejects that prefix,
        // so converting resets it to Second.defaultPrefix (SI.NONE) - and SI can scale all the way down.
        val fromMinutes = Amount.ofMinutes(0.001).align()
        assertThat(fromMinutes.unit).isEqualTo(Time.Second)
        assertThat(fromMinutes.prefix).isEqualTo(SI.MILLI)
        assertThat(fromMinutes.getScaledValue()).isEquivalentAccordingToCompareTo(BigDecimal("60"))
        assertThat(fromMinutes.format(numberFormat)).isEqualTo("60 ms")
        // The Number extension yields the same amount
        assertThat(0.001.minutes().align().format(numberFormat)).isEqualTo("60 ms")
        // The same escape works from an hour, and from an amount stuck in the IEC group
        assertThat(Amount.ofHours(0.0001).align().format(numberFormat)).isEqualTo("360 ms")
        assertThat(Amount(0.5, IEC.NONE, Time.Second).align().format(numberFormat)).isEqualTo("500 ms")
    }

    @Test
    fun `Time#align rejects amounts of other dimensions`() {
        assertThrows<IllegalArgumentException> { Time.align(Amount.ofBytes(5)) }
    }

    @Test
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

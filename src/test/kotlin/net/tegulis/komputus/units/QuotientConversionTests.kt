package net.tegulis.komputus.units

import com.google.common.truth.Truth.assertThat
import java.math.BigDecimal
import net.tegulis.komputus.amount.Amount
import net.tegulis.komputus.prefixes.NotScalingPrefix
import net.tegulis.komputus.prefixes.SI
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

class QuotientConversionTests {

    @ParameterizedTest(name = "{0} converts to magnitude {2}")
    @MethodSource("conversions")
    fun `rates convert between quotient units of the same dimension pair`(
        amount: Amount,
        target: QuotientUnit,
        expectedMagnitude: BigDecimal,
    ) {
        val converted = amount.convertTo(target)
        assertThat(converted.unit).isEqualTo(target)
        assertThat(converted.magnitude).isEqualToIgnoringScale(expectedMagnitude)
        assertThat(converted).isEqualTo(amount)
    }

    @Test
    fun `the base unit of a quotient dimension is the quotient of the base units`() {
        val dimension = QuotientDimension(BinaryInformation, Time)
        assertThat(dimension.baseUnit).isEqualTo(QuotientUnit(BinaryInformation.Byte, Time.Second))
        assertThat(dimension.units).hasSize(BinaryInformation.units.size * Time.units.size)
        assertThat((2.KiB() / Amount.ofSeconds(1)).baseMagnitude).isEqualToIgnoringScale(BigDecimal("2048"))
        assertThat((Amount.ofBytes(120) / Amount.ofMinutes(1)).baseMagnitude).isEqualToIgnoringScale(BigDecimal("2"))
    }

    @Test
    fun `converting to the same quotient unit returns the same instance`() {
        val rate = Amount.ofBytes(1) / Amount.ofSeconds(1)
        assertThat(rate.convertTo(QuotientUnit(BinaryInformation.Byte, Time.Second))).isSameInstanceAs(rate)
    }

    @Test
    fun `Amount#convertTo to a different dimension throws`() {
        val rate = Amount.ofBytes(1) / Amount.ofSeconds(1)
        assertThrows<IllegalArgumentException> { rate.convertTo(Time.Second) }
        assertThrows<IllegalArgumentException> { rate.convertTo(QuotientUnit(Time.Second, BinaryInformation.Byte)) }
        assertThrows<IllegalArgumentException> {
            Amount.ofSeconds(1).convertTo(QuotientUnit(BinaryInformation.Byte, Time.Second))
        }
    }

    @Test
    fun `the prefix is carried over when the target unit admits it and reset otherwise`() {
        val latency = Amount.ofSeconds(500, SI.MILLI) / Amount.ofBytes(1)
        assertThat(latency.prefix).isSameInstanceAs(SI.MILLI)
        val perMinute = latency.convertTo(QuotientUnit(Time.Minute, BinaryInformation.Byte))
        // The numerator (Minute) does not admit SI.MILLI, so the prefix resets to the quotient's defaultPrefix
        assertThat(perMinute.prefix).isSameInstanceAs(NotScalingPrefix)
        assertThat(perMinute.magnitude).isEqualToIgnoringScale(BigDecimal("0.008333333333"))
    }

    @Test
    fun `converting an already rounded rate compounds the rounding`() {
        val hourly = 100.Mb() / Amount.ofHours(3)
        val perSecond = hourly.convertTo(QuotientUnit(BinaryInformation.Bit, Time.Second))
        assertThat(perSecond.magnitude).isEqualToIgnoringScale(BigDecimal("9259.259256"))
        val direct = 100.Mb() / Amount.ofSeconds(10800)
        assertThat(direct.magnitude).isEqualToIgnoringScale(BigDecimal("9259.259259"))
        assertThat(perSecond).isNotEqualTo(direct)
    }

    companion object {
        @JvmStatic
        fun conversions(): List<Arguments> =
            listOf(
                Arguments.of(
                    Amount.ofBytes(1) / Amount.ofSeconds(1),
                    QuotientUnit(BinaryInformation.Bit, Time.Second),
                    BigDecimal("8"),
                ),
                Arguments.of(
                    Amount.ofBytes(1) / Amount.ofSeconds(1),
                    QuotientUnit(BinaryInformation.Byte, Time.Minute),
                    BigDecimal("60"),
                ),
                Arguments.of(
                    Amount.ofBytes(7200) / Amount.ofHours(1),
                    QuotientUnit(BinaryInformation.Byte, Time.Minute),
                    BigDecimal("120"),
                ),
                Arguments.of(
                    Amount.ofBits(16) / Amount.ofSeconds(1),
                    QuotientUnit(BinaryInformation.Byte, Time.Second),
                    BigDecimal("2"),
                ),
                Arguments.of(
                    Amount.ofBytes(-1) / Amount.ofSeconds(1),
                    QuotientUnit(BinaryInformation.Bit, Time.Second),
                    BigDecimal("-8"),
                ),
            )
    }
}

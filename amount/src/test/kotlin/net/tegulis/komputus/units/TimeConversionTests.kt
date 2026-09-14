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

class TimeConversionTests {

    @ParameterizedTest(name = "{0} = {2} × {1}")
    @MethodSource("conversions")
    fun `Amount#convertTo produces the expected magnitude in the target unit`(
        amount: Amount,
        targetUnit: UnitOfMeasurement,
        expectedMagnitude: BigDecimal,
    ) {
        val converted = amount.convertTo(targetUnit)
        assertThat(converted.unit).isEqualTo(targetUnit)
        assertThat(converted.magnitude).isEquivalentAccordingToCompareTo(expectedMagnitude)
    }

    @Test
    fun `Amount#baseMagnitude is expressed in seconds`() {
        assertThat(Amount.ofSeconds(45).baseMagnitude).isEquivalentAccordingToCompareTo(BigDecimal("45"))
        assertThat(Amount.ofMinutes(1).baseMagnitude).isEquivalentAccordingToCompareTo(BigDecimal("60"))
        assertThat(Amount.ofHours(2).baseMagnitude).isEquivalentAccordingToCompareTo(BigDecimal("7200"))
        assertThat(Amount.ofWeeks(1).baseMagnitude).isEquivalentAccordingToCompareTo(BigDecimal("604800"))
    }

    @Test
    fun `Amount#convertTo to the same unit returns the same instance`() {
        val amount = Amount.ofSeconds(90)
        assertThat(amount.convertTo(Time.Second)).isSameInstanceAs(amount)
    }

    @Test
    fun `Amount#convertTo to a different dimension throws`() {
        assertThrows<IllegalArgumentException> { Amount.ofSeconds(5).convertTo(BinaryInformation.Byte) }
        assertThrows<IllegalArgumentException> { Amount(5).convertTo(Time.Second) }
    }

    @Test
    fun `Amount#convertTo resets the prefix when the target unit does not admit it`() {
        // Minutes take no prefix at all, so SI.MILLI cannot be carried over: reset to Minute.defaultPrefix
        val fiveHundredMilliseconds = Amount(500, SI.MILLI, Time.Second)
        val inMinutes = fiveHundredMilliseconds.convertTo(Time.Minute)
        assertThat(inMinutes.prefix).isEqualTo(NotScalingPrefix)
        assertThat(inMinutes.magnitude).isEquivalentAccordingToCompareTo(BigDecimal("0.008333333333"))
        // Not even SI.NONE survives the conversion: minutes only ever carry NotScalingPrefix
        assertThat(Amount.ofSeconds(90).convertTo(Time.Minute).prefix).isEqualTo(NotScalingPrefix)
        // ...and the reverse holds for seconds, which only ever carry SI prefixes
        assertThat(Amount.ofMinutes(1.5).convertTo(Time.Second).prefix).isEqualTo(SI.NONE)
    }

    @Test
    fun `round trips through terminating divisions are exact`() {
        val amount = Amount.ofSeconds(90)
        assertThat(amount.convertTo(Time.Minute).convertTo(Time.Second)).isEqualTo(amount)
    }

    @Test
    fun `round trips through non-terminating divisions are approximate`() {
        // 1 / 60 does not terminate and is rounded to Amount.defaultNonTerminatingPrecision (10) digits
        val oneSecond = Amount.ofSeconds(1)
        val inMinutes = oneSecond.convertTo(Time.Minute)
        assertThat(inMinutes.magnitude).isEquivalentAccordingToCompareTo(BigDecimal("0.01666666667"))
        val roundTrip = inMinutes.convertTo(Time.Second)
        assertThat(roundTrip.magnitude).isEquivalentAccordingToCompareTo(BigDecimal("1.0000000002"))
        assertThat(roundTrip).isNotEqualTo(oneSecond)
    }

    companion object {
        @JvmStatic
        fun conversions(): List<Arguments> =
            listOf(
                Arguments.of(Amount.ofSeconds(90), Time.Minute, BigDecimal("1.5")),
                Arguments.of(Amount.ofSeconds(3600), Time.Hour, BigDecimal("1")),
                Arguments.of(Amount.ofMinutes(90), Time.Hour, BigDecimal("1.5")),
                Arguments.of(Amount.ofHours(1), Time.Minute, BigDecimal("60")),
                Arguments.of(Amount.ofHours(48), Time.Day, BigDecimal("2")),
                Arguments.of(Amount.ofDays(1), Time.Second, BigDecimal("86400")),
                Arguments.of(Amount.ofWeeks(2), Time.Day, BigDecimal("14")),
                Arguments.of(Amount.ofWeeks(1), Time.Second, BigDecimal("604800")),
                Arguments.of(Amount.ofSeconds(0), Time.Hour, BigDecimal("0")),
                Arguments.of(Amount.ofSeconds(-90), Time.Minute, BigDecimal("-1.5")),
            )
    }
}

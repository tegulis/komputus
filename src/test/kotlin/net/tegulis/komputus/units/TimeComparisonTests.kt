package net.tegulis.komputus.units

import com.google.common.truth.Truth.assertThat
import java.math.BigDecimal
import net.tegulis.komputus.amount.Amount
import net.tegulis.komputus.prefixes.SI
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

class TimeComparisonTests {

    @ParameterizedTest(name = "{0} == {1}")
    @MethodSource("equalPairs")
    @DisplayName("Time amounts are equal when their base magnitudes match")
    fun `time amounts are equal when their base magnitudes match`(left: Amount, right: Amount) {
        assertThat(left).isEqualTo(right)
        assertThat(right).isEqualTo(left)
        assertThat(left.hashCode()).isEqualTo(right.hashCode())
        assertThat(left.compareTo(right)).isEqualTo(0)
    }

    @Test
    @DisplayName("Amount.equals is scale-insensitive and prefix-insensitive")
    fun `Amount#equals is scale-insensitive and prefix-insensitive`() {
        val seconds = Amount.ofSeconds(BigDecimal("1500"))
        val fractionalSeconds = Amount.ofSeconds(BigDecimal("1500.00"))
        assertThat(seconds).isEqualTo(fractionalSeconds)
        assertThat(seconds.hashCode()).isEqualTo(fractionalSeconds.hashCode())
        val kiloseconds = Amount(1, SI.KILO, Time.Second)
        assertThat(kiloseconds).isEqualTo(Amount.ofSeconds(1000))
        assertThat(kiloseconds.hashCode()).isEqualTo(Amount.ofSeconds(1000).hashCode())
    }

    @Test
    @DisplayName("Amounts of different dimensions are not equal and equals does not throw")
    fun `amounts of different dimensions are not equal and equals does not throw`() {
        val fiveSeconds = Amount.ofSeconds(5)
        assertThat(fiveSeconds).isNotEqualTo(Amount.ofBytes(5))
        assertThat(fiveSeconds).isNotEqualTo(Amount(5))
        assertThat(fiveSeconds).isNotEqualTo(BigDecimal("5"))
        assertThat(fiveSeconds).isNotEqualTo(null)
    }

    @Test
    @DisplayName("Amount.compareTo orders amounts across time units")
    fun `Amount#compareTo orders amounts across time units`() {
        assertThat(Amount.ofSeconds(59) < Amount.ofMinutes(1)).isTrue()
        assertThat(Amount.ofSeconds(61) > Amount.ofMinutes(1)).isTrue()
        assertThat(Amount.ofSeconds(60).compareTo(Amount.ofMinutes(1))).isEqualTo(0)
        assertThat(Amount.ofHours(23) < Amount.ofDays(1)).isTrue()
    }

    @Test
    @DisplayName("Sorting orders amounts of mixed time units correctly")
    fun `sorting orders amounts of mixed time units correctly`() {
        val thirtySeconds = Amount.ofSeconds(30)
        val twoMinutes = Amount.ofMinutes(2)
        val oneHour = Amount.ofHours(1)
        assertThat(listOf(oneHour, thirtySeconds, twoMinutes).sorted())
            .containsExactly(thirtySeconds, twoMinutes, oneHour)
            .inOrder()
    }

    @Test
    @DisplayName("Amount.compareTo throws for different dimensions")
    fun `Amount#compareTo throws for different dimensions`() {
        assertThrows<IllegalArgumentException> { Amount.ofSeconds(5).compareTo(Amount.ofBytes(5)) }
        assertThrows<IllegalArgumentException> { Amount.ofSeconds(5) < Amount(5) }
    }

    companion object {
        @JvmStatic
        fun equalPairs(): List<Arguments> =
            listOf(
                Arguments.of(Amount.ofSeconds(60), Amount.ofMinutes(1)),
                Arguments.of(Amount.ofSeconds(3600), Amount.ofHours(1)),
                Arguments.of(Amount.ofMinutes(90), Amount.ofHours(1.5)),
                Arguments.of(Amount.ofDays(7), Amount.ofWeeks(1)),
                Arguments.of(Amount.ofSeconds(0), Amount.ofWeeks(0)),
            )
    }
}

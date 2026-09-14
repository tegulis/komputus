package net.tegulis.komputus.units

import com.google.common.truth.Truth.assertThat
import net.tegulis.komputus.amount.Amount
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

class QuotientComparisonTests {

    @ParameterizedTest(name = "{0} == {1}")
    @MethodSource("equalRates")
    fun `rates are equal when their base magnitudes match`(left: Amount, right: Amount) {
        assertThat(left).isEqualTo(right)
        assertThat(right).isEqualTo(left)
        assertThat(left.hashCode()).isEqualTo(right.hashCode())
        assertThat(left.compareTo(right)).isEqualTo(0)
    }

    @Test
    fun `rates of different dimension pairs are not equal and equals does not throw`() {
        val rate = Amount.ofBytes(5) / Amount.ofSeconds(1)
        assertThat(rate).isNotEqualTo(Amount.ofBytes(5))
        assertThat(rate).isNotEqualTo(Amount.ofSeconds(5))
        assertThat(rate).isNotEqualTo(Amount.ofSeconds(1) / Amount.ofBytes(5))
        assertThat(rate).isNotEqualTo(rate / Amount.ofSeconds(1))
        assertThat(rate).isNotEqualTo(null)
    }

    @Test
    fun `Amount#compareTo orders rates across quotient units`() {
        assertThat(Amount.ofBytes(1) / Amount.ofSeconds(1) < Amount.ofBits(10) / Amount.ofSeconds(1)).isTrue()
        assertThat(Amount.ofBytes(7200) / Amount.ofHours(1) > Amount.ofBytes(1) / Amount.ofSeconds(1)).isTrue()
        assertThat((Amount.ofBytes(60) / Amount.ofMinutes(1)).compareTo(Amount.ofBytes(1) / Amount.ofSeconds(1)))
            .isEqualTo(0)
    }

    @Test
    fun `sorting orders rates of mixed quotient units correctly`() {
        val slow = Amount.ofBits(4) / Amount.ofSeconds(1)
        val medium = Amount.ofBytes(90) / Amount.ofMinutes(1)
        val fast = Amount.ofBytes(2) / Amount.ofSeconds(1)
        assertThat(listOf(fast, slow, medium).sorted()).containsExactly(slow, medium, fast).inOrder()
    }

    @Test
    fun `Amount#compareTo throws for different dimension pairs`() {
        val rate = Amount.ofBytes(5) / Amount.ofSeconds(1)
        assertThrows<IllegalArgumentException> { rate.compareTo(Amount.ofBytes(5)) }
        assertThrows<IllegalArgumentException> { rate < Amount.ofSeconds(1) / Amount.ofBytes(5) }
    }

    companion object {
        @JvmStatic
        fun equalRates(): List<Arguments> =
            listOf(
                Arguments.of(Amount.ofBits(8) / Amount.ofSeconds(1), Amount.ofBytes(1) / Amount.ofSeconds(1)),
                Arguments.of(Amount.ofBytes(60) / Amount.ofMinutes(1), Amount.ofBytes(1) / Amount.ofSeconds(1)),
                Arguments.of(Amount.ofBytes(3600) / Amount.ofHours(1), Amount.ofBytes(1) / Amount.ofSeconds(1)),
                Arguments.of(1.KiB() / Amount.ofSeconds(1), Amount.ofBytes(1024) / Amount.ofSeconds(1)),
                Arguments.of(Amount.ofBits(0) / Amount.ofSeconds(1), Amount.ofBytes(0) / Amount.ofMinutes(1)),
            )
    }
}

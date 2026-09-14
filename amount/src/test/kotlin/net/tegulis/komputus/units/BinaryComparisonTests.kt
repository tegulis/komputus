package net.tegulis.komputus.units

import com.google.common.truth.Truth.assertThat
import net.tegulis.komputus.amount.Amount
import net.tegulis.komputus.prefixes.IEC
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

class BinaryComparisonTests {

    @ParameterizedTest(name = "{0} == {1}")
    @MethodSource("equalPairs")
    fun `binary amounts are equal when their base magnitudes match`(left: Amount, right: Amount) {
        assertThat(left).isEqualTo(right)
        assertThat(right).isEqualTo(left)
        assertThat(left.hashCode()).isEqualTo(right.hashCode())
        assertThat(left.compareTo(right)).isEqualTo(0)
    }

    @Test
    fun `Amount#equals is prefix-insensitive`() {
        val kibibyte = Amount(1, IEC.KIBI, BinaryInformation.Byte)
        val bytes = Amount(1024, IEC.NONE, BinaryInformation.Byte)
        assertThat(kibibyte).isEqualTo(bytes)
        assertThat(kibibyte.hashCode()).isEqualTo(bytes.hashCode())
    }

    @Test
    fun `Amount#compareTo orders amounts across binary units`() {
        assertThat(Amount.ofBits(7) < Amount.ofBytes(1)).isTrue()
        assertThat(Amount.ofBits(9) > Amount.ofBytes(1)).isTrue()
        assertThat(Amount.ofBits(8).compareTo(Amount.ofBytes(1))).isEqualTo(0)
        assertThat(Amount.ofNibbles(1) < Amount.ofOctets(1)).isTrue()
    }

    @Test
    fun `amounts of mixed binary units can be sorted correctly`() {
        val fourBits = Amount.ofBits(4)
        val oneByte = Amount.ofBytes(1)
        val threeNibbles = Amount.ofNibbles(3)
        assertThat(listOf(threeNibbles, oneByte, fourBits).sorted())
            .containsExactly(fourBits, oneByte, threeNibbles)
            .inOrder()
    }

    @Test
    fun `Amount#compareTo throws for different dimensions`() {
        assertThrows<IllegalArgumentException> { Amount.ofBytes(5).compareTo(Amount.ofSeconds(5)) }
    }

    companion object {
        @JvmStatic
        fun equalPairs(): List<Arguments> =
            listOf(
                Arguments.of(Amount.ofBits(8), Amount.ofBytes(1)),
                Arguments.of(Amount.ofOctets(1), Amount.ofBytes(1)),
                Arguments.of(Amount.ofNibbles(2), Amount.ofBytes(1)),
                Arguments.of(Amount.ofBits(4), Amount.ofNibbles(1)),
                Arguments.of(Amount.ofBits(0), Amount.ofBytes(0)),
            )
    }
}

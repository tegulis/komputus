package net.tegulis.komputus.units

import com.google.common.truth.Truth.assertThat
import java.math.BigDecimal
import net.tegulis.komputus.amount.Amount
import net.tegulis.komputus.prefixes.IEC
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

class BinaryConversionTests {

    @ParameterizedTest(name = "{0} = {1} × {2}")
    @MethodSource("conversions")
    fun `Amount#convertTo produces the expected magnitude in the target unit`(
        amount: Amount,
        expectedMagnitude: BigDecimal,
        targetUnit: UnitOfMeasurement,
    ) {
        val converted = amount.convertTo(targetUnit)
        assertThat(converted.unit).isEqualTo(targetUnit)
        assertThat(converted.magnitude).isEquivalentAccordingToCompareTo(expectedMagnitude)
    }

    @Test
    fun `Amount#baseMagnitude is expressed in bytes`() {
        assertThat(Amount.ofBytes(3).baseMagnitude).isEquivalentAccordingToCompareTo(BigDecimal("3"))
        assertThat(Amount.ofBits(8).baseMagnitude).isEquivalentAccordingToCompareTo(BigDecimal("1"))
        assertThat(Amount.ofNibbles(2).baseMagnitude).isEquivalentAccordingToCompareTo(BigDecimal("1"))
        assertThat(Amount.ofOctets(1).baseMagnitude).isEquivalentAccordingToCompareTo(BigDecimal("1"))
    }

    @Test
    fun `Amount#convertTo carries the prefix over when the target unit admits it`() {
        // IEC.KIBI is major, and all binary units admit major prefixes
        val twoKibibytes = Amount(2, IEC.KIBI, BinaryInformation.Byte)
        val inBits = twoKibibytes.convertTo(BinaryInformation.Bit)
        assertThat(inBits.prefix).isEqualTo(IEC.KIBI)
        assertThat(inBits.magnitude).isEquivalentAccordingToCompareTo(BigDecimal("16384"))
        assertThat(inBits.getScaledValue()).isEquivalentAccordingToCompareTo(BigDecimal("16"))
    }

    @Test
    fun `Amount#convertTo to a different dimension throws`() {
        assertThrows<IllegalArgumentException> { Amount.ofBytes(5).convertTo(Time.Second) }
    }

    @Test
    fun `round trips between binary units are exact`() {
        // Divisions by 8 and 2 terminate, so binary conversions never lose precision
        val bits = Amount.ofBits(12)
        assertThat(bits.convertTo(BinaryInformation.Byte).convertTo(BinaryInformation.Bit)).isEqualTo(bits)
        val nibbles = Amount.ofNibbles(3)
        assertThat(nibbles.convertTo(BinaryInformation.Byte).convertTo(BinaryInformation.Nibble)).isEqualTo(nibbles)
    }

    companion object {
        @JvmStatic
        fun conversions(): List<Arguments> =
            listOf(
                Arguments.of(Amount.ofBytes(1), BigDecimal("8"), BinaryInformation.Bit),
                Arguments.of(Amount.ofBits(16), BigDecimal("2"), BinaryInformation.Byte),
                Arguments.of(Amount.ofBytes(1), BigDecimal("2"), BinaryInformation.Nibble),
                Arguments.of(Amount.ofNibbles(4), BigDecimal("16"), BinaryInformation.Bit),
                Arguments.of(Amount.ofNibbles(2), BigDecimal("1"), BinaryInformation.Octet),
                Arguments.of(Amount.ofOctets(3), BigDecimal("3"), BinaryInformation.Byte),
                Arguments.of(Amount.ofBits(4), BigDecimal("0.5"), BinaryInformation.Byte),
                Arguments.of(Amount.ofBytes(0), BigDecimal("0"), BinaryInformation.Bit),
            )
    }
}

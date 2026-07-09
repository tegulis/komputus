package net.tegulis.komputus.units

import com.google.common.truth.Truth.assertThat
import java.math.BigDecimal
import java.text.DecimalFormat
import java.util.Locale
import net.tegulis.komputus.amount.Amount
import net.tegulis.komputus.prefixes.IEC
import net.tegulis.komputus.prefixes.SI
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class BinaryAlignmentTests {

    @Test
    @DisplayName("BinaryInformation.align aligns the prefix but never switches units")
    fun `BinaryInformation#align aligns the prefix but never switches units`() {
        // Bits use SI prefixes by default
        val bits = Amount.ofBits(16384).align()
        assertThat(bits.unit).isEqualTo(BinaryInformation.Bit)
        assertThat(bits.prefix).isEqualTo(SI.KILO)
        assertThat(bits.getScaledValue()).isEquivalentAccordingToCompareTo(BigDecimal("16.384"))
        // Bytes use IEC prefixes by default
        val bytes = Amount.ofBytes(16384).align()
        assertThat(bytes.unit).isEqualTo(BinaryInformation.Byte)
        assertThat(bytes.prefix).isEqualTo(IEC.KIBI)
        assertThat(bytes.getScaledValue()).isEquivalentAccordingToCompareTo(BigDecimal("16"))
    }

    @Test
    @DisplayName("Amount.alignPrefix respects the binary units' prefix filter")
    fun `Amount#alignPrefix respects the binary units' prefix filter`() {
        // Binary units admit only major prefixes, so 150 bits skip SI.HECTO and stay at SI.NONE
        val bits = Amount.ofBits(150).alignPrefix()
        assertThat(bits.prefix).isEqualTo(SI.NONE)
        val moreBits = Amount.ofBits(1500).alignPrefix()
        assertThat(moreBits.prefix).isEqualTo(SI.KILO)
    }

    @Test
    @DisplayName("BinaryInformation.align rejects amounts of other dimensions")
    fun `BinaryInformation#align rejects amounts of other dimensions`() {
        assertThrows<IllegalArgumentException> { BinaryInformation.align(Amount.ofSeconds(5)) }
    }

    @Test
    @DisplayName("Aligned binary amounts format naturally")
    fun `aligned binary amounts format naturally`() {
        val numberFormat = DecimalFormat.getInstance(Locale.US).apply { maximumFractionDigits = 2 }
        assertThat(Amount.ofBytes(2048).align().format(numberFormat)).isEqualTo("2 KiB")
        assertThat(Amount.ofBits(2000).align().format(numberFormat)).isEqualTo("2 kb")
    }
}

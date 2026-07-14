package net.tegulis.komputus.units

import com.google.common.truth.Truth.assertThat
import java.math.BigDecimal
import net.tegulis.komputus.amount.Amount
import net.tegulis.komputus.prefixes.IEC
import net.tegulis.komputus.prefixes.SI
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class BinaryAlignmentTests {

    @Test
    fun `BinaryInformation#align rejects amounts of other dimensions`() {
        assertThrows<IllegalArgumentException> { BinaryInformation.align(Amount.ofSeconds(5)) }
    }

    @Test
    fun `BinaryInformation#align aligns the prefix but never switches units`() {
        // Bits use SI prefixes by default
        val bits = Amount.ofBits(16384).align()
        assertThat(bits.unit).isEqualTo(BinaryInformation.Bit)
        assertThat(bits.prefix).isEqualTo(SI.KILO)
        assertThat(bits.getScaledValue()).isEquivalentAccordingToCompareTo(BigDecimal("16.384"))
        // Forcing bits to use IEC prefixes also works
        val bitsIEC = Amount(16384, prefix = IEC.NONE, unit = BinaryInformation.Bit).align()
        assertThat(bitsIEC.unit).isEqualTo(BinaryInformation.Bit)
        assertThat(bitsIEC.prefix).isEqualTo(IEC.KIBI)
        assertThat(bitsIEC.getScaledValue()).isEquivalentAccordingToCompareTo(BigDecimal("16"))
        // Bytes use IEC prefixes by default
        val bytes = Amount.ofBytes(16384).align()
        assertThat(bytes.unit).isEqualTo(BinaryInformation.Byte)
        assertThat(bytes.prefix).isEqualTo(IEC.KIBI)
        assertThat(bytes.getScaledValue()).isEquivalentAccordingToCompareTo(BigDecimal("16"))
        // Forcing bytes to use SI prefixes also works
        val bytesSI = Amount(16384, prefix = SI.NONE, unit = BinaryInformation.Byte).align()
        assertThat(bytesSI.unit).isEqualTo(BinaryInformation.Byte)
        assertThat(bytesSI.prefix).isEqualTo(SI.KILO)
        assertThat(bytesSI.getScaledValue()).isEquivalentAccordingToCompareTo(BigDecimal("16.384"))
    }

    @Test
    fun `Amount#alignPrefix respects the binary units' prefix filter`() {
        // 150 bits skip SI.HECTO and stay at SI.NONE
        val bits = Amount.ofBits(150).alignPrefix()
        assertThat(bits.prefix).isEqualTo(SI.NONE)
        // 150 bits skip SI.HECTO but accept SI.KILO
        val moreBits = Amount.ofBits(1500).alignPrefix()
        assertThat(moreBits.prefix).isEqualTo(SI.KILO)
    }
}

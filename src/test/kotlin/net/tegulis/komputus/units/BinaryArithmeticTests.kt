package net.tegulis.komputus.units

import com.google.common.truth.Truth.assertThat
import net.tegulis.komputus.amount.Amount
import java.math.BigDecimal
import net.tegulis.komputus.prefixes.IEC
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class BinaryArithmeticTests {

    @Test
    @DisplayName("Amount.plus converts the right operand and keeps the left unit and prefix")
    fun `Amount#plus converts the right operand and keeps the left unit and prefix`() {
        val sum = Amount(2, IEC.KIBI, BinaryInformation.Byte) + Amount.ofBytes(1024)
        assertThat(sum.unit).isEqualTo(BinaryInformation.Byte)
        assertThat(sum.prefix).isEqualTo(IEC.KIBI)
        assertThat(sum.getScaledValue()).isEquivalentAccordingToCompareTo(BigDecimal("3"))
    }

    @Test
    @DisplayName("Amount.minus converts the right operand and keeps the left unit")
    fun `Amount#minus converts the right operand and keeps the left unit`() {
        val difference = Amount.ofBytes(1) - Amount.ofBits(4)
        assertThat(difference.unit).isEqualTo(BinaryInformation.Byte)
        assertThat(difference.magnitude).isEquivalentAccordingToCompareTo(BigDecimal("0.5"))
        assertThat(difference).isEqualTo(Amount.ofNibbles(1))
    }

    @Test
    @DisplayName("Amount.times scales by a dimensionless factor")
    fun `Amount#times scales by a dimensionless factor`() {
        assertThat(Amount.ofBits(4) * 2).isEqualTo(Amount.ofBytes(1))
        assertThat(Amount.ofBytes(1) * 8).isEqualTo(Amount.ofBits(64))
    }

    @Test
    @DisplayName("Amount.div scales by a dimensionless divisor")
    fun `Amount#div scales by a dimensionless divisor`() {
        assertThat(Amount.ofBytes(1) / 2).isEqualTo(Amount.ofNibbles(1))
    }

    @Test
    @DisplayName("Amount.unaryMinus negates the amount and keeps the unit")
    fun `Amount#unaryMinus negates the amount and keeps the unit`() {
        assertThat(-Amount.ofBits(8)).isEqualTo(Amount.ofBytes(-1))
        assertThat((-Amount.ofBits(8)).unit).isEqualTo(BinaryInformation.Bit)
    }

    @Test
    @DisplayName("Amount.plus and Amount.minus throw for different dimensions")
    fun `Amount#plus and Amount#minus throw for different dimensions`() {
        assertThrows<IllegalArgumentException> { Amount.ofBytes(1) + Amount.ofSeconds(1) }
        assertThrows<IllegalArgumentException> { Amount.ofBytes(1) - Amount.ofSeconds(1) }
    }
}

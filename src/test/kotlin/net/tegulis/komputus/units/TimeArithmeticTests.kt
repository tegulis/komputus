package net.tegulis.komputus.units

import com.google.common.truth.Truth.assertThat
import java.math.BigDecimal
import net.tegulis.komputus.amount.Amount
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class TimeArithmeticTests {

    @Test
    @DisplayName("Amount.plus converts the right operand and keeps the left unit")
    fun `Amount#plus converts the right operand and keeps the left unit`() {
        val sum = Amount.ofMinutes(1) + Amount.ofSeconds(30)
        assertThat(sum.unit).isEqualTo(Time.Minute)
        assertThat(sum.magnitude).isEquivalentAccordingToCompareTo(BigDecimal("1.5"))
        assertThat(sum).isEqualTo(Amount.ofSeconds(90))
    }

    @Test
    @DisplayName("Amount.minus converts the right operand and keeps the left unit")
    fun `Amount#minus converts the right operand and keeps the left unit`() {
        val difference = Amount.ofHours(1) - Amount.ofMinutes(30)
        assertThat(difference.unit).isEqualTo(Time.Hour)
        assertThat(difference.magnitude).isEquivalentAccordingToCompareTo(BigDecimal("0.5"))
        assertThat(difference).isEqualTo(Amount.ofMinutes(30))
    }

    @Test
    @DisplayName("Amount.times scales by a dimensionless factor")
    fun `Amount#times scales by a dimensionless factor`() {
        assertThat(Amount.ofSeconds(45) * 2).isEqualTo(Amount.ofSeconds(90))
        assertThat(Amount.ofSeconds(45) * BigDecimal("0.5")).isEqualTo(Amount.ofSeconds(22.5))
    }

    @Test
    @DisplayName("Amount.div scales by a dimensionless divisor")
    fun `Amount#div scales by a dimensionless divisor`() {
        assertThat(Amount.ofSeconds(90) / 2).isEqualTo(Amount.ofSeconds(45))
        // Non-terminating divisions are rounded to Amount.defaultNonTerminatingPrecision (10) digits
        assertThat((Amount.ofSeconds(1) / 3).magnitude).isEquivalentAccordingToCompareTo(BigDecimal("0.3333333333"))
    }

    @Test
    @DisplayName("Amount.unaryMinus negates the amount and keeps the unit")
    fun `Amount#unaryMinus negates the amount and keeps the unit`() {
        assertThat(-Amount.ofSeconds(60)).isEqualTo(Amount.ofMinutes(-1))
        assertThat((-Amount.ofSeconds(60)).unit).isEqualTo(Time.Second)
    }

    @Test
    @DisplayName("Amount.plus and Amount.minus throw for different dimensions")
    fun `Amount#plus and Amount#minus throw for different dimensions`() {
        assertThrows<IllegalArgumentException> { Amount.ofSeconds(1) + Amount.ofBytes(1) }
        assertThrows<IllegalArgumentException> { Amount.ofSeconds(1) - Amount.ofBytes(1) }
    }
}

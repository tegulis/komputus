package net.tegulis.komputus.units

import com.google.common.truth.Truth.assertThat
import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.NumberFormat
import java.time.Duration
import java.time.LocalDate
import java.time.Period
import java.util.Locale
import net.tegulis.komputus.amount.Amount
import net.tegulis.komputus.prefixes.SI
import net.tegulis.komputus.toAmount
import net.tegulis.komputus.toJavaDuration
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class QuotientArithmeticTests {

    private fun usNumberFormat(): NumberFormat =
        DecimalFormat.getInstance(Locale.ROOT).apply { maximumFractionDigits = 2 }

    @Test
    fun `dividing 100 Mb by 3 hours creates a rate in megabits per hour`() {
        val rate = 100.Mb() / Amount.ofHours(3)
        assertThat(rate.unit).isEqualTo(QuotientUnit(BinaryInformation.Bit, Time.Hour))
        // 100000000 / 3 is non-terminating and is rounded to Amount.defaultNonTerminatingPrecision
        assertThat(rate.magnitude).isEqualToIgnoringScale(BigDecimal("33333333.33"))
        // The MEGA prefix is carried over because bits admit major prefixes
        assertThat(rate.prefix).isSameInstanceAs(SI.MEGA)
        assertThat(rate.format(usNumberFormat())).isEqualTo("33.33 Mb/h")
    }

    @Test
    fun `multiplying an exact rate by its time span returns the original amount`() {
        val rate = 100.Mb() / Amount.ofHours(2)
        assertThat(rate.magnitude).isEqualToIgnoringScale(BigDecimal("50000000"))
        assertThat(rate * Amount.ofHours(2)).isEqualTo(100.Mb())
    }

    @Test
    fun `a rounded rate makes the round trip approximate`() {
        val rate = 100.Mb() / Amount.ofHours(3)
        val roundTrip = rate * Amount.ofHours(3)
        assertThat(roundTrip.magnitude).isEqualToIgnoringScale(BigDecimal("99999999.99"))
        assertThat(roundTrip).isNotEqualTo(100.Mb())
    }

    @Test
    fun `a monthly quota with explicit 30-day months accumulates approximately over 3 months`() {
        // Komputus has no Month unit (months are not fixed lengths); a conventional month is spelled out explicitly
        val usage = 1.GiB() / Amount.ofDays(30)
        assertThat(usage.unit).isEqualTo(QuotientUnit(BinaryInformation.Byte, Time.Day))
        assertThat(usage.magnitude).isEqualToIgnoringScale(BigDecimal("35791394.13"))
        val total = usage * Amount.ofDays(90)
        // 1 GiB / 30 is non-terminating, so three months come back a whisker short of 3 GiB but display as it
        assertThat(total.magnitude).isEqualToIgnoringScale(BigDecimal("3221225471.7"))
        assertThat(total).isNotEqualTo(3.GiB())
        assertThat(total.format(usNumberFormat())).isEqualTo("3 GiB")
    }

    @Test
    fun `calendar months bridge through the Java Date and Time API`() {
        val start = LocalDate.of(2026, 1, 1).atStartOfDay()
        val january = Duration.between(start, start.plus(Period.ofMonths(1))).toAmount()
        val firstQuarter = Duration.between(start, start.plus(Period.ofMonths(3))).toAmount()
        val usage = 1.GiB() / january
        val total = usage * firstQuarter
        // January's rate over January-March 2026 (90 days) is honestly less than 3 GiB
        assertThat(total < 3.GiB()).isTrue()
        assertThat(total.format(usNumberFormat())).isEqualTo("2.9 GiB")
    }

    @Test
    fun `multiplying a rate by an amount of its denominator dimension cancels the quotient`() {
        val bandwidth = 100.Mb() / Amount.ofSeconds(1)
        val transferred = bandwidth * Amount.ofMinutes(2)
        assertThat(transferred.unit).isSameInstanceAs(BinaryInformation.Bit)
        assertThat(transferred).isEqualTo(12.Gb())
        assertThat(Amount.ofMinutes(2) * bandwidth).isEqualTo(transferred)
    }

    @Test
    fun `dividing an amount by a rate cancels to the denominator dimension`() {
        val bandwidth = 10.Mb() / Amount.ofSeconds(1)
        val transferTime = 1.GiB() / bandwidth
        assertThat(transferTime.unit).isSameInstanceAs(Time.Second)
        assertThat(transferTime).isEqualTo(Amount.ofSeconds(BigDecimal("858.9934592")))
        assertThat(transferTime.toJavaDuration()).isEqualTo(Duration.ofSeconds(858, 993_459_200))
        // The IEC.GIBI prefix of the left operand is not admitted by Second, so the result resets into the SI group -
        // which is what lets a sub-second transfer time align down to milliseconds
        assertThat(transferTime.prefix).isSameInstanceAs(SI.NONE)
        assertThat((1.MiB() / bandwidth).align().format(usNumberFormat())).isEqualTo("838.86 ms")
    }

    @Test
    fun `rates chain into nested quotients`() {
        val ramp = (Amount.ofBits(100) / Amount.ofSeconds(1)) / Amount.ofSeconds(10)
        assertThat(ramp.unit).isEqualTo(QuotientUnit(QuotientUnit(BinaryInformation.Bit, Time.Second), Time.Second))
        assertThat(ramp.unit.symbol).isEqualTo("(b/s)/s")
        assertThat(ramp.magnitude).isEqualToIgnoringScale(BigDecimal.TEN)
        val afterThirtySeconds = ramp * Amount.ofSeconds(30)
        assertThat(afterThirtySeconds).isEqualTo(Amount.ofBits(300) / Amount.ofSeconds(1))
    }

    @Test
    fun `adding rates converts the right operand to the left operand's unit`() {
        val left = 1.KiB() / Amount.ofSeconds(1)
        val right = Amount.ofBytes(61440) / Amount.ofMinutes(1)
        val sum = left + right
        assertThat(sum).isEqualTo(2.KiB() / Amount.ofSeconds(1))
        assertThat(sum.format(usNumberFormat())).isEqualTo("2 KiB/s")
    }

    @Test
    fun `multiplying without a cancellable quotient throws`() {
        assertThrows<IllegalArgumentException> { Amount.ofBytes(5) * Amount.ofSeconds(5) }
        val bandwidth = 10.Mb() / Amount.ofSeconds(1)
        assertThrows<IllegalArgumentException> { bandwidth * Amount.ofBytes(5) }
    }

    @Test
    fun `dividing by a zero amount throws`() {
        assertThrows<ArithmeticException> { 100.Mb() / Amount.ofHours(0) }
    }
}

package net.tegulis.komputus.amount

import com.google.common.truth.Truth.assertThat
import java.math.BigDecimal
import net.tegulis.komputus.prefixes.IEC
import net.tegulis.komputus.prefixes.NotScalingPrefix
import net.tegulis.komputus.prefixes.SI
import net.tegulis.komputus.prefixes.ShortScale
import net.tegulis.komputus.units.BinaryInformation
import net.tegulis.komputus.units.Currency
import net.tegulis.komputus.units.Time
import org.junit.jupiter.api.Test

/** Construction and copying: how the prefix is resolved when it is not given explicitly. */
class ConstructionTests {

    @Test
    fun `an omitted prefix falls back to the unit's default prefix`() {
        assertThat(Amount(1, unit = Time.Second).prefix).isSameInstanceAs(SI.NONE)
        assertThat(Amount(1, unit = Time.Minute).prefix).isSameInstanceAs(NotScalingPrefix)
        assertThat(Amount(1, unit = BinaryInformation.Bit).prefix).isSameInstanceAs(SI.NONE)
        assertThat(Amount(1, unit = BinaryInformation.Byte).prefix).isSameInstanceAs(IEC.NONE)
        assertThat(Amount(1, unit = Currency.MONEY).prefix).isSameInstanceAs(ShortScale.NONE)
        // Without a unit at all
        assertThat(Amount(1).prefix).isSameInstanceAs(NotScalingPrefix)
        // The BigDecimal constructor resolves the prefix the same way
        assertThat(Amount(BigDecimal.ONE, unit = BinaryInformation.Byte).prefix).isSameInstanceAs(IEC.NONE)
    }

    @Test
    fun `an explicit prefix is used and scales the value`() {
        val kilo = Amount(2, SI.KILO)
        assertThat(kilo.prefix).isSameInstanceAs(SI.KILO)
        assertThat(kilo.magnitude).isEqualToIgnoringScale(BigDecimal("2000"))
        val milliseconds = Amount(500, SI.MILLI, Time.Second)
        assertThat(milliseconds.prefix).isSameInstanceAs(SI.MILLI)
        assertThat(milliseconds.magnitude).isEqualToIgnoringScale(BigDecimal("0.5"))
    }

    @Test
    fun `raw stores the magnitude without scaling it with the prefix`() {
        val raw = Amount(500, SI.MILLI, Time.Second, raw = true)
        assertThat(raw.magnitude).isEqualToIgnoringScale(BigDecimal("500"))
        assertThat(raw.getScaledValue()).isEqualToIgnoringScale(BigDecimal("500000"))
    }

    @Test
    fun `copy keeps the prefix when the unit does not change`() {
        val twoKibibytes = Amount(2, IEC.KIBI, BinaryInformation.Byte)
        val copied = twoKibibytes.copy(magnitude = BigDecimal("4096"))
        assertThat(copied.prefix).isSameInstanceAs(IEC.KIBI)
        assertThat(copied.unit).isSameInstanceAs(BinaryInformation.Byte)
        assertThat(copied.magnitude).isEqualToIgnoringScale(BigDecimal("4096"))
    }

    @Test
    fun `copy carries the prefix over to a new unit that admits it`() {
        // Bits admit major prefixes, so KIBI survives the unit change
        val copied = Amount(2, IEC.KIBI, BinaryInformation.Byte).copy(unit = BinaryInformation.Bit)
        assertThat(copied.unit).isSameInstanceAs(BinaryInformation.Bit)
        assertThat(copied.prefix).isSameInstanceAs(IEC.KIBI)
    }

    @Test
    fun `copy resets the prefix to the new unit's default when it is not admitted`() {
        // Seconds admit only SI prefixes that scale below one, so KIBI is replaced by Second's default prefix
        val copied = Amount(2, IEC.KIBI, BinaryInformation.Byte).copy(unit = Time.Second)
        assertThat(copied.unit).isSameInstanceAs(Time.Second)
        assertThat(copied.prefix).isSameInstanceAs(SI.NONE)
    }

    @Test
    fun `an explicit prefix overrides the carry-over policy`() {
        val copied = Amount(2, IEC.KIBI, BinaryInformation.Byte).copy(prefix = IEC.KIBI, unit = Time.Second)
        assertThat(copied.prefix).isSameInstanceAs(IEC.KIBI)
    }
}

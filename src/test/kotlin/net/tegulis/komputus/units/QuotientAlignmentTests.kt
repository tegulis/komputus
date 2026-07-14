package net.tegulis.komputus.units

import com.google.common.truth.Truth.assertThat
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.Locale
import net.tegulis.komputus.amount.Amount
import net.tegulis.komputus.prefixes.IEC
import net.tegulis.komputus.prefixes.SI
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class QuotientAlignmentTests {

    private fun usNumberFormat(): NumberFormat =
        DecimalFormat.getInstance(Locale.US).apply { maximumFractionDigits = 2 }

    @Test
    fun `alignment finds the fitting prefix within the quotient unit`() {
        val aligned = (Amount.ofBits(16000) / Amount.ofSeconds(1)).align()
        assertThat(aligned.unit).isEqualTo(QuotientUnit(BinaryInformation.Bit, Time.Second))
        assertThat(aligned.prefix).isSameInstanceAs(SI.KILO)
        assertThat(aligned.format(usNumberFormat())).isEqualTo("16 kb/s")
    }

    @Test
    fun `alignment uses the prefix group of the amount`() {
        val aligned = (Amount.ofBytes(2048) / Amount.ofSeconds(1)).align()
        assertThat(aligned.prefix).isSameInstanceAs(IEC.KIBI)
        assertThat(aligned.format(usNumberFormat())).isEqualTo("2 KiB/s")
    }

    @Test
    fun `alignment does not switch quotient units`() {
        val aligned = (Amount.ofBytes(7200) / Amount.ofHours(1)).align()
        assertThat(aligned.unit).isEqualTo(QuotientUnit(BinaryInformation.Byte, Time.Hour))
        assertThat(aligned.prefix).isSameInstanceAs(IEC.KIBI)
        assertThat(aligned.format(usNumberFormat())).isEqualTo("7.03 KiB/h")
    }

    @Test
    fun `QuotientDimension#align rejects amounts of other dimensions`() {
        assertThrows<IllegalArgumentException> { QuotientDimension(BinaryInformation, Time).align(Amount.ofBytes(5)) }
    }
}

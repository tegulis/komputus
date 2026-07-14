package net.tegulis.komputus.units

import com.google.common.truth.Truth.assertThat
import java.text.DecimalFormat
import java.util.Locale
import net.tegulis.komputus.amount.Amount
import org.junit.jupiter.api.Test

class BinaryFormattingTests {
    @Test
    fun `aligned binary amounts format naturally`() {
        val numberFormat = DecimalFormat.getInstance(Locale.US).apply { maximumFractionDigits = 2 }
        assertThat(Amount.ofBytes(2048).align().format(numberFormat)).isEqualTo("2 KiB")
        assertThat(Amount.ofBits(2000).align().format(numberFormat)).isEqualTo("2 kb")
    }
}

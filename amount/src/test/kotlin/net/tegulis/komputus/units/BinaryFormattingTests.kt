package net.tegulis.komputus.units

import com.google.common.truth.Truth.assertThat
import net.tegulis.komputus.amount.Amount
import org.junit.jupiter.api.Test

class BinaryFormattingTests {
    @Test
    fun `aligned binary amounts format naturally`() {
        assertThat(Amount.ofBytes(2048).align().format()).isEqualTo("2 KiB")
        assertThat(Amount.ofBits(2000).align().format()).isEqualTo("2 kb")
    }
}

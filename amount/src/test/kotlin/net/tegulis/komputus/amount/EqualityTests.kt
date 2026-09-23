package net.tegulis.komputus.amount

import com.google.common.truth.Truth.assertThat
import net.tegulis.komputus.prefixes.LongScale
import net.tegulis.komputus.prefixes.ShortScale
import net.tegulis.komputus.units.Time
import net.tegulis.komputus.units.hours
import net.tegulis.komputus.units.seconds
import org.junit.Test

class EqualityTests {

    @Test
    @Suppress("ReplaceCallWithBinaryOperator")
    fun `equals() is reflexive`() {
        assertThat(Amount(1).equals(Amount(1))).isTrue()
        assertThat(Amount(1) == Amount(1)).isTrue()
        assertThat(1.hours().equals(1.hours())).isTrue()
        assertThat(1.hours() == 1.hours()).isTrue()
    }

    @Suppress("ReplaceCallWithBinaryOperator")
    @Test
    fun `equals() is symmetric`() {
        assertThat(1.hours().equals(60.seconds())).isTrue()
        assertThat(1.hours() == 60.seconds()).isTrue()
    }

    @Test
    fun `equals() is transitive`() {
        assertThat(1.hours() == 60.seconds()).isTrue()
        assertThat(60.seconds() == 3600.seconds()).isTrue()
        assertThat(1.hours() == 3600.seconds()).isTrue()
    }

    @Test
    fun `equals() is consistent`() {
        val a = Amount(1, ShortScale.THOUSAND, Time.Second)
        val b = Amount(1, LongScale.THOUSAND, Time.Second)
        assertThat(a == b).isTrue()
        a.align()
        assertThat(a == b).isTrue()
        b.alignPrefix()
        assertThat(a == b).isTrue()
    }

    @Suppress("SENSELESS_COMPARISON")
    @Test
    fun `equals() is null safe`() {
        assertThat(Amount(1) == null).isFalse()
        assertThat(null == Amount(1)).isFalse()
    }

    @Test
    fun `equals() is hash consistent`() {
        assertThat(Amount(1) == Amount(1)).isTrue()
        assertThat(Amount(1).hashCode() == Amount(1).hashCode()).isTrue()
        assertThat(1.hours() == 60.seconds()).isTrue()
        assertThat(1.hours().hashCode() == 60.seconds().hashCode()).isTrue()
    }

    @Test
    fun `compareTo() is order consistent`() {
        assertThat(1.hours() == 60.seconds()).isTrue()
        assertThat(1.hours().compareTo(60.seconds())).isEqualTo(0)
    }
}

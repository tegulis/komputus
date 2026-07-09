package net.tegulis.komputus.amount

import com.google.common.truth.Truth.assertThat
import java.math.BigDecimal
import net.tegulis.komputus.prefixes.Prefix
import net.tegulis.komputus.prefixes.SI
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

/**
 * Tests for prefix alignment without unit conventions. See [net.tegulis.komputus.units.TimeAlignmentTests] and
 * [net.tegulis.komputus.units.BinaryAlignmentTests].
 */
class AlignmentTests {

    @Test
    @DisplayName("Amount.alignPrefix finds the largest prefix that keeps the scaled value at least one")
    fun `Amount#alignPrefix finds the largest prefix that keeps the scaled value at least one`() {
        val aligned = Amount(1500, SI.NONE).alignPrefix()
        assertThat(aligned.prefix).isEqualTo(SI.KILO)
        assertThat(aligned.getScaledValue()).isEquivalentAccordingToCompareTo(BigDecimal("1.5"))
        val small = Amount(BigDecimal("0.005"), SI.NONE).alignPrefix()
        assertThat(small.prefix).isEqualTo(SI.MILLI)
        assertThat(small.getScaledValue()).isEquivalentAccordingToCompareTo(BigDecimal("5"))
        val negative = Amount(-1500, SI.NONE).alignPrefix()
        assertThat(negative.prefix).isEqualTo(SI.KILO)
        assertThat(negative.getScaledValue()).isEquivalentAccordingToCompareTo(BigDecimal("-1.5"))
    }

    @Test
    @DisplayName("Amount.alignPrefix carries the magnitude over verbatim")
    fun `Amount#alignPrefix carries the magnitude over verbatim`() {
        val amount = Amount(1500, SI.NONE)
        assertThat(amount.alignPrefix().magnitude).isSameInstanceAs(amount.magnitude)
    }

    @Test
    @DisplayName("Amount.alignPrefix returns the same instance when already aligned or when no prefix fits")
    fun `Amount#alignPrefix returns the same instance when already aligned or when no prefix fits`() {
        val alreadyAligned = Amount(1.5, SI.KILO)
        assertThat(alreadyAligned.alignPrefix()).isSameInstanceAs(alreadyAligned)
        // NotScalingPrefixGroup has no prefix that scales 0.5 above one
        val noFit = Amount(0.5)
        assertThat(noFit.alignPrefix()).isSameInstanceAs(noFit)
    }

    @Test
    @DisplayName("Amount.alignPrefix aligns zero to the group's no-scaling prefix")
    fun `Amount#alignPrefix aligns zero to the group's no-scaling prefix`() {
        val zero = Amount(0, SI.KILO)
        assertThat(zero.alignPrefix().prefix).isEqualTo(SI.NONE)
        val alreadyNone = Amount(0, SI.NONE)
        assertThat(alreadyNone.alignPrefix()).isSameInstanceAs(alreadyNone)
    }

    @Test
    @DisplayName("Amount.alignPrefix accepts a custom prefix filter")
    fun `Amount#alignPrefix accepts a custom prefix filter`() {
        val amount = Amount(150, SI.NONE)
        assertThat(amount.alignPrefix().prefix).isEqualTo(SI.HECTO)
        assertThat(amount.alignPrefix(Prefix.majorPrefixFilter).prefix).isEqualTo(SI.NONE)
    }
}

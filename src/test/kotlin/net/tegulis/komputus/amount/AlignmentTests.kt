package net.tegulis.komputus.amount

import com.google.common.truth.Truth.assertThat
import java.math.BigDecimal
import net.tegulis.komputus.prefixes.IEC
import net.tegulis.komputus.prefixes.Prefix
import net.tegulis.komputus.prefixes.SI
import net.tegulis.komputus.units.NoUnit
import org.junit.jupiter.api.Test

/**
 * Tests for prefix alignment without unit conventions. See unit specific alignment tests in
 * [net.tegulis.komputus.units].
 */
class AlignmentTests {

    @Test
    fun `Amount#alignPrefix carries the magnitude over verbatim`() {
        val amount = Amount(1500, prefix = SI.NONE)
        assertThat(amount.alignPrefix().magnitude).isSameInstanceAs(amount.magnitude)
    }

    @Test
    fun `Amount#alignPrefix returns the same instance when already aligned or when no prefix fits`() {
        val alreadyAligned = Amount(1.5, prefix = SI.KILO)
        assertThat(alreadyAligned.alignPrefix()).isSameInstanceAs(alreadyAligned)
        // NotScalingPrefixGroup has no prefix that scales 0.5 above one
        val noFit = Amount(0.5, unit = NoUnit)
        assertThat(noFit.alignPrefix()).isSameInstanceAs(noFit)
    }

    @Test
    fun `Amount#alignPrefix aligns zero to the group's no-scaling prefix`() {
        val zeroSI = Amount(0, prefix = SI.KILO)
        assertThat(zeroSI.alignPrefix().prefix).isEqualTo(SI.defaultNotScalingPrefix)
        val alreadyNoneSI = Amount(0, prefix = SI.NONE)
        assertThat(alreadyNoneSI.alignPrefix()).isSameInstanceAs(alreadyNoneSI)
        val zeroIEC = Amount(0, prefix = IEC.KIBI)
        assertThat(zeroIEC.alignPrefix().prefix).isEqualTo(IEC.defaultNotScalingPrefix)
        val alreadyNoneIEC = Amount(0, prefix = IEC.NONE)
        assertThat(alreadyNoneIEC.alignPrefix()).isSameInstanceAs(alreadyNoneIEC)
    }

    @Test
    fun `Amount#alignPrefix finds the largest prefix that keeps the scaled value at least one`() {
        val alignedSI = Amount(1500, prefix = SI.NONE).alignPrefix()
        assertThat(alignedSI.prefix).isEqualTo(SI.KILO)
        assertThat(alignedSI.getScaledValue()).isEquivalentAccordingToCompareTo(BigDecimal("1.5"))
        val alignedIEC = Amount(1536, prefix = IEC.NONE).alignPrefix()
        assertThat(alignedIEC.prefix).isEqualTo(IEC.KIBI)
        assertThat(alignedIEC.getScaledValue()).isEquivalentAccordingToCompareTo(BigDecimal("1.5"))
        val smallSI = Amount(BigDecimal("0.005"), SI.NONE).alignPrefix()
        assertThat(smallSI.prefix).isEqualTo(SI.MILLI)
        assertThat(smallSI.getScaledValue()).isEquivalentAccordingToCompareTo(BigDecimal("5"))
        val negativeSI = Amount(-1500, prefix = SI.NONE).alignPrefix()
        assertThat(negativeSI.prefix).isEqualTo(SI.KILO)
        assertThat(negativeSI.getScaledValue()).isEquivalentAccordingToCompareTo(BigDecimal("-1.5"))
        val negativeIEC = Amount(-1536, prefix = IEC.NONE).alignPrefix()
        assertThat(negativeIEC.prefix).isEqualTo(IEC.KIBI)
        assertThat(negativeIEC.getScaledValue()).isEquivalentAccordingToCompareTo(BigDecimal("-1.5"))
    }

    @Test
    fun `Amount#alignPrefix accepts a custom prefix filter`() {
        val amount = Amount(150, prefix = SI.NONE)
        assertThat(amount.alignPrefix().prefix).isEqualTo(SI.HECTO)
        assertThat(amount.alignPrefix(Prefix.majorPrefixFilter).prefix).isEqualTo(SI.NONE)
    }
}

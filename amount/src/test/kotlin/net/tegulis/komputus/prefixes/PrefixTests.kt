package net.tegulis.komputus.prefixes

import com.google.common.truth.Truth.assertThat
import com.google.common.truth.Truth.assertWithMessage
import java.math.BigDecimal
import kotlin.math.abs
import net.tegulis.komputus.toBigDecimalWithMathContext
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

class PrefixGroupTests {

    @ParameterizedTest(name = "{0}")
    @MethodSource(Parameters.PREFIX_GROUP_PROVIDER)
    fun `PrefixGroup has at least one prefix`(prefixGroup: PrefixGroup) {
        assertThat(prefixGroup.prefixes).isNotEmpty()
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource(Parameters.PREFIX_GROUP_PROVIDER)
    fun `PrefixGroup#prefixes is ordered by power`(prefixGroup: PrefixGroup) {
        assertThat(prefixGroup.prefixes).containsExactlyElementsIn(prefixGroup.prefixes.sortedBy { it.power }).inOrder()
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource(Parameters.PREFIX_GROUP_PROVIDER)
    fun `PrefixGroup#prefixes contains PrefixGroup#noScalePrefix`(prefixGroup: PrefixGroup) {
        assertThat(prefixGroup.prefixes).contains(prefixGroup.defaultNotScalingPrefix)
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource(Parameters.PREFIX_GROUP_PROVIDER)
    fun `prefix#prefixGroup == PrefixGroup for prefix in PrefixGroup#prefixes`(prefixGroup: PrefixGroup) {
        prefixGroup.prefixes.forEach { prefix ->
            assertWithMessage("${prefix}.prefixGroup != $prefixGroup").that(prefix.prefixGroup).isEqualTo(prefixGroup)
        }
    }

    @Test
    fun `all SI prefixes are defined`() {
        assertThat(SI.prefixes.map { it.power }).containsExactlyElementsIn((-30..-3 step 3) + (-2..2) + (3..30 step 3))
    }

    @Test
    fun `all IEC prefixes are defined`() {
        assertThat(IEC.prefixes.map { it.power }).containsExactlyElementsIn(0..10)
    }

    @Test
    fun `all ShortScale prefixes are defined`() {
        // Nothing below one, so money never scales down
        assertThat(ShortScale.prefixes.map { it.power }).containsExactlyElementsIn(0..27 step 3)
        assertThat(ShortScale.prefixes.map { it.symbol })
            .containsExactly(
                "",
                "thousand",
                "million",
                "billion",
                "trillion",
                "quadrillion",
                "quintillion",
                "sextillion",
                "septillion",
                "octillion",
            )
            .inOrder()
    }

    @Test
    fun `all LongScale prefixes are defined`() {
        assertThat(LongScale.prefixes.map { it.power }).containsExactlyElementsIn(0..27 step 3)
        assertThat(LongScale.prefixes.map { it.symbol })
            .containsExactly(
                "",
                "thousand",
                "million",
                "milliard",
                "billion",
                "billiard",
                "trillion",
                "trilliard",
                "quadrillion",
                "quadrilliard",
            )
            .inOrder()
    }

    @Test
    fun `the scales agree up to a million and diverge above it`() {
        assertThat(LongScale.MILLION.value).isEquivalentAccordingToCompareTo(ShortScale.MILLION.value)
        // A long scale billion is a thousand times a short scale billion - the reason they are separate groups
        assertThat(LongScale.BILLION.value)
            .isEquivalentAccordingToCompareTo(ShortScale.BILLION.value.multiply(BigDecimal("1000")))
        // 10^9 is a billion in the short scale, but a milliard in the long scale
        assertThat(LongScale.MILLIARD.value).isEquivalentAccordingToCompareTo(ShortScale.BILLION.value)
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource(Parameters.PREFIX_PROVIDER)
    fun `Prefix#value == Prefix#prefixGroup#magnitude ^ Prefix#power`(prefix: Prefix) {
        assertThat(prefix.value)
            .isEquivalentAccordingToCompareTo(
                Prefix.valueFromPowerOfMultiplier(prefix.prefixGroup.multiplier, prefix.power)
            )
        if (prefix.power < 0) {
            assertThat(prefix.value)
                .isEquivalentAccordingToCompareTo(
                    BigDecimal.ONE.divide(
                        prefix.prefixGroup.multiplier.toBigDecimalWithMathContext().pow(abs(prefix.power))
                    )
                )
        } else {
            assertThat(prefix.value)
                .isEquivalentAccordingToCompareTo(
                    prefix.prefixGroup.multiplier.toBigDecimalWithMathContext().pow(prefix.power)
                )
        }
    }
}

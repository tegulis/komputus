package net.tegulis.komputus.prefixes

import com.google.common.truth.Truth.assertThat
import com.google.common.truth.Truth.assertWithMessage
import java.math.BigDecimal
import kotlin.math.abs
import net.tegulis.komputus.toBigDecimalWithMathContext
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

class PrefixGroupTests {

    @ParameterizedTest(name = "{0}")
    @MethodSource(Parameters.PREFIX_GROUP_PROVIDER)
    @DisplayName("PrefixGroup has at least one prefix")
    fun `PrefixGroup has at least one prefix`(prefixGroup: PrefixGroup) {
        assertThat(prefixGroup.prefixes).isNotEmpty()
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource(Parameters.PREFIX_GROUP_PROVIDER)
    @DisplayName("PrefixGroup.prefixes is ordered by power")
    fun `PrefixGroup#prefixes is ordered by power`(prefixGroup: PrefixGroup) {
        assertThat(prefixGroup.prefixes).containsExactlyElementsIn(prefixGroup.prefixes.sortedBy { it.power })
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource(Parameters.PREFIX_GROUP_PROVIDER)
    @DisplayName("PrefixGroup.prefixes contains PrefixGroup.noScalePrefix")
    fun `PrefixGroup#prefixes contains PrefixGroup#noScalePrefix`(prefixGroup: PrefixGroup) {
        assertThat(prefixGroup.prefixes).contains(prefixGroup.noScalingPrefix)
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource(Parameters.PREFIX_GROUP_PROVIDER)
    @DisplayName("prefix.prefixGroup == PrefixGroup for prefix in PrefixGroup.prefixes")
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

    @ParameterizedTest(name = "{0}")
    @MethodSource(Parameters.PREFIX_PROVIDER)
    @DisplayName("Prefix.value == Prefix.prefixGroup.magnitude ^ Prefix.power")
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

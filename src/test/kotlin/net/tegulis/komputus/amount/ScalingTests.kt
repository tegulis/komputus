package net.tegulis.komputus.amount

import com.google.common.truth.Truth.assertThat
import java.math.BigDecimal
import net.tegulis.komputus.prefixes.IEC
import net.tegulis.komputus.prefixes.NoScalingPrefix
import net.tegulis.komputus.prefixes.Prefix
import net.tegulis.komputus.prefixes.SI
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

class ScalingTests {

    @ParameterizedTest(name = "{0}")
    @MethodSource("prefixesWithScale")
    @DisplayName("Amount#magnitude is calculated correctly from Prefix")
    fun `Amount#magnitude is calculated correctly from Prefix`(prefix: Prefix, expectedMagnitude: BigDecimal) {
        val value = BigDecimal("1")
        assertThat(Amount(value, prefix).magnitude).isEquivalentAccordingToCompareTo(expectedMagnitude)
    }

    companion object {
        @JvmStatic
        fun prefixesWithScale(): List<Arguments> =
            listOf(
                // NoScalingPrefix
                Arguments.of(NoScalingPrefix, BigDecimal("1")),
                // IEC prefixes
                Arguments.of(IEC.NONE, BigDecimal("1")),
                Arguments.of(IEC.KIBI, BigDecimal("1024")),
                Arguments.of(IEC.MEBI, BigDecimal("1048576")),
                Arguments.of(IEC.GIBI, BigDecimal("1073741824")),
                Arguments.of(IEC.TEBI, BigDecimal("1099511627776")),
                Arguments.of(IEC.PEBI, BigDecimal("1125899906842624")),
                Arguments.of(IEC.EXBI, BigDecimal("1152921504606846976")),
                Arguments.of(IEC.ZEBI, BigDecimal("1180591620717411303424")),
                Arguments.of(IEC.YOBI, BigDecimal("1208925819614629174706176")),
                // SI prefixes
                Arguments.of(SI.QUETTA, BigDecimal("1E30")),
                Arguments.of(SI.RONNA, BigDecimal("1E27")),
                Arguments.of(SI.YOTTA, BigDecimal("1E24")),
                Arguments.of(SI.ZETTA, BigDecimal("1E21")),
                Arguments.of(SI.EXA, BigDecimal("1E18")),
                Arguments.of(SI.PETA, BigDecimal("1E15")),
                Arguments.of(SI.TERA, BigDecimal("1E12")),
                Arguments.of(SI.GIGA, BigDecimal("1E9")),
                Arguments.of(SI.MEGA, BigDecimal("1E6")),
                Arguments.of(SI.KILO, BigDecimal("1E3")),
                Arguments.of(SI.HECTO, BigDecimal("1E2")),
                Arguments.of(SI.DECA, BigDecimal("1E1")),
                Arguments.of(SI.NONE, BigDecimal("1")),
                Arguments.of(SI.DECI, BigDecimal("1E-1")),
                Arguments.of(SI.CENTI, BigDecimal("1E-2")),
                Arguments.of(SI.MILLI, BigDecimal("1E-3")),
                Arguments.of(SI.MICRO, BigDecimal("1E-6")),
                Arguments.of(SI.NANO, BigDecimal("1E-9")),
                Arguments.of(SI.PICO, BigDecimal("1E-12")),
                Arguments.of(SI.FEMTO, BigDecimal("1E-15")),
                Arguments.of(SI.ATTO, BigDecimal("1E-18")),
                Arguments.of(SI.ZEPTO, BigDecimal("1E-21")),
                Arguments.of(SI.YOCTO, BigDecimal("1E-24")),
                Arguments.of(SI.RONTO, BigDecimal("1E-27")),
                Arguments.of(SI.QUECTO, BigDecimal("1E-30")),
            )
    }
}

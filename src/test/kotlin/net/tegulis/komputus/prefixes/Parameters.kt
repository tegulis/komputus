package net.tegulis.komputus.prefixes

import com.google.common.truth.Truth.assertThat
import java.math.BigDecimal
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.provider.Arguments

object Parameters {
    const val PREFIX_GROUP_PROVIDER = "net.tegulis.komputus.prefixes.Parameters#prefixGroupProvider"

    @JvmStatic fun prefixGroupProvider(): List<PrefixGroup> = listOf(NotScalingPrefixGroup, SI, IEC)

    @Test
    fun `prefixGroupProvider() provides all prefix groups`() {
        assertThat(prefixGroupProvider()).containsExactly(NotScalingPrefixGroup, SI, IEC)
    }

    const val PREFIX_PROVIDER = "net.tegulis.komputus.prefixes.Parameters#prefixProvider"

    @JvmStatic fun prefixProvider(): List<Prefix> = NotScalingPrefixGroup.prefixes + SI.prefixes + IEC.prefixes

    @Test
    fun `prefixProvider() provides all prefixes`() {
        assertThat(prefixProvider())
            .containsExactlyElementsIn(NotScalingPrefixGroup.prefixes + SI.prefixes + IEC.prefixes)
    }

    const val PREFIX_AND_THEIR_SCALE_PROVIDER = "net.tegulis.komputus.prefixes.Parameters#prefixesAndTheirScales"

    @JvmStatic
    fun prefixesAndTheirScales(): List<Arguments> =
        listOf(
            // NotScalingPrefix
            Arguments.of(NotScalingPrefix, BigDecimal("1")),
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
            Arguments.of(IEC.ROBI, BigDecimal("1237940039285380274899124224")),
            Arguments.of(IEC.QUEBI, BigDecimal("1267650600228229401496703205376")),
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

    @Test
    fun `prefixesWithTheirScale() provides all prefixes`() {
        assertThat(prefixesAndTheirScales().map { it.get()[0] })
            .containsExactlyElementsIn(NotScalingPrefixGroup.prefixes + SI.prefixes + IEC.prefixes)
    }
}

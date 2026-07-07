package net.tegulis.komputus.prefixes

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test

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
}

package net.tegulis.komputus.prefixes

import com.google.common.truth.Truth
import java.math.BigDecimal
import net.tegulis.komputus.amount.Amount
import net.tegulis.komputus.prefixes.Parameters.PREFIX_AND_THEIR_SCALE_PROVIDER
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

class ScalingTests {

    @ParameterizedTest(name = "{0}")
    @MethodSource(PREFIX_AND_THEIR_SCALE_PROVIDER)
    fun `Amount#magnitude is calculated correctly from Prefix`(prefix: Prefix, expectedMagnitude: BigDecimal) {
        val bigDecimalValue = BigDecimal("1")
        val byteValue = 1.toByte()
        val intValue = 1
        val longValue = 1L
        val floatValue = 1f
        val doubleValue = 1.0
        listOf(bigDecimalValue, byteValue, intValue, longValue, floatValue, doubleValue).forEach { number ->
            Truth.assertThat(Amount(number, prefix = prefix).magnitude)
                .isEquivalentAccordingToCompareTo(expectedMagnitude)
            if (prefix.power == 0) {
                Truth.assertThat(Amount(number).magnitude).isEquivalentAccordingToCompareTo(expectedMagnitude)
            }
        }
    }
}

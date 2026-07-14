package net.tegulis.komputus.units

import com.google.common.truth.Truth.assertThat
import com.google.common.truth.Truth.assertWithMessage
import net.tegulis.komputus.prefixes.IEC
import net.tegulis.komputus.prefixes.NotScalingPrefix
import net.tegulis.komputus.prefixes.SI
import net.tegulis.komputus.prefixes.ShortScale
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

/** Invariants that every [UnitOfMeasurement] must uphold, across all dimensions. */
class UnitOfMeasurementTests {

    @ParameterizedTest(name = "{0}")
    @MethodSource("allUnits")
    fun `every unit's defaultPrefix is admitted by its own prefixFilter`(name: String, unit: UnitOfMeasurement) {
        // Otherwise a prefix reset (Amount.copy) would land on a prefix the unit itself rejects, and alignPrefix could
        // not find any candidate to scale with.
        assertWithMessage("$name: defaultPrefix ${unit.defaultPrefix} is rejected by its own prefixFilter")
            .that(unit.prefixFilter(unit.defaultPrefix))
            .isTrue()
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("allUnits")
    fun `every unit's defaultPrefix group contains a prefix the unit admits`(name: String, unit: UnitOfMeasurement) {
        // The group an amount resets into must offer at least one usable prefix, or alignment would be a dead end.
        assertWithMessage("$name: no prefix of ${unit.defaultPrefix.prefixGroup} is admitted by its prefixFilter")
            .that(unit.defaultPrefix.prefixGroup.prefixes.any(unit.prefixFilter))
            .isTrue()
    }

    @Test
    fun `units declare the customary defaultPrefix`() {
        // Time: seconds scale with SI, the larger units never scale
        assertThat(Time.Second.defaultPrefix).isSameInstanceAs(SI.NONE)
        assertThat(Time.Minute.defaultPrefix).isSameInstanceAs(NotScalingPrefix)
        assertThat(Time.Week.defaultPrefix).isSameInstanceAs(NotScalingPrefix)
        // Binary information: bits and nibbles use SI (networking), octets and bytes use IEC (storage)
        assertThat(BinaryInformation.Bit.defaultPrefix).isSameInstanceAs(SI.NONE)
        assertThat(BinaryInformation.Nibble.defaultPrefix).isSameInstanceAs(SI.NONE)
        assertThat(BinaryInformation.Octet.defaultPrefix).isSameInstanceAs(IEC.NONE)
        assertThat(BinaryInformation.Byte.defaultPrefix).isSameInstanceAs(IEC.NONE)
        // Money scales with the currency scale (thousand, million, billion, trillion)
        assertThat(Currency.MONEY.defaultPrefix).isSameInstanceAs(ShortScale.NONE)
        assertThat(Currency.EUR.defaultPrefix).isSameInstanceAs(ShortScale.NONE)
        // Dimensionless
        assertThat(NoUnit.defaultPrefix).isSameInstanceAs(NotScalingPrefix)
    }

    @Test
    fun `a quotient unit takes the numerator's defaultPrefix`() {
        assertThat(QuotientUnit(BinaryInformation.Bit, Time.Second).defaultPrefix).isSameInstanceAs(SI.NONE)
        assertThat(QuotientUnit(BinaryInformation.Byte, Time.Hour).defaultPrefix).isSameInstanceAs(IEC.NONE)
        assertThat(QuotientUnit(Time.Second, BinaryInformation.Byte).defaultPrefix).isSameInstanceAs(SI.NONE)
        assertThat(QuotientUnit(Currency.EUR, BinaryInformation.Byte).defaultPrefix).isSameInstanceAs(ShortScale.NONE)
        // Nested: a price per byte per day still follows the outermost numerator, which is a quotient itself
        val pricePerBytePerDay = QuotientUnit(QuotientUnit(Currency.EUR, BinaryInformation.Byte), Time.Day)
        assertThat(pricePerBytePerDay.defaultPrefix).isSameInstanceAs(ShortScale.NONE)
    }

    companion object {
        @JvmStatic
        fun allUnits(): List<Arguments> {
            val declaredUnits = listOf(Time, BinaryInformation, Currency, NoDimension).flatMap { it.units }
            val quotientUnits =
                listOf(
                    QuotientUnit(BinaryInformation.Bit, Time.Second),
                    QuotientUnit(BinaryInformation.Byte, Time.Hour),
                    QuotientUnit(Time.Second, BinaryInformation.Byte),
                    QuotientUnit(Currency.EUR, BinaryInformation.Byte),
                    QuotientUnit(QuotientUnit(Currency.EUR, BinaryInformation.Byte), Time.Day),
                )
            return (declaredUnits + quotientUnits).map { Arguments.of(it.name, it) }
        }
    }
}

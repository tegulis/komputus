package net.tegulis.komputus

import com.google.common.truth.Truth.assertThat
import java.math.BigDecimal
import java.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import net.tegulis.komputus.amount.Amount
import net.tegulis.komputus.prefixes.SI
import net.tegulis.komputus.units.Time
import net.tegulis.komputus.units.ofBytes
import net.tegulis.komputus.units.ofMinutes
import net.tegulis.komputus.units.ofSeconds
import net.tegulis.komputus.units.ofWeeks
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

class DurationTests {

    @Test
    @DisplayName("Duration.getTotalSeconds includes fractions")
    fun `Duration#getTotalSeconds includes fractions`() {
        assertThat(Duration.ofSeconds(1, 500_000_000).getTotalSeconds()).isEqualToIgnoringScale(BigDecimal("1.5"))
        assertThat(Duration.ofMillis(-500).getTotalSeconds()).isEqualToIgnoringScale(BigDecimal("-0.5"))
    }

    @ParameterizedTest(name = "{0} == {1}")
    @MethodSource("equivalentDurationsAndAmounts")
    @DisplayName("Java durations convert to equal amounts of seconds")
    fun `java durations convert to equal amounts of seconds`(duration: Duration, amount: Amount) {
        val converted = duration.toAmount()
        assertThat(converted).isEqualTo(amount)
        assertThat(converted.unit).isSameInstanceAs(Time.Second)
        assertThat(converted.prefix).isSameInstanceAs(SI.NONE)
    }

    @ParameterizedTest(name = "{1} == {0}")
    @MethodSource("equivalentDurationsAndAmounts")
    @DisplayName("Time amounts convert to equal java durations")
    fun `time amounts convert to equal java durations`(duration: Duration, amount: Amount) {
        assertThat(amount.toJavaDuration()).isEqualTo(duration)
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("roundTripDurations")
    @DisplayName("Java durations survive the round trip through Amount")
    fun `java durations survive the round trip through Amount`(duration: Duration) {
        assertThat(duration.toAmount().toJavaDuration()).isEqualTo(duration)
    }

    @Test
    @DisplayName("Amounts with prefixes convert to java durations")
    fun `amounts with prefixes convert to java durations`() {
        assertThat(Amount.ofSeconds(500, SI.MILLI).toJavaDuration()).isEqualTo(Duration.ofMillis(500))
        assertThat(Amount.ofSeconds(2, SI.KILO).toJavaDuration()).isEqualTo(Duration.ofSeconds(2000))
    }

    @Test
    @DisplayName("Fractions below one nanosecond are rounded with the default rounding mode")
    fun `fractions below one nanosecond are rounded with the default rounding mode`() {
        // Default rounding mode is HALF_DOWN
        assertThat(Amount.ofSeconds(BigDecimal("0.0000000015")).toJavaDuration()).isEqualTo(Duration.ofNanos(1))
        assertThat(Amount.ofSeconds(BigDecimal("0.0000000004")).toJavaDuration()).isEqualTo(Duration.ZERO)
        // Rounding may carry into the next second
        assertThat(Amount.ofSeconds(BigDecimal("0.99999999995")).toJavaDuration()).isEqualTo(Duration.ofSeconds(1))
    }

    @Test
    @DisplayName("Amount.toJavaDuration throws for other dimensions")
    fun `Amount#toJavaDuration throws for other dimensions`() {
        assertThrows<IllegalArgumentException> { Amount.ofBytes(5).toJavaDuration() }
        assertThrows<IllegalArgumentException> { Amount(5).toJavaDuration() }
        assertThrows<IllegalArgumentException> { Amount.ofBytes(5).toKotlinDuration() }
    }

    @Test
    @DisplayName("Amount.toJavaDuration throws when the seconds overflow a Long")
    fun `Amount#toJavaDuration throws when the seconds overflow a Long`() {
        assertThrows<ArithmeticException> { Amount.ofSeconds(BigDecimal("1E+20")).toJavaDuration() }
    }

    @Test
    @DisplayName("Kotlin durations convert to equal amounts of seconds")
    fun `kotlin durations convert to equal amounts of seconds`() {
        assertThat(90.seconds.toAmount()).isEqualTo(Amount.ofMinutes(1.5))
        assertThat(1500.milliseconds.toAmount()).isEqualTo(Amount.ofSeconds(1.5))
        assertThat((-30).minutes.toAmount()).isEqualTo(Amount.ofMinutes(-30))
    }

    @Test
    @DisplayName("Time amounts convert to equal kotlin durations")
    fun `time amounts convert to equal kotlin durations`() {
        assertThat(Amount.ofMinutes(1.5).toKotlinDuration()).isEqualTo(90.seconds)
        assertThat(Amount.ofWeeks(1).toKotlinDuration()).isEqualTo(7.days)
        assertThat(Amount.ofSeconds(-0.5).toKotlinDuration()).isEqualTo((-500).milliseconds)
    }

    @Test
    @DisplayName("Infinite kotlin durations cannot convert to an Amount")
    fun `infinite kotlin durations cannot convert to an Amount`() {
        assertThrows<IllegalArgumentException> { kotlin.time.Duration.INFINITE.toAmount() }
        assertThrows<IllegalArgumentException> { (-kotlin.time.Duration.INFINITE).toAmount() }
    }

    companion object {
        @JvmStatic
        fun equivalentDurationsAndAmounts(): List<Arguments> =
            listOf(
                Arguments.of(Duration.ofSeconds(90), Amount.ofMinutes(1.5)),
                Arguments.of(Duration.ofMillis(1500), Amount.ofSeconds(1.5)),
                Arguments.of(Duration.ofDays(7), Amount.ofWeeks(1)),
                Arguments.of(Duration.ZERO, Amount.ofSeconds(0)),
                Arguments.of(Duration.ofMillis(-500), Amount.ofSeconds(-0.5)),
                Arguments.of(Duration.ofNanos(1), Amount.ofSeconds(BigDecimal("0.000000001"))),
            )

        @JvmStatic
        fun roundTripDurations(): List<Arguments> =
            listOf(
                Arguments.of(Duration.ofSeconds(90)),
                Arguments.of(Duration.ofSeconds(1, 999_999_999)),
                Arguments.of(Duration.ofNanos(1)),
                Arguments.of(Duration.ofMillis(-1500)),
                Arguments.of(Duration.ZERO),
            )
    }
}

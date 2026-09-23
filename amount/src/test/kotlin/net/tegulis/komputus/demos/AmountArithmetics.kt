package net.tegulis.komputus.demos

import com.google.common.truth.Truth.assertThat
import net.tegulis.komputus.amount.FormattingTests.Companion.assertFormatting
import net.tegulis.komputus.amount.formatter.Alignment
import net.tegulis.komputus.amount.formatter.AmountFormatter
import net.tegulis.komputus.units.GB
import net.tegulis.komputus.units.hours
import net.tegulis.komputus.units.minutes
import net.tegulis.komputus.units.seconds
import org.junit.jupiter.api.Test

/** This demo became a test class to make sure the arithmetics work as expected. */
class AmountArithmetics {

    val noAlignmentFormatter = AmountFormatter.DEFAULT.copy(alignment = Alignment.NONE)

    /**
     * When adding and subtracting amounts of the *same* dimension, the right side is converted to the **left** side's
     * unit first.
     *
     * NOTE: a not-aligning formatter is used, as the [net.tegulis.komputus.amount.Amount.defaultFormatter] aligns both
     * prefix and unit.
     */
    @Test
    fun addingAndSubtracting() {
        assertThat((30.minutes() + 1.hours()).format(noAlignmentFormatter)).isEqualTo("90 min")
        listOf(
                // the right side is converted to hours
                (1.hours() + 30.minutes()) to "1.5 h",
                // the left side is minutes
                (30.minutes() + 1.hours()) to "90 min",
                (1.hours() - 30.minutes()) to "0.5 h",
                (30.minutes() - 1.hours()) to "-30 min",
            )
            .forEach { assertFormatting(it, formatter = noAlignmentFormatter) }
    }

    /** Scaling by a dimensionless number keeps the unit and prefix. */
    @Test
    fun scaling() {
        listOf((90.seconds() * 2) to "180 s", (90.seconds() / 2) to "45 s", ((-90).seconds()) to "-90 s").forEach {
            assertFormatting(it, noAlignmentFormatter)
        }
    }

    /** Mixing dimensions is a mistake, and it is caught: adding storage to time throws. */
    @Test
    fun `mixing dimensions`() {
        runCatching { 1.hours() + 1.GB() }.onFailure { println("1 h + 1 GB throws: ${it.message}") }
    }
}

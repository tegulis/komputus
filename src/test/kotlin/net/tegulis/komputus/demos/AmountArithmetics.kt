package net.tegulis.komputus.demos

import java.text.DecimalFormat
import java.util.Locale
import net.tegulis.komputus.units.GB
import net.tegulis.komputus.units.hours
import net.tegulis.komputus.units.minutes
import net.tegulis.komputus.units.seconds

fun main() {
    // A fixed number format so the printed output matches the comments regardless of the default locale.
    val format = DecimalFormat.getInstance(Locale.US).apply { maximumFractionDigits = 2 }

    // Adding and subtracting amounts of the SAME dimension.
    // The right side is converted to the LEFT side's unit first, so the left side decides the result unit and prefix.
    println((1.hours() + 30.minutes()).format(format)) // 1.5 h  - the right side is converted to hours
    println((30.minutes() + 1.hours()).format(format)) // 90 min - the left side is minutes this time
    println((1.hours() - 30.minutes()).format(format)) // 0.5 h
    println((30.minutes() - 1.hours()).format(format)) // -30 min

    // Scaling by a dimensionless number keeps the unit and prefix.
    println((90.seconds() * 2).format(format)) // 180 s
    println((90.seconds() / 2).format(format)) // 45 s
    println(((-90).seconds()).format(format)) //    -90 s

    // Mixing dimensions is a mistake, and it is caught: adding storage to time throws.
    runCatching { 1.hours() + 1.GB() }
        .onFailure { println("1 h + 1 GB throws: ${it.message}") }
}

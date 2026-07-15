package net.tegulis.komputus.demos

import java.math.BigDecimal
import java.text.DecimalFormat
import java.util.Locale
import net.tegulis.komputus.units.Currency.EUR
import net.tegulis.komputus.units.GB
import net.tegulis.komputus.units.GiB
import net.tegulis.komputus.units.Mb
import net.tegulis.komputus.units.days
import net.tegulis.komputus.units.div
import net.tegulis.komputus.units.hours
import net.tegulis.komputus.units.money
import net.tegulis.komputus.units.seconds
import net.tegulis.komputus.units.times

fun main() {
    // A fixed number format so the printed output matches the comments regardless of the default locale.
    val format = DecimalFormat.getInstance(Locale.US).apply { maximumFractionDigits = 2 }

    // 1. Dividing two amounts of DIFFERENT units builds a rate: a quotient unit that scales with its numerator.
    val bandwidth = 500.Mb() / 1.seconds()
    println(bandwidth.format(format)) // 500 Mb/s

    // The denominator does not have to be time. Any two units divide.
    val perHour = 40.money(EUR) / 1.hours()
    println(perHour.format(format)) // 40 EUR/h

    // 2. Multiplying a rate by an amount of its DENOMINATOR dimension cancels the quotient (in either order).
    val threeHourCost = perHour * 3.hours()
    println(threeHourCost.format(format)) // 120 EUR

    // 3. Dividing an amount by a rate whose NUMERATOR matches its dimension cancels the other way, leaving the
    //    denominator dimension: bytes over (bits per second) is a time.
    val transferTime = 250.GiB() / bandwidth
    println(transferTime.align().format(format)) // 1.19 h

    // 4. When nothing cancels, the result is just a structural rate - here bits per hour.
    val overThreeHours = 100.Mb() / 3.hours()
    println(overThreeHours.format(format)) // 33.33 Mb/h

    // 5. A denominator that is itself a product is written by currying. A bucket price per gigabyte per month is
    //    money per byte per day: a nested quotient (¤/B)/d.
    val price = BigDecimal("0.023").money() / 1.GB() / 30.days()
    println(price.unit.symbol) // (¤/B)/d
}

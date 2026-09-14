package net.tegulis.komputus.demos

import java.math.BigDecimal
import java.time.Duration
import java.time.LocalDate
import java.time.Period
import net.tegulis.komputus.toAmount
import net.tegulis.komputus.toJavaDuration
import net.tegulis.komputus.units.GB
import net.tegulis.komputus.units.GiB
import net.tegulis.komputus.units.Mb
import net.tegulis.komputus.units.days
import net.tegulis.komputus.units.div
import net.tegulis.komputus.units.money
import net.tegulis.komputus.units.seconds
import net.tegulis.komputus.units.times

fun main(args: Array<String>) {
    // Nightly backups are uploaded to a cloud bucket
    val nightlyBackup = 250.GiB()
    // Networking convention: SI bits per second
    val uplink = 500.Mb() / 1.seconds()

    // 1. How long does one upload take? Dividing an amount by a rate cancels to the rate's denominator dimension.
    val transferTime = nightlyBackup / uplink
    println("Uploading ${nightlyBackup.align()} at ${uplink.align()} takes ${transferTime.align()}")
    println("As a java.time.Duration: ${transferTime.toJavaDuration()}")

    // 2. How much traffic do nightly backups generate over a month?
    // Multiplying a rate by an amount of its denominator dimension cancels the quotient.
    val dailyTraffic = nightlyBackup / 1.days()
    val conventionalMonth = dailyTraffic * 30.days()
    println("Nightly backups generate ${conventionalMonth.align()} per 30-day month")

    // Komputus has no month unit (months are not fixed lengths) - calendar spans bridge through java.time
    val start = LocalDate.of(2026, 1, 1)
    val january = Duration.between(start.atStartOfDay(), start.plus(Period.ofMonths(1)).atStartOfDay()).toAmount()
    val januaryTraffic = dailyTraffic * january
    println("In January they generate ${januaryTraffic.align()} (31 nights)")

    // 3. What does storing a month of backups cost? Bucket pricing is per decimal gigabyte per month:
    // a curried quotient (¤/GB)/month with the 30-day month spelled out explicitly.
    val price = BigDecimal("0.023").money() / 1.GB() / 30.days()
    // Cancel step by step: times a month of days leaves ¤/B, times the stored bytes leaves ¤
    val monthlyCost = price * 30.days() * conventionalMonth
    println("Storing ${conventionalMonth.align()} costs $monthlyCost per month")
}

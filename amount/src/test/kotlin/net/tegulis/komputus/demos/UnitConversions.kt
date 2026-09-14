package net.tegulis.komputus.demos

import java.text.DecimalFormat
import java.util.Locale
import net.tegulis.komputus.amount.Amount
import net.tegulis.komputus.prefixes.IEC
import net.tegulis.komputus.prefixes.SI
import net.tegulis.komputus.units.BinaryInformation
import net.tegulis.komputus.units.Currency.EUR
import net.tegulis.komputus.units.Currency.USD
import net.tegulis.komputus.units.Time
import net.tegulis.komputus.units.minutes
import net.tegulis.komputus.units.money
import net.tegulis.komputus.units.ofBytes
import net.tegulis.komputus.units.ofSeconds
import net.tegulis.komputus.units.seconds

fun main() {
    // A fixed number format so the printed output matches the comments regardless of the default locale.
    val format = DecimalFormat.getInstance(Locale.ROOT).apply { maximumFractionDigits = 2 }

    // 1. convertTo changes the unit within a dimension, going through the dimension's base unit.
    println(Amount.ofSeconds(90).convertTo(Time.Minute).format(format)) // 1.5 min
    println(Amount.ofBytes(1000).convertTo(BinaryInformation.Bit).format(format)) // 8,000 b

    // align() also converts, but it CHOOSES the unit a human would write.
    println(Amount.ofSeconds(3600).align().format(format)) // 1 h

    // 2. Amounts compare by their value in the base unit, so different units compare correctly.
    println(60.seconds() == 1.minutes()) // true - 60 s and 1 min are the same amount
    println(90.seconds() > 1.minutes()) //  true - 90 s is more than 60 s

    // A marketing "1 TB" disk is smaller than a real "1 TiB" one (10^12 bytes vs 2^40 bytes).
    println(Amount.ofBytes(1, SI.TERA) < Amount.ofBytes(1, IEC.TEBI)) // true

    // 3. Across dimensions, equals() never throws - it is simply false. compareTo() does throw.
    println(5.money() == Amount.ofSeconds(5)) // false
    runCatching { 5.money() < Amount.ofSeconds(5) }.onFailure { println("5 ¤ < 5 s throws: ${it.message}") }

    // 4. Currencies convert too, but only with the identity for now: there are no exchange rates yet, so every
    //    currency is treated as worth exactly one unit of money. 5 dollars therefore "equals" 5 euros.
    println(5.money(USD).convertTo(EUR).format(format)) // 5 EUR
    println(5.money(USD) == 5.money(EUR)) // true (for now) - see the exchange-rate note
}

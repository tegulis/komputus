package net.tegulis.komputus.demos

import net.tegulis.komputus.amount.Amount
import net.tegulis.komputus.amount.formatter.Alignment
import net.tegulis.komputus.amount.formatter.AmountFormatter
import net.tegulis.komputus.amount.formatter.UnitNotation
import net.tegulis.komputus.prefixes.IEC
import net.tegulis.komputus.prefixes.SI
import net.tegulis.komputus.units.BinaryInformation
import net.tegulis.komputus.units.Mb
import net.tegulis.komputus.units.div
import net.tegulis.komputus.units.ofBytes
import net.tegulis.komputus.units.seconds

fun main() {
    // A formatter that keeps a few fraction digits so forced-down prefixes stay visible. Formatters are immutable:
    // copy() derives a variant and leaves the original untouched.
    val precise =
        AmountFormatter.DEFAULT.copy(
            numberFormatProvider = { AmountFormatter.DEFAULT.getNumberFormat().apply { maximumFractionDigits = 3 } }
        )

    // 1. Forcing the display PREFIX
    // An amount formats with the prefix it currently carries. A formatter with a prefix overrides that for display
    // only: the value is unchanged, and - unlike align() - no prefixFilter applies, so any prefix is honoured, even one
    // from a different prefix group.
    val disk = Amount.ofBytes(1, IEC.TEBI)
    println("Default:              ${disk.format(precise)}") //                            1 TiB
    println("Forced IEC GiB:       ${disk.format(precise.copy(forcedPrefix = IEC.GIBI))}") // 1,024 GiB
    println("Forced SI GB:         ${disk.format(precise.copy(forcedPrefix = SI.GIGA))}") //  1,099.512 GB
    println("Forced SI TB:         ${disk.format(precise.copy(forcedPrefix = SI.TERA))}") //  1.1 TB

    // 2. Forcing the display UNIT
    // Changing the unit changes the numeric value, so it is a real conversion (convertTo), not just presentation.
    // Combine convertTo for the unit with a prefix override for the prefix. 1000 bytes shown as bits:
    val kilobits = Amount.ofBytes(1000).convertTo(BinaryInformation.Bit)
    println("1000 B in bits:       ${kilobits.format(precise)}") //                            8,000 b
    println("1000 B as kilobits:   ${kilobits.format(precise.copy(forcedPrefix = SI.KILO))}") // 8 kb
    println("1000 B as megabits:   ${kilobits.format(precise.copy(forcedPrefix = SI.MEGA))}") // 0.008 Mb

    // 3. The override works on rates too (it scales the numerator)
    val bandwidth = 100.Mb() / 1.seconds()
    println("Bandwidth:            ${bandwidth.format(precise)}") //                           100 Mb/s
    println("Bandwidth in Gb/s:    ${bandwidth.format(precise.copy(forcedPrefix = SI.GIGA))}") // 0.1 Gb/s

    // 4. Contrast with alignment, which CHOOSES a fitting prefix (within the unit's own prefix group). The formatter
    //    can align for display, leaving the amount as it is.
    val advertised = Amount.ofBytes(1, SI.TERA) // a marketing "1 TB" disk
    val aligned = precise.copy(alignment = Alignment.UNIT_AND_PREFIX)
    println("Advertised aligned:   ${advertised.format(aligned)}") //                       1 TB
    println("Advertised in TiB:    ${advertised.format(precise.copy(forcedPrefix = IEC.TEBI))}") // 0.909 TiB

    // 5. Names instead of symbols
    println("Written out:          ${disk.format(precise.copy(unitNotation = UnitNotation.NAMES))}") // 1 tebibyte
}

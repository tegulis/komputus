package net.tegulis.komputus.units

import java.math.BigDecimal
import net.tegulis.komputus.amount.Amount
import net.tegulis.komputus.divideWithMathContext
import net.tegulis.komputus.multiplyWithMathContext
import net.tegulis.komputus.prefixes.IEC
import net.tegulis.komputus.prefixes.Prefix
import net.tegulis.komputus.prefixes.SI

/**
 * Units of information - or in other words, binary units.
 *
 * Only bits, nibbles, and octets/bytes are provided. Neither [Octet] nor [Byte] is a typealias to provide two different
 * unit symbols.
 * > The word is a size that varies by and has a special importance for a particular hardware context. On modern
 * > hardware, a word is typically 2, 4 or 8 bytes, but the size varies dramatically on older hardware.
 *
 * See: https://en.wikipedia.org/wiki/Units_of_information
 * - All units align to major prefixes by default.
 * - Convenience functions for bits use SI prefix by default - as used in networking
 * - Convenience functions for nibbles use SI prefix by default.
 * - Convenience functions for octets and bytes use IEC prefix by default - as used when measuring (but not when
 *   advertising!) storage.
 *
 * Alignment does not switch units in this dimension ([Dimension.align] keeps its prefix-only default): whether an
 * amount of information is displayed in bits or bytes is the caller's choice, not a question of magnitude.
 *
 * @see UnitOfMeasurement.prefixFilter
 * @see Prefix.Companion.majorPrefixFilter
 * @see Prefix.isMajor
 */
object BinaryInformation : Dimension {
    override val units: List<BinaryUnit> by lazy { listOf(Bit, Nibble, Octet, Byte) }
    override val baseUnit: UnitOfMeasurement
        get() = Byte

    sealed class BinaryUnit(
        override val name: String,
        override val pluralName: String,
        override val symbol: String,
        unitsInAByte: Int = 1,
        override val defaultPrefix: Prefix = IEC.NONE,
    ) : UnitOfMeasurement {
        override val dimension: Dimension
            get() = BinaryInformation

        override val prefixFilter: (Prefix) -> Boolean = Prefix.majorPrefixFilter
        override val toBase: (BigDecimal) -> BigDecimal = { it.divideWithMathContext(unitsInAByte) }
        override val fromBase: (BigDecimal) -> BigDecimal = { it.multiplyWithMathContext(unitsInAByte) }
    }

    const val BITS_IN_AN_OCTET = 8
    const val NIBBLES_IN_AN_OCTET = 2

    // Bits and nibbles default to SI prefixes (networking); octets and bytes to IEC prefixes (storage).
    object Bit : BinaryUnit("bit", "bits", "b", BITS_IN_AN_OCTET, SI.NONE)

    object Nibble : BinaryUnit("nibble", "nibbles", "", NIBBLES_IN_AN_OCTET, SI.NONE)

    /** See: https://en.wikipedia.org/wiki/Octet_(computing) */
    object Octet : BinaryUnit("octet", "octets", "o")

    object Byte : BinaryUnit("byte", "bytes", "B")
}

//
// Convenience functions for creating time amounts
//

fun Amount.Companion.ofBits(bits: Number, prefix: Prefix = SI.defaultNotScalingPrefix): Amount =
    BinaryInformation.Bit.amountOf(bits, prefix)

fun Amount.Companion.ofBits(bits: BigDecimal, prefix: Prefix = SI.defaultNotScalingPrefix): Amount =
    BinaryInformation.Bit.amountOf(bits, prefix)

fun Amount.Companion.ofNibbles(nibbles: Number, prefix: Prefix = SI.defaultNotScalingPrefix): Amount =
    BinaryInformation.Nibble.amountOf(nibbles, prefix)

fun Amount.Companion.ofNibbles(nibbles: BigDecimal, prefix: Prefix = SI.defaultNotScalingPrefix): Amount =
    BinaryInformation.Nibble.amountOf(nibbles, prefix)

fun Amount.Companion.ofOctets(octets: Number, prefix: Prefix = IEC.defaultNotScalingPrefix): Amount =
    BinaryInformation.Octet.amountOf(octets, prefix)

fun Amount.Companion.ofOctets(octets: BigDecimal, prefix: Prefix = IEC.defaultNotScalingPrefix): Amount =
    BinaryInformation.Octet.amountOf(octets, prefix)

fun Amount.Companion.ofBytes(bytes: Number, prefix: Prefix = IEC.defaultNotScalingPrefix): Amount =
    BinaryInformation.Byte.amountOf(bytes, prefix)

fun Amount.Companion.ofBytes(bytes: BigDecimal, prefix: Prefix = IEC.defaultNotScalingPrefix): Amount =
    BinaryInformation.Byte.amountOf(bytes, prefix)

//
//  Convenience functions to create SI bits from Numbers
//

// TODO: Generate this code with something like JavaPoet (for Kotlin)

fun Number.toSiBits() = Amount.ofBits(this, SI.defaultNotScalingPrefix)

fun BigDecimal.toSiBits() = Amount.ofBits(this, SI.defaultNotScalingPrefix)

fun Number.Qb() = Amount.ofBits(this, SI.QUETTA)

fun BigDecimal.Qb() = Amount.ofBits(this, SI.QUETTA)

fun Number.Rb() = Amount.ofBits(this, SI.RONNA)

fun BigDecimal.Rb() = Amount.ofBits(this, SI.RONNA)

fun Number.Yb() = Amount.ofBits(this, SI.YOTTA)

fun BigDecimal.Yb() = Amount.ofBits(this, SI.YOTTA)

fun Number.Zb() = Amount.ofBits(this, SI.ZETTA)

fun BigDecimal.Zb() = Amount.ofBits(this, SI.ZETTA)

fun Number.Eb() = Amount.ofBits(this, SI.EXA)

fun BigDecimal.Eb() = Amount.ofBits(this, SI.EXA)

fun Number.Pb() = Amount.ofBits(this, SI.PETA)

fun BigDecimal.Pb() = Amount.ofBits(this, SI.PETA)

fun Number.Tb() = Amount.ofBits(this, SI.TERA)

fun BigDecimal.Tb() = Amount.ofBits(this, SI.TERA)

fun Number.Gb() = Amount.ofBits(this, SI.GIGA)

fun BigDecimal.Gb() = Amount.ofBits(this, SI.GIGA)

fun Number.Mb() = Amount.ofBits(this, SI.MEGA)

fun BigDecimal.Mb() = Amount.ofBits(this, SI.MEGA)

fun Number.kb() = Amount.ofBits(this, SI.KILO)

fun BigDecimal.kb() = Amount.ofBits(this, SI.KILO)

//
//  Convenience functions to create IEC bits from Numbers
//
fun Number.toIecBits() = Amount.ofBits(this, IEC.defaultNotScalingPrefix)

fun BigDecimal.toIecBits() = Amount.ofBits(this, IEC.defaultNotScalingPrefix)

fun Number.Kib() = Amount.ofBits(this, IEC.KIBI)

fun BigDecimal.Kib() = Amount.ofBits(this, IEC.KIBI)

fun Number.Mib() = Amount.ofBits(this, IEC.MEBI)

fun BigDecimal.Mib() = Amount.ofBits(this, IEC.MEBI)

fun Number.Gib() = Amount.ofBits(this, IEC.GIBI)

fun BigDecimal.Gib() = Amount.ofBits(this, IEC.GIBI)

fun Number.Tib() = Amount.ofBits(this, IEC.TEBI)

fun BigDecimal.Tib() = Amount.ofBits(this, IEC.TEBI)

fun Number.Pib() = Amount.ofBits(this, IEC.PEBI)

fun BigDecimal.Pib() = Amount.ofBits(this, IEC.PEBI)

fun Number.Eib() = Amount.ofBits(this, IEC.EXBI)

fun BigDecimal.Eib() = Amount.ofBits(this, IEC.EXBI)

fun Number.Zib() = Amount.ofBits(this, IEC.ZEBI)

fun BigDecimal.Zib() = Amount.ofBits(this, IEC.ZEBI)

fun Number.Yib() = Amount.ofBits(this, IEC.YOBI)

fun BigDecimal.Yib() = Amount.ofBits(this, IEC.YOBI)

//
//  Convenience functions to create SI bytes from Numbers
//
fun Number.toSiBytes() = Amount.ofBytes(this, SI.defaultNotScalingPrefix)

fun BigDecimal.toSiBytes() = Amount.ofBytes(this, SI.defaultNotScalingPrefix)

fun Number.QB() = Amount.ofBytes(this, SI.QUETTA)

fun BigDecimal.QB() = Amount.ofBytes(this, SI.QUETTA)

fun Number.RB() = Amount.ofBytes(this, SI.RONNA)

fun BigDecimal.RB() = Amount.ofBytes(this, SI.RONNA)

fun Number.YB() = Amount.ofBytes(this, SI.YOTTA)

fun BigDecimal.YB() = Amount.ofBytes(this, SI.YOTTA)

fun Number.ZB() = Amount.ofBytes(this, SI.ZETTA)

fun BigDecimal.ZB() = Amount.ofBytes(this, SI.ZETTA)

fun Number.EB() = Amount.ofBytes(this, SI.EXA)

fun BigDecimal.EB() = Amount.ofBytes(this, SI.EXA)

fun Number.PB() = Amount.ofBytes(this, SI.PETA)

fun BigDecimal.PB() = Amount.ofBytes(this, SI.PETA)

fun Number.TB() = Amount.ofBytes(this, SI.TERA)

fun BigDecimal.TB() = Amount.ofBytes(this, SI.TERA)

fun Number.GB() = Amount.ofBytes(this, SI.GIGA)

fun BigDecimal.GB() = Amount.ofBytes(this, SI.GIGA)

fun Number.MB() = Amount.ofBytes(this, SI.MEGA)

fun BigDecimal.MB() = Amount.ofBytes(this, SI.MEGA)

fun Number.kB() = Amount.ofBytes(this, SI.KILO)

fun BigDecimal.kB() = Amount.ofBytes(this, SI.KILO)

fun Number.hB() = Amount.ofBytes(this, SI.HECTO)

fun BigDecimal.hB() = Amount.ofBytes(this, SI.HECTO)

fun Number.daB() = Amount.ofBytes(this, SI.DECA)

fun BigDecimal.daB() = Amount.ofBytes(this, SI.DECA)

//
//  Convenience functions to create IEC bytes from Numbers
//
fun Number.toIecBytes() = Amount.ofBytes(this, IEC.defaultNotScalingPrefix)

fun BigDecimal.toIecBytes() = Amount.ofBytes(this, IEC.defaultNotScalingPrefix)

fun Number.KiB() = Amount.ofBytes(this, IEC.KIBI)

fun BigDecimal.KiB() = Amount.ofBytes(this, IEC.KIBI)

fun Number.MiB() = Amount.ofBytes(this, IEC.MEBI)

fun BigDecimal.MiB() = Amount.ofBytes(this, IEC.MEBI)

fun Number.GiB() = Amount.ofBytes(this, IEC.GIBI)

fun BigDecimal.GiB() = Amount.ofBytes(this, IEC.GIBI)

fun Number.TiB() = Amount.ofBytes(this, IEC.TEBI)

fun BigDecimal.TiB() = Amount.ofBytes(this, IEC.TEBI)

fun Number.PiB() = Amount.ofBytes(this, IEC.PEBI)

fun BigDecimal.PiB() = Amount.ofBytes(this, IEC.PEBI)

fun Number.EiB() = Amount.ofBytes(this, IEC.EXBI)

fun BigDecimal.EiB() = Amount.ofBytes(this, IEC.EXBI)

fun Number.ZiB() = Amount.ofBytes(this, IEC.ZEBI)

fun BigDecimal.ZiB() = Amount.ofBytes(this, IEC.ZEBI)

fun Number.YiB() = Amount.ofBytes(this, IEC.YOBI)

fun BigDecimal.YiB() = Amount.ofBytes(this, IEC.YOBI)

package net.tegulis.komputus.units

import java.math.BigDecimal
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
 * @see Prefix.Companion.isMajorPrefixFilter
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

        override val prefixFilter: (Prefix) -> Boolean = Prefix.isMajorPrefixFilter
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

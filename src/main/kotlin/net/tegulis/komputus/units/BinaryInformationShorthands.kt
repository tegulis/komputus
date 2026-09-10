// Generated file - DO NOT edit by hand!
package net.tegulis.komputus.units

import java.math.BigDecimal
import kotlin.Number
import net.tegulis.komputus.amount.Amount
import net.tegulis.komputus.prefixes.IEC
import net.tegulis.komputus.prefixes.Prefix
import net.tegulis.komputus.prefixes.SI

fun Amount.Companion.ofBits(bits: Number, prefix: Prefix = SI.defaultNotScalingPrefix): Amount = BinaryInformation.Bit.amountOf(bits, prefix)

fun Amount.Companion.ofBits(bits: BigDecimal, prefix: Prefix = SI.defaultNotScalingPrefix): Amount = BinaryInformation.Bit.amountOf(bits, prefix)

fun Amount.Companion.ofNibbles(nibbles: Number, prefix: Prefix = SI.defaultNotScalingPrefix): Amount = BinaryInformation.Nibble.amountOf(nibbles, prefix)

fun Amount.Companion.ofNibbles(nibbles: BigDecimal, prefix: Prefix = SI.defaultNotScalingPrefix): Amount = BinaryInformation.Nibble.amountOf(nibbles, prefix)

fun Amount.Companion.ofOctets(octets: Number, prefix: Prefix = IEC.defaultNotScalingPrefix): Amount = BinaryInformation.Octet.amountOf(octets, prefix)

fun Amount.Companion.ofOctets(octets: BigDecimal, prefix: Prefix = IEC.defaultNotScalingPrefix): Amount = BinaryInformation.Octet.amountOf(octets, prefix)

fun Amount.Companion.ofBytes(bytes: Number, prefix: Prefix = IEC.defaultNotScalingPrefix): Amount = BinaryInformation.Byte.amountOf(bytes, prefix)

fun Amount.Companion.ofBytes(bytes: BigDecimal, prefix: Prefix = IEC.defaultNotScalingPrefix): Amount = BinaryInformation.Byte.amountOf(bytes, prefix)

fun Number.toSiBits(): Amount = Amount.ofBits(this, SI.defaultNotScalingPrefix)

fun BigDecimal.toSiBits(): Amount = Amount.ofBits(this, SI.defaultNotScalingPrefix)

fun Number.kb(): Amount = Amount.ofBits(this, SI.KILO)

fun BigDecimal.kb(): Amount = Amount.ofBits(this, SI.KILO)

fun Number.Mb(): Amount = Amount.ofBits(this, SI.MEGA)

fun BigDecimal.Mb(): Amount = Amount.ofBits(this, SI.MEGA)

fun Number.Gb(): Amount = Amount.ofBits(this, SI.GIGA)

fun BigDecimal.Gb(): Amount = Amount.ofBits(this, SI.GIGA)

fun Number.Tb(): Amount = Amount.ofBits(this, SI.TERA)

fun BigDecimal.Tb(): Amount = Amount.ofBits(this, SI.TERA)

fun Number.Pb(): Amount = Amount.ofBits(this, SI.PETA)

fun BigDecimal.Pb(): Amount = Amount.ofBits(this, SI.PETA)

fun Number.Eb(): Amount = Amount.ofBits(this, SI.EXA)

fun BigDecimal.Eb(): Amount = Amount.ofBits(this, SI.EXA)

fun Number.Zb(): Amount = Amount.ofBits(this, SI.ZETTA)

fun BigDecimal.Zb(): Amount = Amount.ofBits(this, SI.ZETTA)

fun Number.Yb(): Amount = Amount.ofBits(this, SI.YOTTA)

fun BigDecimal.Yb(): Amount = Amount.ofBits(this, SI.YOTTA)

fun Number.Rb(): Amount = Amount.ofBits(this, SI.RONNA)

fun BigDecimal.Rb(): Amount = Amount.ofBits(this, SI.RONNA)

fun Number.Qb(): Amount = Amount.ofBits(this, SI.QUETTA)

fun BigDecimal.Qb(): Amount = Amount.ofBits(this, SI.QUETTA)

fun Number.toIecBits(): Amount = Amount.ofBits(this, IEC.defaultNotScalingPrefix)

fun BigDecimal.toIecBits(): Amount = Amount.ofBits(this, IEC.defaultNotScalingPrefix)

fun Number.Kib(): Amount = Amount.ofBits(this, IEC.KIBI)

fun BigDecimal.Kib(): Amount = Amount.ofBits(this, IEC.KIBI)

fun Number.Mib(): Amount = Amount.ofBits(this, IEC.MEBI)

fun BigDecimal.Mib(): Amount = Amount.ofBits(this, IEC.MEBI)

fun Number.Gib(): Amount = Amount.ofBits(this, IEC.GIBI)

fun BigDecimal.Gib(): Amount = Amount.ofBits(this, IEC.GIBI)

fun Number.Tib(): Amount = Amount.ofBits(this, IEC.TEBI)

fun BigDecimal.Tib(): Amount = Amount.ofBits(this, IEC.TEBI)

fun Number.Pib(): Amount = Amount.ofBits(this, IEC.PEBI)

fun BigDecimal.Pib(): Amount = Amount.ofBits(this, IEC.PEBI)

fun Number.Eib(): Amount = Amount.ofBits(this, IEC.EXBI)

fun BigDecimal.Eib(): Amount = Amount.ofBits(this, IEC.EXBI)

fun Number.Zib(): Amount = Amount.ofBits(this, IEC.ZEBI)

fun BigDecimal.Zib(): Amount = Amount.ofBits(this, IEC.ZEBI)

fun Number.Yib(): Amount = Amount.ofBits(this, IEC.YOBI)

fun BigDecimal.Yib(): Amount = Amount.ofBits(this, IEC.YOBI)

fun Number.Rib(): Amount = Amount.ofBits(this, IEC.ROBI)

fun BigDecimal.Rib(): Amount = Amount.ofBits(this, IEC.ROBI)

fun Number.Qib(): Amount = Amount.ofBits(this, IEC.QUEBI)

fun BigDecimal.Qib(): Amount = Amount.ofBits(this, IEC.QUEBI)

fun Number.toSiBytes(): Amount = Amount.ofBytes(this, SI.defaultNotScalingPrefix)

fun BigDecimal.toSiBytes(): Amount = Amount.ofBytes(this, SI.defaultNotScalingPrefix)

fun Number.kB(): Amount = Amount.ofBytes(this, SI.KILO)

fun BigDecimal.kB(): Amount = Amount.ofBytes(this, SI.KILO)

fun Number.MB(): Amount = Amount.ofBytes(this, SI.MEGA)

fun BigDecimal.MB(): Amount = Amount.ofBytes(this, SI.MEGA)

fun Number.GB(): Amount = Amount.ofBytes(this, SI.GIGA)

fun BigDecimal.GB(): Amount = Amount.ofBytes(this, SI.GIGA)

fun Number.TB(): Amount = Amount.ofBytes(this, SI.TERA)

fun BigDecimal.TB(): Amount = Amount.ofBytes(this, SI.TERA)

fun Number.PB(): Amount = Amount.ofBytes(this, SI.PETA)

fun BigDecimal.PB(): Amount = Amount.ofBytes(this, SI.PETA)

fun Number.EB(): Amount = Amount.ofBytes(this, SI.EXA)

fun BigDecimal.EB(): Amount = Amount.ofBytes(this, SI.EXA)

fun Number.ZB(): Amount = Amount.ofBytes(this, SI.ZETTA)

fun BigDecimal.ZB(): Amount = Amount.ofBytes(this, SI.ZETTA)

fun Number.YB(): Amount = Amount.ofBytes(this, SI.YOTTA)

fun BigDecimal.YB(): Amount = Amount.ofBytes(this, SI.YOTTA)

fun Number.RB(): Amount = Amount.ofBytes(this, SI.RONNA)

fun BigDecimal.RB(): Amount = Amount.ofBytes(this, SI.RONNA)

fun Number.QB(): Amount = Amount.ofBytes(this, SI.QUETTA)

fun BigDecimal.QB(): Amount = Amount.ofBytes(this, SI.QUETTA)

fun Number.toIecBytes(): Amount = Amount.ofBytes(this, IEC.defaultNotScalingPrefix)

fun BigDecimal.toIecBytes(): Amount = Amount.ofBytes(this, IEC.defaultNotScalingPrefix)

fun Number.KiB(): Amount = Amount.ofBytes(this, IEC.KIBI)

fun BigDecimal.KiB(): Amount = Amount.ofBytes(this, IEC.KIBI)

fun Number.MiB(): Amount = Amount.ofBytes(this, IEC.MEBI)

fun BigDecimal.MiB(): Amount = Amount.ofBytes(this, IEC.MEBI)

fun Number.GiB(): Amount = Amount.ofBytes(this, IEC.GIBI)

fun BigDecimal.GiB(): Amount = Amount.ofBytes(this, IEC.GIBI)

fun Number.TiB(): Amount = Amount.ofBytes(this, IEC.TEBI)

fun BigDecimal.TiB(): Amount = Amount.ofBytes(this, IEC.TEBI)

fun Number.PiB(): Amount = Amount.ofBytes(this, IEC.PEBI)

fun BigDecimal.PiB(): Amount = Amount.ofBytes(this, IEC.PEBI)

fun Number.EiB(): Amount = Amount.ofBytes(this, IEC.EXBI)

fun BigDecimal.EiB(): Amount = Amount.ofBytes(this, IEC.EXBI)

fun Number.ZiB(): Amount = Amount.ofBytes(this, IEC.ZEBI)

fun BigDecimal.ZiB(): Amount = Amount.ofBytes(this, IEC.ZEBI)

fun Number.YiB(): Amount = Amount.ofBytes(this, IEC.YOBI)

fun BigDecimal.YiB(): Amount = Amount.ofBytes(this, IEC.YOBI)

fun Number.RiB(): Amount = Amount.ofBytes(this, IEC.ROBI)

fun BigDecimal.RiB(): Amount = Amount.ofBytes(this, IEC.ROBI)

fun Number.QiB(): Amount = Amount.ofBytes(this, IEC.QUEBI)

fun BigDecimal.QiB(): Amount = Amount.ofBytes(this, IEC.QUEBI)

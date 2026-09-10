package net.tegulis.komputus.codegen.units

import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.ParameterSpec
import java.util.Locale
import net.tegulis.komputus.codegen.References.IEC_CLASS_NAME
import net.tegulis.komputus.codegen.References.SI_CLASS_NAME
import net.tegulis.komputus.codegen.SourceGenerator
import net.tegulis.komputus.codegen.simpleName
import net.tegulis.komputus.codegen.units.UnitShorthands.VALUE_TYPES
import net.tegulis.komputus.prefixes.IEC
import net.tegulis.komputus.prefixes.SI
import net.tegulis.komputus.units.BinaryInformation

object BinaryInformationShorthandsGenerator : SourceGenerator {
    /**
     * Factory functions:
     * - `fun Amount.Companion.ofBits(bits: Number, prefix: Prefix = SI.defaultNotScalingPrefix): Amount =
     *   BinaryInformation.Bit.amountOf(bits, prefix)`
     * - `fun Amount.Companion.ofBits(bits: BigDecimal, prefix: Prefix = SI.defaultNotScalingPrefix): Amount =
     *   BinaryInformation.Bit.amountOf(bits, prefix)`
     * - `fun Amount.Companion.ofNibbles(nibbles: Number, prefix: Prefix = SI.defaultNotScalingPrefix): Amount =
     *   BinaryInformation.Nibble.amountOf(nibbles, prefix)`
     * - `fun Amount.Companion.ofNibbles(nibbles: BigDecimal, prefix: Prefix = SI.defaultNotScalingPrefix): Amount =
     *   BinaryInformation.Nibble.amountOf(nibbles, prefix)` Shorthand functions for SI:
     * - `fun Number.toSiBits() = Amount.ofBits(this, SI.defaultNotScalingPrefix)`
     * - `fun BigDecimal.toSiBits() = Amount.ofBits(this, SI.defaultNotScalingPrefix)`
     * - `fun Number.Qb() = Amount.ofBits(this, SI.QUETTA)`
     * - `fun BigDecimal.Qb() = Amount.ofBits(this, SI.QUETTA)`
     * - `fun Number.toSiBytes() = Amount.ofBytes(this, SI.defaultNotScalingPrefix)`
     * - `fun BigDecimal.toSiBytes() = Amount.ofBytes(this, SI.defaultNotScalingPrefix)`
     * - `fun Number.QB() = Amount.ofBytes(this, SI.QUETTA)`
     * - `fun BigDecimal.QB() = Amount.ofBytes(this, SI.QUETTA)` Shorthand functions for IEC:
     * - `fun Number.toIecBits() = Amount.ofBits(this, IEC.defaultNotScalingPrefix)`
     * - `fun BigDecimal.toIecBits() = Amount.ofBits(this, IEC.defaultNotScalingPrefix)`
     * - `fun Number.Kib() = Amount.ofBits(this, IEC.KIBI)`
     * - `fun BigDecimal.Kib() = Amount.ofBits(this, IEC.KIBI)`
     * - `fun Number.toIecBytes() = Amount.ofBytes(this, IEC.defaultNotScalingPrefix)`
     * - `fun BigDecimal.toIecBytes() = Amount.ofBytes(this, IEC.defaultNotScalingPrefix)`
     * - `fun Number.KiB() = Amount.ofBytes(this, IEC.KIBI)`
     * - `fun BigDecimal.KiB() = Amount.ofBytes(this, IEC.KIBI)`
     */
    override fun generate(): List<FileSpec> {
        val fileSpecBuilder = UnitShorthands.getFileSpecBuilder("BinaryInformationShorthands")
        for (unit in BinaryInformation.units) {
            val siPrefixBuilder: ParameterSpec.Builder.() -> Unit = {
                defaultValue("%T.defaultNotScalingPrefix", SI_CLASS_NAME)
            }
            val iecPrefixBuilder: ParameterSpec.Builder.() -> Unit = {
                defaultValue("%T.defaultNotScalingPrefix", IEC_CLASS_NAME)
            }
            val prefixBuilder: ParameterSpec.Builder.() -> Unit =
                if (unit == BinaryInformation.Octet || unit == BinaryInformation.Byte) {
                    iecPrefixBuilder
                } else {
                    siPrefixBuilder
                }
            for (valueType in VALUE_TYPES) {
                fileSpecBuilder.addFunction(UnitShorthands.factoryFunction(unit, valueType, prefixBuilder))
            }
        }
        for (unit in listOf(BinaryInformation.Bit, BinaryInformation.Byte)) {
            // SI
            for (receiver in VALUE_TYPES) {
                fileSpecBuilder.addFunction(
                    UnitShorthands.shorthandFunction(
                        "toSi${unit.pluralName.replaceFirstChar { it.titlecase(Locale.ROOT) }}",
                        receiver,
                        unit,
                        CodeBlock.builder().add("SI.defaultNotScalingPrefix").build(),
                    )
                )
            }
            for (prefix in SI.prefixes.filter { it.isMajor && it.power > 0 }) {
                for (receiver in VALUE_TYPES) {
                    fileSpecBuilder.addFunction(
                        UnitShorthands.shorthandFunction(
                            prefix.symbol + unit.symbol,
                            receiver,
                            unit,
                            CodeBlock.builder().add("SI.%L", prefix.simpleName()).build(),
                        )
                    )
                }
            }
            // IEC
            for (receiver in VALUE_TYPES) {
                fileSpecBuilder.addFunction(
                    UnitShorthands.shorthandFunction(
                        "toIec${unit.pluralName.replaceFirstChar { it.titlecase(Locale.ROOT) }}",
                        receiver,
                        unit,
                        CodeBlock.builder().add("IEC.defaultNotScalingPrefix").build(),
                    )
                )
            }
            for (prefix in IEC.prefixes.filter { it.isMajor && it.power > 0 }) {
                for (receiver in VALUE_TYPES) {
                    fileSpecBuilder.addFunction(
                        UnitShorthands.shorthandFunction(
                            prefix.symbol + unit.symbol,
                            receiver,
                            unit,
                            CodeBlock.builder().add("IEC.%L", prefix.simpleName()).build(),
                        )
                    )
                }
            }
        }
        return listOf(fileSpecBuilder.build())
    }
}

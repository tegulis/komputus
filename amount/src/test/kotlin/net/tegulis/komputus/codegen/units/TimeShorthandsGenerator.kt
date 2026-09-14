package net.tegulis.komputus.codegen.units

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.ParameterSpec
import net.tegulis.komputus.codegen.References.PREFIXES_PACKAGE
import net.tegulis.komputus.codegen.References.SI_CLASS_NAME
import net.tegulis.komputus.codegen.SourceGenerator
import net.tegulis.komputus.codegen.units.UnitShorthands.VALUE_TYPES
import net.tegulis.komputus.units.Time

object TimeShorthandsGenerator : SourceGenerator {
    /**
     * Factory functions:
     * - `fun Amount.Companion.ofSeconds(seconds: Number, prefix: Prefix = SI.defaultNotScalingPrefix): Amount =
     *   Time.Second.amountOf(seconds, prefix)`
     * - `fun Amount.Companion.ofSeconds(seconds: BigDecimal, prefix: Prefix = SI.defaultNotScalingPrefix): Amount =
     *   Time.Second.amountOf(seconds, prefix)`
     * - `fun Amount.Companion.ofMinutes(minutes: Number, prefix: Prefix = NotScalingPrefix): Amount =
     *   Time.Minute.amountOf(minutes, prefix)`
     * - `fun Amount.Companion.ofMinutes(minutes: BigDecimal, prefix: Prefix = NotScalingPrefix): Amount =
     *   Time.Minute.amountOf(minutes, prefix)` Shorthand functions:
     * - `fun Number.seconds() = Amount.ofSeconds(this)`
     * - `fun BigDecimal.seconds() = Amount.ofSeconds(this)`
     * - `fun Number.minutes() = Amount.ofMinutes(this)`
     * - `fun BigDecimal.minutes() = Amount.ofMinutes(this)`
     */
    override fun generate(): List<FileSpec> {
        val fileSpecBuilder = UnitShorthands.getFileSpecBuilder("TimeShorthands")
        for (unit in Time.units) {
            val secondsPrefixBuilder: ParameterSpec.Builder.() -> Unit = {
                defaultValue("%T.defaultNotScalingPrefix", SI_CLASS_NAME)
            }
            val defaultPrefixBuilder: ParameterSpec.Builder.() -> Unit = {
                defaultValue("%T", ClassName(PREFIXES_PACKAGE, "NotScalingPrefix"))
            }
            val prefixBuilder: ParameterSpec.Builder.() -> Unit =
                if (unit == Time.Second) secondsPrefixBuilder else defaultPrefixBuilder
            for (valueType in VALUE_TYPES) {
                fileSpecBuilder.addFunction(UnitShorthands.factoryFunction(unit, valueType, prefixBuilder))
            }
        }
        for (unit in Time.units) {
            for (receiver in VALUE_TYPES) {
                fileSpecBuilder.addFunction(UnitShorthands.shorthandFunction(unit.pluralName, receiver, unit))
            }
        }
        return listOf(fileSpecBuilder.build())
    }
}

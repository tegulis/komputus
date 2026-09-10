package net.tegulis.komputus.codegen.units

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.TypeName
import java.util.Locale
import net.tegulis.komputus.codegen.References
import net.tegulis.komputus.codegen.References.AMOUTN_CLASS_NAME
import net.tegulis.komputus.codegen.References.BIG_DECIMAL_CLASS_NAME
import net.tegulis.komputus.codegen.References.NUMBER_CLASS_NAME
import net.tegulis.komputus.codegen.References.PREFIX_CLASS_NAME
import net.tegulis.komputus.codegen.simpleName
import net.tegulis.komputus.units.UnitOfMeasurement

object UnitShorthands {
    val VALUE_TYPES = listOf(NUMBER_CLASS_NAME, BIG_DECIMAL_CLASS_NAME)

    fun getFileSpecBuilder(fileName: String) =
        FileSpec.builder("net.tegulis.komputus.units", fileName)
            .indent("    ")
            .addFileComment("Generated file - DO NOT edit by hand!")

    private fun factoryName(unit: UnitOfMeasurement) =
        "of${unit.pluralName.replaceFirstChar { it.titlecase(Locale.ROOT) }}"

    /**
     * An `Amount.Companion.ofSomething()` style factory function for one unit and value type.
     *
     * Examples:
     * - `fun Amount.Companion.ofBits(bits: Number, prefix: Prefix = SI.defaultNotScalingPrefix): Amount =
     *   BinaryInformation.Bit.amountOf(bits, prefix)`
     * - `fun Amount.Companion.ofSeconds(seconds: Number, prefix: Prefix = SI.defaultNotScalingPrefix): Amount =
     *   Time.Second.amountOf(seconds, prefix)`
     */
    fun factoryFunction(
        unit: UnitOfMeasurement,
        valueType: TypeName,
        defaultPrefixBuilder: ParameterSpec.Builder.() -> Unit,
    ): FunSpec {
        val unitReference = ClassName(References.UNITS_PACKAGE, unit.dimension.simpleName(), unit.simpleName())
        val valueParameter = ParameterSpec.builder(unit.pluralName, valueType).build()
        val prefixParameter = ParameterSpec.builder("prefix", PREFIX_CLASS_NAME).apply(defaultPrefixBuilder).build()
        val factoryName = factoryName(unit)
        return FunSpec.builder(factoryName)
            .receiver(References.AMOUNT_COMPANION_CLASS_NAME)
            .addParameter(valueParameter)
            .addParameter(prefixParameter)
            .returns(AMOUTN_CLASS_NAME)
            .addStatement("return %T.amountOf(%N, %N)", unitReference, valueParameter, prefixParameter)
            .build()
    }

    /**
     * A shorthand extension.
     *
     * Examples:
     * - `fun Number.seconds(): Amount = Amount.ofSeconds(this)`
     * - `fun BigDecimal.kb(): Amount = Amount.ofBits(this, SI.KILO)`
     * - `fun Number.toIecBits(): Amount = Amount.ofBits(this, IEC.defaultNotScalingPrefix)`
     */
    fun shorthandFunction(
        shorthandName: String,
        receiver: TypeName,
        unit: UnitOfMeasurement,
        prefixCodeBlock: CodeBlock? = null,
    ): FunSpec {
        val builder = FunSpec.builder(shorthandName).receiver(receiver).returns(AMOUTN_CLASS_NAME)
        val factory = factoryName(unit)
        if (prefixCodeBlock == null) {
            builder.addStatement("return %T.$factory(this)", AMOUTN_CLASS_NAME)
        } else {
            builder.addStatement("return %T.$factory(this, %L)", AMOUTN_CLASS_NAME, prefixCodeBlock)
        }
        return builder.build()
    }
}

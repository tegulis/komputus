package net.tegulis.komputus.codegen

import com.squareup.kotlinpoet.ClassName

object References {
    val AMOUTN_CLASS_NAME = ClassName(AMOUNT_PACKAGE, "Amount")
    val AMOUNT_COMPANION_CLASS_NAME = AMOUTN_CLASS_NAME.nestedClass("Companion")
    val PREFIX_CLASS_NAME = ClassName(PREFIXES_PACKAGE, "Prefix")
    val SI_CLASS_NAME = ClassName(PREFIXES_PACKAGE, "SI")
    val IEC_CLASS_NAME = ClassName(PREFIXES_PACKAGE, "IEC")
    val BIG_DECIMAL_CLASS_NAME = ClassName("java.math", "BigDecimal")
    val NUMBER_CLASS_NAME = ClassName("kotlin", "Number")
    const val KOMPUTUS_PACKAGE = "net.tegulis.komputus"
    const val AMOUNT_PACKAGE = "${KOMPUTUS_PACKAGE}.amount"
    const val PREFIXES_PACKAGE = "${KOMPUTUS_PACKAGE}.prefixes"
    const val UNITS_PACKAGE = "${KOMPUTUS_PACKAGE}.units"
}

fun Any.simpleName() = this::class.simpleName ?: error("cannot name this class")

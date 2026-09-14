package net.tegulis.komputus.codegen

import com.squareup.kotlinpoet.FileSpec

interface SourceGenerator {
    fun generate(): List<FileSpec>
}

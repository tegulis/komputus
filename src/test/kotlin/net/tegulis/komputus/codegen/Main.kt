package net.tegulis.komputus.codegen

import java.io.File
import net.tegulis.komputus.codegen.units.BinaryInformationShorthandsGenerator
import net.tegulis.komputus.codegen.units.TimeShorthandsGenerator

/** Every generator that needs running. */
private val generators: List<SourceGenerator> = listOf(BinaryInformationShorthandsGenerator, TimeShorthandsGenerator)

/**
 * Entry point of the source generator, run by the `generateSources` Gradle task.
 *
 * @param args the output directory, defaulting to `src/main/kotlin` relative to the project directory.
 */
fun main(args: Array<String>) {
    val outputDirectory = File(args.firstOrNull() ?: "src/main/kotlin")
    println("Komputus source generator outputting to $outputDirectory")
    for (generator in generators) {
        println("  running: ${generator::class.simpleName}")
        for (file in generator.generate()) {
            val relativePath = "${file.packageName.replace('.', '/')}/${file.name}.kt"
            file.writeTo(outputDirectory)

            // Clean up unnecessary `public` modifiers:
            // KotlinPoet prints one on every top-level declaration and offers no option to omit it.
            // Kotlin defaults to public, so it warns.
            val target = File(outputDirectory, relativePath)
            target.writeText(target.readText().replace(Regex("^public ", RegexOption.MULTILINE), ""))

            println("    wrote: $relativePath")
        }
    }
}

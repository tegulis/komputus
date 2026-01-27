import com.ncorti.ktfmt.gradle.TrailingCommaManagementStrategy
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.gradle.ktfmt)
    alias(libs.plugins.gradle.versions)
}

group = "net.tegulis.komputus"

version = "0.1.1"

kotlin.jvmToolchain(25)

// Co-locate Java sources with Kotlin
sourceSets {
    main { java.srcDirs("src/main/kotlin") }
    test { java.srcDirs("src/test/kotlin") }
}

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    // TESTING
    // JUnit Jupiter
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.platform)
    // Google Truth
    testImplementation(libs.google.truth)
}

tasks.withType<Test> {
    // Check formatting before testing for good measure, even though it will be skipped since it is
    // disabled below
    dependsOn("ktfmtCheck")
    useJUnitPlatform()
    enableAssertions = true
    // Extra settings for very verbose testing
    testLogging {
        events = TestLogEvent.entries.filter { it != TestLogEvent.STARTED }.toSet()
        exceptionFormat = TestExceptionFormat.FULL
        showExceptions = true
        showCauses = true
        showStackTraces = true
    }
    // Google Truth: don't clean stack traces
    systemProperty("com.google.common.truth.disable_stack_trace_cleaning", "true")
    // Don't generate reports
    reports.all { required = false }
    // Temporary solution for MockK: https://github.com/mockk/mockk/issues/1171
    jvmArgs("-XX:+EnableDynamicAgentLoading")
}

ktfmt {
    kotlinLangStyle()
    maxWidth.set(120)
    removeUnusedImports.set(true)
    trailingCommaManagementStrategy.set(TrailingCommaManagementStrategy.COMPLETE)
}

// Don't check formatting unless explicitly asked for
listOf("", "Main", "Scripts", "Test").forEach { taskName ->
    tasks.named("ktfmtCheck$taskName") { enabled = gradle.startParameter.taskNames.contains(this.name) }
}

// Format code before compiling
tasks.withType<KotlinCompile> { dependsOn("ktfmtFormat") }

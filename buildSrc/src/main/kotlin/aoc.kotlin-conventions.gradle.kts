import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    kotlin("jvm")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

repositories {
    mavenCentral()
}

tasks.withType<JavaCompile>().configureEach {
    options.release.set(21)
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_21
        freeCompilerArgs.add("-Xcontext-receivers")
    }
}

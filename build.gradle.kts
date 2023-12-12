plugins {
    id("aoc.kotlin-conventions")
    id("application")
    id("com.github.johnrengelman.shadow") version "7.0.0"
}

group = "de.skyrising"
version = "1.0"

val yearProjects = subprojects.filter { it.name.matches(Regex("aoc-\\d{4}")) }

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

dependencies {
    implementation(project(":aoc-utils"))
    yearProjects.forEach(::implementation)
}

application {
    mainClass.set("de.skyrising.aoc.AocKt")
}
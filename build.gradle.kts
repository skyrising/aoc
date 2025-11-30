plugins {
    id("aoc.kotlin-conventions")
    id("application")
    id("com.gradleup.shadow") version "9.2.2"
}

group = "de.skyrising"
version = "1.0"

val yearProjects = subprojects.filter { it.name.matches(Regex("aoc-\\d{4}")) }

dependencies {
    implementation(project(":aoc-utils"))
    yearProjects.forEach(::implementation)
}

application {
    mainClass.set("de.skyrising.aoc.AocKt")
}
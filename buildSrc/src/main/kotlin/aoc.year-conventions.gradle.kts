import kotlinx.benchmark.gradle.JvmBenchmarkTarget

plugins {
    id("aoc.kotlin-conventions")
    id("org.jetbrains.kotlinx.benchmark")
    kotlin("plugin.allopen")
    id("com.google.devtools.ksp")
}

dependencies {
    implementation(project(":aoc-utils"))
    ksp(project(":aoc-ksp"))
}

val year = project.name.substringAfter("aoc-").toInt()

ksp {
    arg("aoc-year-package", "de.skyrising.aoc$year")
}

allOpen {
    annotation("org.openjdk.jmh.annotations.State")
}

benchmark {
    targets {
        register("main") {
            (this as JvmBenchmarkTarget).jmhVersion = "1.37"
        }
    }
}
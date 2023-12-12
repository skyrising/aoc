import kotlinx.benchmark.gradle.JvmBenchmarkTarget

plugins {
    id("aoc.kotlin-conventions")
    id("org.jetbrains.kotlinx.benchmark")
    kotlin("plugin.allopen")
}

dependencies {
    implementation(project(":aoc-utils"))
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
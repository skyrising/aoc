
plugins {
    id("aoc.kotlin-conventions")
    id("org.jetbrains.kotlinx.benchmark")
}

dependencies {
    api(libs.kotlinx.benchmark.runtime)
    api(libs.fastutil)
    api(libs.commons.math3)
    api(libs.z3.turnkey)
    compileOnly(libs.jmh.core)
}

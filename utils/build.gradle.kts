
plugins {
    id("aoc.kotlin-conventions")
    id("org.jetbrains.kotlinx.benchmark")
}

dependencies {
    api("org.jetbrains.kotlinx:kotlinx-benchmark-runtime:0.4.10")
    api("it.unimi.dsi:fastutil:8.5.13")
    api("org.apache.commons:commons-math3:3.6.1")
    api("tools.aqua:z3-turnkey:4.13.0")
    compileOnly("org.openjdk.jmh:jmh-core:1.37")
}

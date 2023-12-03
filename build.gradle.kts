import kotlinx.benchmark.gradle.JvmBenchmarkTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.21"
    kotlin("plugin.allopen") version "1.9.21"
    id("org.jetbrains.kotlinx.benchmark") version "0.4.9"
    id("application")
}

group = "de.skyrising"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("it.unimi.dsi:fastutil:8.5.12")
    implementation("org.jetbrains.kotlinx:kotlinx-benchmark-runtime:0.4.7")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0")
}

application {
    mainClassName = "de.skyrising.aoc.AocKt"
}

allOpen {
    annotation("org.openjdk.jmh.annotations.State")
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions.jvmTarget = "21"
}

benchmark {
    targets {
        register("main") {
            (this as JvmBenchmarkTarget).jmhVersion = "1.37"
        }
    }
}
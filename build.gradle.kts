import kotlinx.benchmark.gradle.JvmBenchmarkTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.20"
    id("kotlinx.benchmark") version "0.2.0-dev-20"
    id("org.jetbrains.kotlin.plugin.allopen") version "1.4.20"

    id("application")
}

group = "de.skyrising"
version = "1.0"

repositories {
    mavenCentral()
    maven("https://dl.bintray.com/kotlin/kotlinx")
}

dependencies {
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("it.unimi.dsi:fastutil:8.4.4")
    implementation("org.jetbrains.kotlinx:kotlinx.benchmark.runtime-jvm:0.2.0-dev-20")
}

application {
    mainClassName = "de.skyrising.aoc2020.AocKt"
}

allOpen {
    annotation("org.openjdk.jmh.annotations.State")
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions.jvmTarget = "1.8"
}

benchmark {
    targets {
        register("main") {
            (this as JvmBenchmarkTarget).jmhVersion = "1.27"
        }
    }
}
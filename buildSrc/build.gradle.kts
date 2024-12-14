plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation(libs.kotlin.gradle.plugin)
    implementation(libs.kotlin.allopen)
    implementation(libs.kotlinx.benchmark.plugin)
    implementation(libs.ksp)
}

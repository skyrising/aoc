plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.21")
    implementation("org.jetbrains.kotlin:kotlin-allopen:1.9.21")
    implementation("org.jetbrains.kotlinx:kotlinx-benchmark-plugin:0.4.10")
    implementation("com.google.devtools.ksp:com.google.devtools.ksp.gradle.plugin:1.9.21-1.0.16")
}

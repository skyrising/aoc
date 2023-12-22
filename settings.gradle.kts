import java.time.LocalDate

rootProject.name = "aoc"

include("aoc-ksp")
project(":aoc-ksp").projectDir = file("ksp")

include("aoc-utils")
project(":aoc-utils").projectDir = file("utils")

for (year in 2015..LocalDate.now().year) {
    val dir = file("$year")
    if (dir.exists()) {
        include("aoc-$year")
        project(":aoc-$year").projectDir = dir
    }
}

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}
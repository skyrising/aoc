package de.skyrising.aoc2022

import de.skyrising.aoc.TestInput
import de.skyrising.aoc.Vec3i
import de.skyrising.aoc.boundingBox
import de.skyrising.aoc.floodFill

class BenchmarkDay18 : BenchmarkDayV1(18)

fun registerDay18() {
    val test = TestInput("""
        2,2,2
        1,2,2
        3,2,2
        2,1,2
        2,3,2
        2,2,1
        2,2,3
        2,2,4
        2,2,6
        1,2,5
        3,2,5
        2,1,5
        2,3,5
    """)
    puzzle(18, "Boiling Boulders") {
        lines.mapTo(mutableSetOf(), Vec3i::parse).let { cubes -> cubes.sumOf { c -> c.sixNeighbors().count { it !in cubes } } }
    }
    puzzle(18, "Part Two") {
        val cubes = lines.mapTo(mutableSetOf(), Vec3i::parse)
        val bbox = cubes.boundingBox().expand(1)
        val outside = floodFill(bbox.min) { it.sixNeighbors().filter { n -> n in bbox && n !in cubes } }
        cubes.sumOf { c -> c.sixNeighbors().count { it in outside } }
    }
}
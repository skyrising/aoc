@file:PuzzleName("Boiling Boulders")

package de.skyrising.aoc2022.day18

import de.skyrising.aoc.*

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

fun PuzzleInput.part1(): Any {
    return lines.mapTo(mutableSetOf(), Vec3i::parse).let { cubes -> cubes.sumOf { c -> c.sixNeighbors().count { it !in cubes } } }
}

fun PuzzleInput.part2(): Any {
    val cubes = lines.mapTo(mutableSetOf(), Vec3i::parse)
    val bbox = cubes.boundingBox().expand(1)
    val outside = floodFill(bbox.min) { it.sixNeighbors().filter { n -> n in bbox && n !in cubes } }
    return cubes.sumOf { c -> c.sixNeighbors().count { it in outside } }
}

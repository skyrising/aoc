package de.skyrising.aoc2022.day14

import de.skyrising.aoc.*

@Suppress("unused")
class BenchmarkDay : BenchmarkBaseV1(2022, 14)

private fun parseInput(input: PuzzleInput, floor: Boolean = false): CharGrid {
    val lines = input.lines.flatMapTo(mutableListOf()) { line ->
        line.split(" -> ").map(Vec2i::parse).zipWithNext { a, b -> a lineTo b }
    }
    if (floor) {
        val floorPos = lines.boundingBox().max.y + 2
        lines.add(Vec2i(500 - floorPos - 1, floorPos) lineTo Vec2i(500 + floorPos + 1, floorPos))
    }
    return lines.boundingBox().expand(Vec2i(500, 0)).charGrid { '.' }.also { it[lines.flatten()] = '#' }
}

private fun CharGrid.dropSand(pos: Vec2i) = pos.iterate {
    if (y == Int.MAX_VALUE) return@iterate null
    for (below in arrayOf(south, southWest, southEast)) {
        if (below.y >= height) return@iterate Vec2i(x, Int.MAX_VALUE)
        if (this@dropSand[below] == '.') return@iterate below
    }
    null
}.also { if (it in this) this[it] = 'o' }

@Suppress("unused")
fun register() {
    val test = TestInput("""
        498,4 -> 498,6 -> 496,6
        503,4 -> 502,4 -> 502,9 -> 494,9
    """)
    part1("Regolith Reservoir") {
        val grid = parseInput(this)
        countWhile {
            grid.dropSand(Vec2i(500, 0)) in grid
        }
    }
    part2 {
        val grid = parseInput(this, true)
        1 + countWhile {
            grid.dropSand(Vec2i(500, 0)).y != 0
        }
    }
}
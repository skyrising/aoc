package de.skyrising.aoc2021

import de.skyrising.aoc.TestInput
import de.skyrising.aoc.part1
import de.skyrising.aoc.part2

@Suppress("unused")
class BenchmarkDay11 : BenchmarkDayV1(11)

@Suppress("unused")
fun registerDay11() {
    val test = TestInput("""
        5483143223
        2745854711
        5264556173
        6141336146
        6357385478
        4167524645
        2176841721
        6882881134
        4846848554
        5283751526
    """)
    val test2 = TestInput("""
        11111
        19991
        19191
        19991
        11111
    """)
    part1("Dumbo Octopus") {
        val octopuses = Array<IntArray>(lines.size) { line -> lines[line].chars().map { n -> n - '0'.code }.toArray() }
        val width = octopuses[0].size
        val height = octopuses.size
        var flashes = 0
        for (step in 1..100) {
            for (y in 0 until height) for (x in 0 until width) {
                octopuses[y][x]++
            }
            val flashesThisStep = mutableSetOf<Pair<Int, Int>>()
            for (y in 0 until height) for (x in 0 until width) {
                if (octopuses[y][x] == 10) {
                    flash(octopuses, width, height, x, y, flashesThisStep)
                }
            }
            for ((x, y) in flashesThisStep) {
                octopuses[y][x] = 0
            }
            flashes += flashesThisStep.size
        }
        flashes
    }
    part2 {
        val octopuses = Array<IntArray>(lines.size) { line -> lines[line].chars().map { n -> n - '0'.code }.toArray() }
        val width = octopuses[0].size
        val height = octopuses.size
        var step = 1
        while (true) {
            for (y in 0 until height) for (x in 0 until width) {
                octopuses[y][x]++
            }
            val flashesThisStep = mutableSetOf<Pair<Int, Int>>()
            for (y in 0 until height) for (x in 0 until width) {
                if (octopuses[y][x] == 10) {
                    flash(octopuses, width, height, x, y, flashesThisStep)
                }
            }
            for ((x, y) in flashesThisStep) {
                octopuses[y][x] = 0
            }
            if (flashesThisStep.size == width * height) return@part2 step
            step++
        }
    }
}

private fun flash(octopuses: Array<IntArray>, width: Int, height: Int, x: Int, y: Int, flashesThisStep: MutableSet<Pair<Int, Int>>) {
    if (!flashesThisStep.add(Pair(x, y))) return
    if (x > 0) {
        if (octopuses[y][x - 1]++ >= 9) flash(octopuses, width, height, x - 1, y, flashesThisStep)
        if (y > 0 && octopuses[y - 1][x - 1]++ >= 9) flash(octopuses, width, height, x - 1, y - 1, flashesThisStep)
        if (y < height - 1 && octopuses[y + 1][x - 1]++ >= 9) flash(octopuses, width, height, x - 1, y + 1, flashesThisStep)
    }
    if (x < width - 1) {
        if (octopuses[y][x + 1]++ >= 9) flash(octopuses, width, height, x + 1, y, flashesThisStep)
        if (y > 0 && octopuses[y - 1][x + 1]++ >= 9) flash(octopuses, width, height, x + 1, y - 1, flashesThisStep)
        if (y < height - 1 && octopuses[y + 1][x + 1]++ >= 9) flash(octopuses, width, height, x + 1, y + 1, flashesThisStep)
    }
    if (y > 0 && octopuses[y - 1][x]++ >= 9) flash(octopuses, width, height, x, y - 1, flashesThisStep)
    if (y < height - 1 && octopuses[y + 1][x]++ >= 9) flash(octopuses, width, height, x, y + 1, flashesThisStep)
}
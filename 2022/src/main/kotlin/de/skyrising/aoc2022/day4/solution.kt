package de.skyrising.aoc2022.day4

import de.skyrising.aoc.*

private fun parseRange(range: String): IntRange {
    val (from, to) = range.split('-')
    return from.toInt()..to.toInt()
}

@PuzzleName("Camp Cleanup")
fun PuzzleInput.part1(): Any {
    var count = 0
    for (line in lines) {
        val (a, b) = line.split(",")
        val aRange = parseRange(a)
        val bRange = parseRange(b)
        if (bRange.first in aRange && bRange.last in aRange || aRange.first in bRange && aRange.last in bRange) {
            count++
        }
    }
    return count
}

fun PuzzleInput.part2(): Any {
    var count = 0
    for (line in lines) {
        val (a, b) = line.split(",")
        val aRange = parseRange(a)
        val bRange = parseRange(b)
        if (bRange.first in aRange || bRange.last in aRange || aRange.first in bRange || aRange.last in bRange) {
            count++
        }
    }
    return count
}

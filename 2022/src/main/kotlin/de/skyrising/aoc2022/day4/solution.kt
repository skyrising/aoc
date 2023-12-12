package de.skyrising.aoc2022.day4

import de.skyrising.aoc.BenchmarkBaseV1
import de.skyrising.aoc.part1
import de.skyrising.aoc.part2

@Suppress("unused")
class BenchmarkDay : BenchmarkBaseV1(2022, 4)

private fun parseRange(range: String): IntRange {
    val (from, to) = range.split('-')
    return from.toInt()..to.toInt()
}

@Suppress("unused")
fun register() {
    part1("Camp Cleanup") {
        var count = 0
        for (line in lines) {
            val (a, b) = line.split(",")
            val aRange = parseRange(a)
            val bRange = parseRange(b)
            if (bRange.first in aRange && bRange.last in aRange || aRange.first in bRange && aRange.last in bRange) {
                count++
            }
        }
        count
    }

    part2 {
        var count = 0
        for (line in lines) {
            val (a, b) = line.split(",")
            val aRange = parseRange(a)
            val bRange = parseRange(b)
            if (bRange.first in aRange || bRange.last in aRange || aRange.first in bRange || aRange.last in bRange) {
                count++
            }
        }
        count
    }
}
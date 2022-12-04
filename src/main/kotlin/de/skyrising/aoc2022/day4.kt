package de.skyrising.aoc2022

class BenchmarkDay4 : BenchmarkDayV1(4)

private fun parseRange(range: String): IntRange {
    val (from, to) = range.split('-')
    return from.toInt()..to.toInt()
}

fun registerDay4() {
    puzzleLS(4, "Camp Cleanup") {
        var count = 0
        for (line in it) {
            val (a, b) = line.split(",")
            val aRange = parseRange(a)
            val bRange = parseRange(b)
            if (bRange.first in aRange && bRange.last in aRange || aRange.first in bRange && aRange.last in bRange) {
                count++
            }
        }
        count
    }

    puzzleLS(4, "Part Two") {
        var count = 0
        for (line in it) {
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
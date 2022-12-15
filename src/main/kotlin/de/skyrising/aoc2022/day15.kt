package de.skyrising.aoc2022

import de.skyrising.aoc.PuzzleInput
import de.skyrising.aoc.TestInput
import de.skyrising.aoc.Vec2i
import de.skyrising.aoc.ints
import kotlin.math.abs

class BenchmarkDay15 : BenchmarkDayV1(15)

private fun parseInput(input: PuzzleInput): List<Pair<Vec2i, Vec2i>> {
    return input.lines.map { val (a, b, c, d) = it.ints(); Vec2i(a, b) to Vec2i(c, d) }
}

private fun joinRanges(ranges: Collection<IntRange>): Set<IntRange> {
    if (ranges.isEmpty()) return emptySet()
    val sorted = ranges.sortedBy { it.first }
    val result = mutableSetOf<IntRange>()
    var current = sorted.first()
    for (range in sorted) {
        if (range.first <= current.last + 1) {
            current = current.first..maxOf(current.last, range.last)
        } else {
            result += current
            current = range
        }
    }
    result += current
    return result
}

private fun rangesForRow(pairs: List<Pair<Vec2i, Vec2i>>, row: Int): Set<IntRange> {
    val ranges = mutableListOf<IntRange>()
    for ((s, b) in pairs) {
        val dist = s.manhattanDistance(b)
        val yDiff = abs(s.y - row)
        val width = dist - yDiff
        if (width < 0) continue
        ranges.add(s.x - width..s.x + width)
    }
    return joinRanges(ranges)
}

private fun findGap(ranges: Set<IntRange>, min: Int, max: Int): Int? {
    if (ranges.size == 2) {
        return ranges.minByOrNull { it.first }!!.last + 1
    }
    val range = ranges.single()
    if (min !in range) return min
    if (max !in range) return max
    return null
}

fun registerDay15() {
    val test = TestInput("""
        Sensor at x=2, y=18: closest beacon is at x=-2, y=15
        Sensor at x=9, y=16: closest beacon is at x=10, y=16
        Sensor at x=13, y=2: closest beacon is at x=15, y=3
        Sensor at x=12, y=14: closest beacon is at x=10, y=16
        Sensor at x=10, y=20: closest beacon is at x=10, y=16
        Sensor at x=14, y=17: closest beacon is at x=10, y=16
        Sensor at x=8, y=7: closest beacon is at x=2, y=10
        Sensor at x=2, y=0: closest beacon is at x=2, y=10
        Sensor at x=0, y=11: closest beacon is at x=2, y=10
        Sensor at x=20, y=14: closest beacon is at x=25, y=17
        Sensor at x=17, y=20: closest beacon is at x=21, y=22
        Sensor at x=16, y=7: closest beacon is at x=15, y=3
        Sensor at x=14, y=3: closest beacon is at x=15, y=3
        Sensor at x=20, y=1: closest beacon is at x=15, y=3
    """)
    puzzle(15, "Beacon Exclusion Zone") {
        val pairs = parseInput(this)
        val row = 2000000
        val joined = rangesForRow(pairs, row)
        val beaconsThisRow = pairs.map { it.second }.filterTo(mutableSetOf()) { it.y == row }
        joined.sumOf { range ->
            range.last - range.first + 1 - beaconsThisRow.count { it.x in range }
        }
    }
    puzzle(15, "Part Two") {
        val pairs = parseInput(this)
        val bound = 4000000
        for (y in 0..bound) {
            val ranges = rangesForRow(pairs, y)
            val gap = findGap(ranges, 0, bound) ?: continue
            val pos = Vec2i(gap, y)
            return@puzzle pos.x * 4000000L + y
        }
        null
    }
}
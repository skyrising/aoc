@file:PuzzleName("Beacon Exclusion Zone")

package de.skyrising.aoc2022.day15

import de.skyrising.aoc.*
import kotlin.math.abs

private fun parseInput(input: PuzzleInput) = input.lines.map {
    val (a, b, c, d) = it.ints()
    Vec2i(a, b) to Vec2i(c, d)
}

private fun rangesForRow(pairs: List<Pair<Vec2i, Vec2i>>, row: Int) = joinRanges(pairs.mapNotNull { (s, b) ->
    val dist = s.manhattanDistance(b)
    val yDiff = abs(s.y - row)
    val width = dist - yDiff
    if (width < 0) null else s.x - width..s.x + width
})

private fun findGap(ranges: Set<IntRange>, min: Int, max: Int) = when (ranges.size) {
    1 -> {
        val range = ranges.single()
        when {
            min !in range -> min
            max !in range -> max
            else -> null
        }
    }
    2 ->  ranges.minBy(IntRange::first).last + 1
    else -> null
}

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

fun PuzzleInput.part1(): Any {
    val pairs = parseInput(this)
    val row = 2000000
    val joined = rangesForRow(pairs, row)
    val beaconsThisRow = pairs.map { it.second }.filterTo(mutableSetOf()) { it.y == row }
    return joined.sumOf { range ->
        range.last - range.first + 1 - beaconsThisRow.count { it.x in range }
    }
}

fun PuzzleInput.part2(): Any? {
    val pairs = parseInput(this)
    val bound = 4000000
    for (y in 0..bound) {
        val ranges = rangesForRow(pairs, y)
        val gap = findGap(ranges, 0, bound) ?: continue
        val pos = Vec2i(gap, y)
        return pos.x * 4000000L + y
    }
    return null
}

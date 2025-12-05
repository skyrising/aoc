@file:PuzzleName("Cafeteria")

package de.skyrising.aoc2025.day5

import de.skyrising.aoc.*

val test = TestInput("""
3-5
10-14
16-20
12-18

1
5
8
11
17
32
""")

fun PuzzleInput.part1(): Any {
    val (a, b) = lines.splitOnEmpty(2)
    val freshRanges = joinRanges(a.map(String::toLongRange))
    val items = b.map(String::toLong)
    return items.count { l -> freshRanges.any { l in it } }
}

fun PuzzleInput.part2(): Any {
    val (a, _) = lines.splitOnEmpty(2)
    val freshRanges = a.map(String::toLongRange)
    return joinRanges(freshRanges).sumOf { it.count() }
}

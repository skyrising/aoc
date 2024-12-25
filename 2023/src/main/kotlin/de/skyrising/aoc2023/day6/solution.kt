@file:PuzzleName("Wait For It")

package de.skyrising.aoc2023.day6

import de.skyrising.aoc.PuzzleInput
import de.skyrising.aoc.PuzzleName
import de.skyrising.aoc.TestInput
import de.skyrising.aoc.ints
import kotlin.math.sqrt

val test = TestInput("""
    Time:      7  15   30
    Distance:  9  40  200
""")

fun wins(t: Long, d: Long): Long {
    val t0 = ((t - sqrt(t*t-4.0*d)) / 2).toInt()
    return t - t0 * 2 - 1
}

fun PuzzleInput.part1(): Any {
    val (time, distance) = lines.map { it.ints() }
    return time.indices.map {
        wins(time.getInt(it).toLong(), distance.getInt(it).toLong())
    }.reduce(Long::times)
}

fun PuzzleInput.part2(): Any {
    val (t, d) = lines.map { it.filter(Char::isDigit).toLong() }
    return wins(t, d)
}

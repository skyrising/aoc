package de.skyrising.aoc2023

import de.skyrising.aoc.TestInput
import de.skyrising.aoc.ints
import de.skyrising.aoc.part1
import de.skyrising.aoc.part2
import kotlin.math.sqrt

@Suppress("unused")
class BenchmarkDay6 : BenchmarkDayV1(6)

@Suppress("unused")
fun registerDay6() {
    val test = TestInput("""
        Time:      7  15   30
        Distance:  9  40  200
    """)
    fun wins(t: Long, d: Long): Long {
        val t0 = ((t - sqrt(t*t-4.0*d)) / 2).toInt()
        return t - t0 * 2 - 1
    }
    part1("Wait For It") {
        val (time, distance) = lines.map { it.ints() }
        time.indices.map {
            wins(time.getInt(it).toLong(), distance.getInt(it).toLong())
        }.reduce(Long::times)
    }
    part2 {
        val (t, d) = lines.map { it.filter(Char::isDigit).toLong() }
        wins(t, d)
    }
}
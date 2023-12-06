package de.skyrising.aoc2023

import de.skyrising.aoc.TestInput
import de.skyrising.aoc.ints
import de.skyrising.aoc.part1
import de.skyrising.aoc.part2

@Suppress("unused")
class BenchmarkDay6 : BenchmarkDayV1(6)

@Suppress("unused")
fun registerDay6() {
    val test = TestInput("""
        Time:      7  15   30
        Distance:  9  40  200
    """)
    part1("") {
        val (time, distance) = lines.map { it.ints() }
        time.indices.map {
            val t = time.getInt(it)
            val d = distance.getInt(it)
            (0..t).count { t0 -> t0*(t-t0) > d }
        }.reduce(Int::times)
    }
    part2 {
        val (t, d) = lines.map { it.filter(Char::isDigit).toLong() }
        (0..t).count { t0 -> t0*(t-t0) > d }
    }
}
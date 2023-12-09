package de.skyrising.aoc2023

import de.skyrising.aoc.TestInput
import de.skyrising.aoc.ints
import de.skyrising.aoc.part1
import de.skyrising.aoc.part2
import it.unimi.dsi.fastutil.ints.IntArrayList
import it.unimi.dsi.fastutil.ints.IntList

@Suppress("unused")
class BenchmarkDay9 : BenchmarkDayV1(9)

@Suppress("unused")
fun registerDay9() {
    val test = TestInput("""
        0 3 6 9 12 15
        1 3 6 10 15 21
        10 13 16 21 30 45
    """)
    fun diffs(sequence: IntList): IntList = IntArrayList(sequence.windowed(2) { it[1] - it[0] })
    part1("Mirage Maintenance") {
        lines.map(String::ints).sumOf { line ->
            val lasts = IntArrayList()
            var current = line
            while (current.any { it != 0 }) {
                lasts.add(current.last())
                current = diffs(current)
            }
            lasts.reduceRight(Int::plus)
        }
    }
    part2 {
        lines.map(String::ints).sumOf { line ->
            val firsts = IntArrayList()
            var current = line
            while (current.any { it != 0 }) {
                firsts.add(current.first())
                current = diffs(current)
            }
            firsts.reduceRight(Int::minus)
        }
    }
}
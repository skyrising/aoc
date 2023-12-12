package de.skyrising.aoc2022.day6

import de.skyrising.aoc.BenchmarkBaseV1
import de.skyrising.aoc.part1
import de.skyrising.aoc.part2

@Suppress("unused")
class BenchmarkDay : BenchmarkBaseV1(2022, 6)

@Suppress("unused")
fun register() {
    part1("Tuning Trouble") {
        val window = CharArray(4)
        for (i in chars.indices) {
            val c = chars[i]
            window[i % 4] = c
            if (i < 4) continue
            if (window.toSet().size == 4) return@part1 i + 1
        }
        null
    }

    part2 {
        val window = CharArray(14)
        for (i in chars.indices) {
            val c = chars[i]
            window[i % 14] = c
            if (i < 14) continue
            if (window.toSet().size == 14) return@part2 i + 1
        }
        null
    }
}
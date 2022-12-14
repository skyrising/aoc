package de.skyrising.aoc2022

class BenchmarkDay6 : BenchmarkDayV1(6)

fun registerDay6() {
    puzzle(6, "Tuning Trouble") {
        val window = CharArray(4)
        for (i in chars.indices) {
            val c = chars[i]
            window[i % 4] = c
            if (i < 4) continue
            if (window.toSet().size == 4) return@puzzle i + 1
        }
        null
    }

    puzzle(6, "Part Two") {
        val window = CharArray(14)
        for (i in chars.indices) {
            val c = chars[i]
            window[i % 14] = c
            if (i < 14) continue
            if (window.toSet().size == 14) return@puzzle i + 1
        }
        null
    }
}
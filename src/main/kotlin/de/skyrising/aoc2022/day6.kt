package de.skyrising.aoc2022

class BenchmarkDay6 : BenchmarkDayV1(6)

fun registerDay6() {
    puzzleS(6, "Tuning Trouble") {
        val chars = CharArray(4)
        for (i in it.indices) {
            val c = it[i]
            chars[i % 4] = c
            if (i < 4) continue
            if (chars.toSet().size == 4) return@puzzleS i + 1
        }
        null
    }

    puzzleS(6, "Part Two") {
        val chars = CharArray(14)
        for (i in it.indices) {
            val c = it[i]
            chars[i % 14] = c
            if (i < 14) continue
            if (chars.toSet().size == 14) return@puzzleS i + 1
        }
        null
    }
}
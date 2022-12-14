package de.skyrising.aoc2015

import de.skyrising.aoc.TestInput

class BenchmarkDay8 : BenchmarkDayV1(8)

fun registerDay8() {
    val test = TestInput("""
        ""
        "abc"
        "aaa\"aaa"
        "\x27"
    """)
    puzzle(8, "Matchsticks") {
        var totalChars = 0
        var memChars = 0
        for (line in lines) {
            totalChars += line.length
            var i = 1
            while (i < line.lastIndex) {
                val c = line[i]
                if (c == '\\') {
                    i += when (line[i + 1]) {
                        '\\', '\"' -> 2
                        'x' -> 4
                        else -> 2
                    }
                } else {
                    i++
                }
                memChars++
            }
        }
        totalChars - memChars
    }
    puzzle(8, "Part Two") {
        var totalChars = 0
        var origChars = 0
        for (line in lines) {
            origChars += line.length
            totalChars += line.length + 2
            for (c in line) {
                when (c) {
                    '\\', '"' -> totalChars++
                }
            }
        }
        totalChars - origChars
    }
}
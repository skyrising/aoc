package de.skyrising.aoc2015.day8

import de.skyrising.aoc.BenchmarkBaseV1
import de.skyrising.aoc.TestInput
import de.skyrising.aoc.part1
import de.skyrising.aoc.part2

@Suppress("unused")
class BenchmarkDay : BenchmarkBaseV1(2015, 8)

@Suppress("unused")
fun register() {
    val test = TestInput("""
        ""
        "abc"
        "aaa\"aaa"
        "\x27"
    """)
    part1("Matchsticks") {
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
    part2 {
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
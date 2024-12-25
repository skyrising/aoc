@file:PuzzleName("Matchsticks")

package de.skyrising.aoc2015.day8

import de.skyrising.aoc.PuzzleInput
import de.skyrising.aoc.PuzzleName
import de.skyrising.aoc.TestInput

val test = TestInput("""
    ""
    "abc"
    "aaa\"aaa"
    "\x27"
""")

fun PuzzleInput.part1(): Any {
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
    return totalChars - memChars
}

fun PuzzleInput.part2(): Any {
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
    return totalChars - origChars
}

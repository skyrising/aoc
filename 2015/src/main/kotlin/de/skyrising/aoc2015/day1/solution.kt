@file:PuzzleName("Not Quite Lisp")

package de.skyrising.aoc2015.day1

import de.skyrising.aoc.PuzzleInput
import de.skyrising.aoc.PuzzleName

fun PuzzleInput.part1() = chars.sumOf { c -> if (c == '(') 1.toInt() else -1 }

fun PuzzleInput.part2(): Any {
    var floor = 0
    for (i in chars.indices) {
        when (chars[i]) {
            '(' -> floor++
            ')' -> floor--
        }
        if (floor == -1) return i + 1
    }
    return Unit
}

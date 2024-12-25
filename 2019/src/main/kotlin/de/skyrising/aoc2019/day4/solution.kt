@file:PuzzleName("Secure Container")

package de.skyrising.aoc2019.day4

import de.skyrising.aoc.PuzzleInput
import de.skyrising.aoc.PuzzleName

inline fun valid(password: String, validRun: (Int)->Boolean): Boolean {
    var last = '0'
    var hasValidRun = false
    var run = 1
    for (c in password) {
        if (c < last) return false
        if (c == last) {
            run++
        } else {
            if (validRun(run)) hasValidRun = true
            run = 1
        }
        last = c
    }
    return hasValidRun || validRun(run)
}

fun PuzzleInput.part1(): Any {
    val (min, max) = lines[0].split('-').map(String::toInt)
    return (min..max).count { valid(it.toString()) { it >= 2 } }
}

fun PuzzleInput.part2(): Any {
    val (min, max) = lines[0].split('-').map(String::toInt)
    return (min..max).count { valid(it.toString()) { it == 2 } }
}

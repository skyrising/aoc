package de.skyrising.aoc2019

import de.skyrising.aoc.part1
import de.skyrising.aoc.part2

@Suppress("unused")
class BenchmarkDay4 : BenchmarkDayV1(4)

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

@Suppress("unused")
fun registerDay4() {
    part1("Secure Container") {
        val (min, max) = lines[0].split('-').map(String::toInt)
        (min..max).count { valid(it.toString()) { it >= 2 } }
    }
    part2 {
        val (min, max) = lines[0].split('-').map(String::toInt)
        (min..max).count { valid(it.toString()) { it == 2 } }
    }
}
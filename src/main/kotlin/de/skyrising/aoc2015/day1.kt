package de.skyrising.aoc2015

import de.skyrising.aoc.part1
import de.skyrising.aoc.part2

@Suppress("unused")
class BenchmarkDay1 : BenchmarkDayV1(1)

@Suppress("unused")
fun registerDay1() {
    part1("Not Quite Lisp") {
        chars.sumOf { c -> if (c == '(') 1.toInt() else -1 }
    }
    part2 {
        var floor = 0
        for (i in chars.indices) {
            when (chars[i]) {
                '(' -> floor++
                ')' -> floor--
            }
            if (floor == -1) return@part2 i + 1
        }
    }
}
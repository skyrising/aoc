package de.skyrising.aoc2015

class BenchmarkDay1 : BenchmarkDayV1(1)

fun registerDay1() {
    puzzle(1, "Not Quite Lisp") {
        chars.sumOf { c -> if (c == '(') 1.toInt() else -1 }
    }
    puzzle(1, "Part Two") {
        var floor = 0
        for (i in chars.indices) {
            when (chars[i]) {
                '(' -> floor++
                ')' -> floor--
            }
            if (floor == -1) return@puzzle i + 1
        }
    }
}
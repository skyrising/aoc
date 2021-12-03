package de.skyrising.aoc2015

class BenchmarkDay1 : BenchmarkDayV1(1)

fun registerDay1() {
    puzzleS(1, "Not Quite Lisp") {
        it.sumOf { c -> if (c == '(') 1.toInt() else -1 }
    }
    puzzleS(1, "Part Two") {
        var floor = 0
        for (i in it.indices) {
            when (it[i]) {
                '(' -> floor++
                ')' -> floor--
            }
            if (floor == -1) return@puzzleS i + 1
        }
    }
}
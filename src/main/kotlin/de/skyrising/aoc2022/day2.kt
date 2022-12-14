package de.skyrising.aoc2022

import de.skyrising.aoc.TestInput

class BenchmarkDay2 : BenchmarkDayV1(2)

fun registerDay2() {
    val test = TestInput("""
        A Y
        B X
        C Z
    """)
    puzzle(2, "Rock Paper Scissors") {
        var total = 0
        for (line in lines) {
            if (line.isBlank()) continue
            val (a, b) = line.split(" ")
            val aInt = a[0].code - 'A'.code
            val bInt = b[0].code - 'X'.code
            val win = when (bInt) {
                aInt -> 3
                (aInt + 1) % 3 -> 6
                else -> 0
            }
            val shapePoints = bInt + 1
            val points = shapePoints + win
            total += points
        }
        total
    }

    puzzle(2, "Part Two") {
        var total = 0
        for (line in lines) {
            if (line.isBlank()) continue
            val (a, b) = line.split(" ")
            val aInt = a[0].code - 'A'.code
            val shape = when (b[0]) {
                'X' -> (aInt + 2) % 3
                'Y' -> aInt
                else -> (aInt + 1) % 3
            }
            val win = when (shape) {
                aInt -> 3
                (aInt + 1) % 3 -> 6
                else -> 0
            }
            val shapePoints = shape + 1
            val points = shapePoints + win
            total += points
        }
        total
    }
}
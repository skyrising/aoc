package de.skyrising.aoc2022.day2

import de.skyrising.aoc.BenchmarkBaseV1
import de.skyrising.aoc.TestInput
import de.skyrising.aoc.part1
import de.skyrising.aoc.part2

@Suppress("unused")
class BenchmarkDay : BenchmarkBaseV1(2022, 2)

@Suppress("unused")
fun register() {
    val test = TestInput("""
        A Y
        B X
        C Z
    """)
    part1("Rock Paper Scissors") {
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

    part2 {
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
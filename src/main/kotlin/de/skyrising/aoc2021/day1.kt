package de.skyrising.aoc2021

import de.skyrising.aoc.TestInput

class BenchmarkDay1 : BenchmarkDayV1(1)

fun registerDay1() {
    val test = TestInput("""
        199
        200
        208
        210
        200
        207
        240
        269
        260
        263
    """)
    puzzle(1, "Sonar Sweep") {
        val numbers = lines.map(String::toInt)
        var increasing = 0
        for (i in 1 .. numbers.lastIndex) {
            if (numbers[i] > numbers[i - 1]) increasing++
        }
        increasing
    }

    puzzle(1, "Part Two") {
        val numbers = lines.map(String::toInt)
        val movingSum = IntArray(numbers.size + 1)
        for (i in numbers.indices) {
            movingSum[i + 1] = numbers[i] + movingSum[i]
        }
        var increasing = 0
        for (i in 4 .. movingSum.lastIndex) {
            val prevWindow = movingSum[i - 1] - movingSum[maxOf(i - 4, 0)]
            val curWindow = movingSum[i] - movingSum[maxOf(i - 3, 0)]
            if (curWindow > prevWindow) increasing++
        }
        increasing
    }
}
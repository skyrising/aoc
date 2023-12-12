package de.skyrising.aoc2021.day1

import de.skyrising.aoc.BenchmarkBaseV1
import de.skyrising.aoc.TestInput
import de.skyrising.aoc.part1
import de.skyrising.aoc.part2

@Suppress("unused")
class BenchmarkDay : BenchmarkBaseV1(2021, 1)

@Suppress("unused")
fun register() {
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
    part1("Sonar Sweep") {
        val numbers = lines.map(String::toInt)
        var increasing = 0
        for (i in 1 .. numbers.lastIndex) {
            if (numbers[i] > numbers[i - 1]) increasing++
        }
        increasing
    }

    part2 {
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
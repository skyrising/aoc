package de.skyrising.aoc2024.day3

import de.skyrising.aoc.PuzzleInput
import de.skyrising.aoc.PuzzleName
import de.skyrising.aoc.TestInput

val test = TestInput("""
xmul(2,4)%&mul[3,7]!@^do_not_mul(5,5)+mul(32,64]then(mul(11,8)mul(8,5))
""")

@PuzzleName("Mull It Over")
fun PuzzleInput.part1() =
    Regex("mul\\((\\d+),(\\d+)\\)").findAll(string).sumOf { it.groupValues[1].toLong() * it.groupValues[2].toLong() }

fun PuzzleInput.part2(): Any {
    var sum = 0L
    var mul = true
    for (instr in Regex("mul\\((\\d+),(\\d+)\\)|do\\(\\)|don't\\(\\)").findAll(string)) {
        when (instr.value) {
            "do()" -> mul = true
            "don't()" -> mul = false
            else -> if (mul) {
                val (_, a, b) = instr.groupValues
                sum += a.toLong() * b.toLong()
            }
        }
    }
    return sum
}

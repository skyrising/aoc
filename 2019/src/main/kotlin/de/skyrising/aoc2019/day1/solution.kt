package de.skyrising.aoc2019.day1

import de.skyrising.aoc.*

@PuzzleName("The Tyranny of the Rocket Equation")
fun PuzzleInput.part1() = lines.sumOf { it.toInt() / 3 - 2 }

fun PuzzleInput.part2() = lines.sumOf {
    var totalFuel = maxOf(it.toInt() / 3 - 2, 0)
    var fuel = totalFuel
    while (fuel > 0) {
        fuel = maxOf(fuel / 3 - 2, 0)
        totalFuel += fuel
    }
    totalFuel
}
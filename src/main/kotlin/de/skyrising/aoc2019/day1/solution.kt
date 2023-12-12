package de.skyrising.aoc2019.day1

import de.skyrising.aoc.BenchmarkBaseV1
import de.skyrising.aoc.part1
import de.skyrising.aoc.part2

@Suppress("unused")
class BenchmarkDay : BenchmarkBaseV1(2019, 1)

@Suppress("unused")
fun register() {
    part1("The Tyranny of the Rocket Equation") {
        lines.sumOf { it.toInt() / 3 - 2 }
    }
    part2 {
        lines.sumOf {
            var totalFuel = maxOf(it.toInt() / 3 - 2, 0)
            var fuel = totalFuel
            while (fuel > 0) {
                fuel = maxOf(fuel / 3 - 2, 0)
                totalFuel += fuel
            }
            totalFuel
        }
    }
}
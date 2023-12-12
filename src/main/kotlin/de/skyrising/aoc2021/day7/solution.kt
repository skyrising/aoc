package de.skyrising.aoc2021.day7

import de.skyrising.aoc.BenchmarkBaseV1
import de.skyrising.aoc.TestInput
import de.skyrising.aoc.part1
import de.skyrising.aoc.part2
import kotlin.math.abs

@Suppress("unused")
class BenchmarkDay : BenchmarkBaseV1(2021, 7)

@Suppress("unused")
fun register() {
    val test = TestInput("16,1,2,0,4,2,7,1,2,14")
    part1("The Treachery of Whales") {
        val crabs = chars.trim().split(',').map(String::toInt).sorted()
        var minFuel = Int.MAX_VALUE
        for (pos in crabs.first()..crabs.last()) {
            var fuel = 0
            for (c in crabs) {
                fuel += abs(c - pos)
            }
            minFuel = minOf(minFuel, fuel)
        }
        minFuel
    }
    part2 {
        val crabs = chars.trim().split(',').map(String::toInt).sorted()
        var minFuel = Int.MAX_VALUE
        for (pos in crabs.first()..crabs.last()) {
            var fuel = 0
            for (c in crabs) {
                val dist = abs(c - pos)
                val cost = (dist * (dist + 1)) / 2
                fuel += cost
            }
            minFuel = minOf(minFuel, fuel)
        }
        minFuel
    }
}
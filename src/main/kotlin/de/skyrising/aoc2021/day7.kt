package de.skyrising.aoc2021

import de.skyrising.aoc.TestInput
import kotlin.math.abs

class BenchmarkDay7 : BenchmarkDayV1(7)

fun registerDay7() {
    val test = TestInput("16,1,2,0,4,2,7,1,2,14")
    puzzle(7, "The Treachery of Whales") {
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
    puzzle(7, "Part Two") {
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
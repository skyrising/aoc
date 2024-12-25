@file:PuzzleName("The Treachery of Whales")

package de.skyrising.aoc2021.day7

import de.skyrising.aoc.PuzzleInput
import de.skyrising.aoc.PuzzleName
import de.skyrising.aoc.TestInput
import kotlin.math.abs

val test = TestInput("16,1,2,0,4,2,7,1,2,14")

fun PuzzleInput.part1(): Any {
    val crabs = chars.trim().split(',').map(String::toInt).sorted()
    var minFuel = Int.MAX_VALUE
    for (pos in crabs.first()..crabs.last()) {
        var fuel = 0
        for (c in crabs) {
            fuel += abs(c - pos)
        }
        minFuel = minOf(minFuel, fuel)
    }
    return minFuel
}

fun PuzzleInput.part2(): Any {
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
    return minFuel
}

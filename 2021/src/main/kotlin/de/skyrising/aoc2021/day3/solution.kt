@file:PuzzleName("Binary Diagnostic")

package de.skyrising.aoc2021.day3

import de.skyrising.aoc.PuzzleInput
import de.skyrising.aoc.PuzzleName
import de.skyrising.aoc.TestInput

val test = TestInput("""
    00100
    11110
    10110
    10111
    10101
    01111
    00111
    11100
    10000
    11001
    00010
    01010
""")

fun PuzzleInput.part1(): Any {
    val input = lines
    val count = IntArray(input[0].length)
    for (line in input) {
        for (i in count.indices) {
            count[i] += (line.toInt(2) shr i) and 1
        }
    }
    var gamma = 0
    for (i in count.indices) {
        if (count[i] * 2 > input.size) gamma += 1 shl i
    }
    return gamma * (gamma.inv() and ((1 shl count.size) - 1))
}

fun getRating(list: MutableList<Int>, bits: Int, invert: Boolean): Int {
    for (i in bits - 1 downTo 0) {
        var count = 0
        for (number in list) {
            count += (number shr i) and 1
        }
        val value = if (count * 2 >= list.size) 1 else 0
        list.removeIf { n -> (((n shr i) and 1) != value) xor invert }
        if (list.size == 1) return list[0]
    }
    return list[0]
}

fun PuzzleInput.part2(): Any {
    val input = lines
    val list = input.mapTo(ArrayList()) { line -> line.toInt(2) }
    val bits = input[0].length
    val oxygen = getRating(ArrayList(list), bits, false)
    val co2 = getRating(list, bits, true)
    return oxygen * co2
}

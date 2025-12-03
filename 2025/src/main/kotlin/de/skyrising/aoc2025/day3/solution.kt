@file:PuzzleName("Lobby")

package de.skyrising.aoc2025.day3

import de.skyrising.aoc.PuzzleInput
import de.skyrising.aoc.PuzzleName
import de.skyrising.aoc.TestInput
import java.nio.ByteBuffer

val test = TestInput("""
987654321111111
811111111111119
234234234234278
818181911112111
""")

fun ByteBuffer.findBestJoltage(len: Int): Long {
    var joltage = 0L
    val data = array()
    var start = arrayOffset()
    val limit = start + limit()
    for (i in 0 ..< len) {
        var d = '0'.code.toByte()
        val end = limit - len + i
        for (j in start..end) {
            val c = data[j]
            if (c > d) {
                d = c
                start = j
            }
        }
        joltage = 10 * joltage + d - 48
        start++
    }
    return joltage
}

fun PuzzleInput.part1() = byteLines.sumOf { it.findBestJoltage(2) }

fun PuzzleInput.part2() = byteLines.sumOf { it.findBestJoltage(12) }
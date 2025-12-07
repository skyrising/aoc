@file:PuzzleName("Laboratories")

package de.skyrising.aoc2025.day7

import de.skyrising.aoc.PuzzleInput
import de.skyrising.aoc.PuzzleName
import de.skyrising.aoc.TestInput
import de.skyrising.aoc.getLongLE

val test = TestInput("""
.......S.......
...............
.......^.......
...............
......^.^......
...............
.....^.^.^.....
...............
....^.^...^....
...............
...^.^...^.^...
...............
..^...^.....^..
...............
.^.^.^.^.^...^.
...............
""")

class Prepared(val part1: Int, val part2: Long)

fun PuzzleInput.prepare(): Prepared {
    val byteLines = byteLines
    val width = byteLines.last().limit()
    val counts = LongArray(width)
    var splitCount = 0
    for (y in byteLines.indices) {
        val line = byteLines[y]
        val arr = line.array()
        val arrOff = line.arrayOffset()
        var x = 1
        while (x < width - 1) {
            // Skip ........ if possible
            val next = minOf(x + 8, width - 1)
            if (x < width - 7 && arr.getLongLE(arrOff + x) == 0x2e2e2e2e2e2e2e2eL) {
                x = next
                continue
            }
            // Non '.' in the next 8 characters
            while (x < next) {
                val c = arr[arrOff + x].toInt()
                if (c != '.'.code) {
                    val cx = counts[x]
                    counts[x - 1] += cx
                    counts[x + 1] += cx
                    counts[x] = (c and 1).toLong()
                    if (cx > 0) splitCount++
                }
                x++
            }
        }
    }
    return Prepared(splitCount, counts.sum())
}

fun Prepared.part1() = part1
fun Prepared.part2() = part2

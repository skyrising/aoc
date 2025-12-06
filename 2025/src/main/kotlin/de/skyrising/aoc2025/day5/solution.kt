@file:PuzzleName("Cafeteria")

package de.skyrising.aoc2025.day5

import de.skyrising.aoc.*
import it.unimi.dsi.fastutil.longs.LongArrays

val test = TestInput("""
3-5
10-14
16-20
12-18

1
5
8
11
17
32
""")

typealias Prepared = Pair<List<LongRange>, LongArray>

fun PuzzleInput.prepare(): Prepared {
    val rangesFrom = LongArray(byteLines.size)
    val rangesTo = LongArray(byteLines.size)
    var i = 0
    while (true) {
        val line = byteLines[i]
        if (!line.hasRemaining()) break
        val split = line.indexOf('-'.code.toByte())
        rangesFrom[i] = line.toLong(0, split)
        rangesTo[i] = line.toLong(split + 1)
        i++
    }
    LongArrays.radixSort(rangesFrom, rangesTo, 0, i)
    val allRanges = ArrayList<LongRange>(i)
    for (j in 0 ..< i) allRanges.add(rangesFrom[j]..rangesTo[j])
    val freshRanges = allRanges.mergeTo(mutableListOf()) { a, b ->
        if (b.first <= a.last + 1) a.first..maxOf(a.last, b.last) else null
    }
    i++
    val items = LongArray(byteLines.size - i)
    var j = 0
    while (i < byteLines.size) {
        items[j++] = byteLines[i++].toLong()
    }
    return freshRanges to items
}

fun Prepared.part1() = second.count(first::binarySearchContains)

fun Prepared.part2() = first.sumOf { it.count() }

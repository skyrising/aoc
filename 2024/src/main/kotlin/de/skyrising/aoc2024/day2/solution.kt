@file:PuzzleName("Red-Nosed Reports")

package de.skyrising.aoc2024.day2

import de.skyrising.aoc.PuzzleInput
import de.skyrising.aoc.PuzzleName
import de.skyrising.aoc.TestInput
import de.skyrising.aoc.ints
import it.unimi.dsi.fastutil.ints.IntList
import kotlin.math.absoluteValue

val test = TestInput("""
7 6 4 2 1
1 2 7 8 9
9 7 6 2 1
1 3 2 4 5
8 6 4 4 1
1 3 6 7 9
""")

fun safe(report: List<Int>, skip: Int = -1): Boolean {
    var allNegative = true
    var allPositive = true
    var i = if (skip == 0) 1 else 0
    var a = report[i]
    while (true) {
        i += if (i + 1 == skip) 2 else 1
        if (i >= report.size) break
        val b = report[i]
        val diff = b - a
        if (diff <= 0) allPositive = false
        if (diff >= 0) allNegative = false
        if (diff.absoluteValue > 3) return false
        a = b
    }
    return allNegative || allPositive
}

fun PuzzleInput.prepare() = lines.map { it.ints() }

fun List<IntList>.part1() = count(::safe)

fun List<IntList>.part2() = count { it.indices.any { i -> safe(it, i) } }

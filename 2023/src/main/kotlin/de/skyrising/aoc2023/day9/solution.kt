@file:PuzzleName("Mirage Maintenance")

package de.skyrising.aoc2023.day9

import de.skyrising.aoc.PuzzleInput
import de.skyrising.aoc.PuzzleName
import de.skyrising.aoc.TestInput
import de.skyrising.aoc.ints
import it.unimi.dsi.fastutil.ints.IntList

inline fun generate(list: IntList, pick: IntList.()->Int): IntList {
    var splitPoint = list.size
    while (true) {
        list[splitPoint - 1] = pick(list.subList(0, splitPoint))
        splitPoint--
        var allZero = true
        for (i in 0 until splitPoint) {
            val diff = list.getInt(i + 1) - list.getInt(i)
            list[i] = diff
            if (diff != 0) allZero = false
        }
        if (allZero) return list.subList(splitPoint, list.size)
    }
}

val test = TestInput("""
    0 3 6 9 12 15
    1 3 6 10 15 21
    10 13 16 21 30 45
""")

fun PuzzleInput.part1() = lines.sumOf {
    generate(it.ints()) { getInt(lastIndex) }.sum()
}

fun PuzzleInput.part2() = lines.sumOf {
    generate(it.ints()) { getInt(0) }.reduce { a, b -> b - a }
}

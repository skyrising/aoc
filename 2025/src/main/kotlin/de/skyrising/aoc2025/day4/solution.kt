@file:PuzzleName("Printing Department")

package de.skyrising.aoc2025.day4

import de.skyrising.aoc.PuzzleInput
import de.skyrising.aoc.PuzzleName
import de.skyrising.aoc.TestInput

val test = TestInput("""
..@@.@@@@.
@@@.@.@.@@
@@@@@.@.@@
@.@@@@..@.
@@.@@@@.@@
.@@@@@@@.@
.@.@.@.@@@
@.@@@.@@@@
.@@@@@@@@.
@.@.@@@.@.
""")

fun PuzzleInput.part1(): Any {
    val g = charGrid
    return g.positions.count {
        g[it] == '@' && it.eightNeighbors().count { n -> n in g && g[n] == '@' } < 4
    }
}

fun PuzzleInput.part2(): Any {
    val g = charGrid.copy()
    var removed = 0
    while (true) {
        var newRemoved = removed
        g.positions.forEach {
            if (g[it] == '@' && it.eightNeighbors().count { n -> n in g && g[n] == '@' } < 4) {
                g[it] = '.'
                newRemoved++
            }
        }
        if (newRemoved == removed) break
        removed = newRemoved
    }
    return removed
}
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

fun PuzzleInput.part1(): Int {
    val g = charGrid
    return g.positions.count {
        g[it] == '@' && it.eightNeighbors().count { n -> n in g && g[n] == '@' } < 4
    }
}

fun PuzzleInput.part2(): Int {
    val g = charGrid
    var removed = 0
    val todo = ArrayDeque(g.where { it == '@' })
    while (todo.isNotEmpty()) {
        val p = todo.removeLast()
        if (p !in g) continue
        if (g[p] == '@' && p.eightNeighbors().count { n -> n in g && g[n] == '@' } < 4) {
            g[p] = '.'
            removed++
            todo.addAll(p.eightNeighbors())
        }
    }
    return removed
}
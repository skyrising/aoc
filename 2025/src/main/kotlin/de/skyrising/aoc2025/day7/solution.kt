@file:PuzzleName("Laboratories")

package de.skyrising.aoc2025.day7

import de.skyrising.aoc.PuzzleInput
import de.skyrising.aoc.PuzzleName
import de.skyrising.aoc.TestInput
import de.skyrising.aoc.Vec2i

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

fun PuzzleInput.part1(): Any {
    val g = charGrid
    val splits = mutableSetOf<Vec2i>()
    val beams = ArrayDeque(g.where { it == 'S' })
    while (beams.isNotEmpty()) {
        val beam = beams.removeLast()
        val x = beam.x
        var y = beam.y
        while (y < g.height) {
            if (g[x, y] == '^') {
                if (splits.add(Vec2i(x, y))) {
                    beams.add(Vec2i(x - 1, y))
                    beams.add(Vec2i(x + 1, y))
                }
                break
            } else {
                g[x, y] = '|'
            }
            y++
        }
    }
    return splits.size
}

fun PuzzleInput.part2(): Any {
    val g = charGrid
    val counts = mutableMapOf<Vec2i, Long>()
    fun getCount(beam: Vec2i): Long {
        if (beam in counts) return counts[beam]!!
        val x = beam.x
        var y = beam.y
        while (y < g.height) {
            if (g[x, y] == '^') {
                return (getCount(Vec2i(x - 1, y)) + getCount(Vec2i(x + 1, y))).also { counts[beam] = it }
            }
            y++
        }
        return 1L.also { counts[beam] = it }
    }
    return getCount(g.where { it == 'S' }.single())
}

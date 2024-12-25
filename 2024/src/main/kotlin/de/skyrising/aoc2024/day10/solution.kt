@file:PuzzleName("Hoof It")

package de.skyrising.aoc2024.day10

import de.skyrising.aoc.*

val test = TestInput("""
89010123
78121874
87430965
96549874
45678903
32019012
01329801
10456732
""")

fun walk(grid: CharGrid, pos: Vec2i, ends: MutableCollection<Vec2i>) {
    val value = grid[pos]
    if (value == '9') {
        ends.add(pos)
        return
    }
    for (n in pos.fourNeighbors()) {
        if (n !in grid || grid[n] != value + 1) continue
        walk(grid, n, ends)
    }
}

fun PuzzleInput.part1(): Any {
    val grid = charGrid
    return grid.where { it == '0' }.sumOf { head ->
        val ends = mutableSetOf<Vec2i>()
        walk(grid, head, ends)
        ends.size
    }
}

fun PuzzleInput.part2(): Any {
    val grid = charGrid
    return grid.where { it == '0' }.sumOf { head ->
        val ends = mutableListOf<Vec2i>()
        walk(grid, head, ends)
        ends.size
    }
}

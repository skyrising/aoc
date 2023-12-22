package de.skyrising.aoc2023.day3

import de.skyrising.aoc.*

val test = TestInput("""
    467..114..
    ...*......
    ..35..633.
    ......#...
    617*......
    .....+.58.
    ..592.....
    ......755.
    ...${'$'}.*....
    .664.598..
""")

fun findNumbers(grid: CharGrid, pos: Vec2i, numbers: MutableMap<Vec2i, Int>) =
    pos.eightNeighbors().mapNotNullTo(mutableSetOf()) { n ->
        if (n !in grid || !grid[n].isDigit()) return@mapNotNullTo null
        val start = grid[0 until n.x, n.y].indexOfLast { !it.isDigit() } + 1
        numbers.getOrPut(Vec2i(start, n.y)) {
            val end = grid[n.x until grid.width, n.y].indexOfFirst { !it.isDigit() } + n.x - 1
            grid[start..maxOf(n.x, end), n.y].toInt()
        }
    }

@PuzzleName("Gear Ratios")
fun PuzzleInput.part1(): Any {
    val grid = charGrid
    val numbers = mutableMapOf<Vec2i, Int>()
    grid.where { !it.isDigit() && it != '.' }.forEach { findNumbers(grid, it, numbers) }
    return numbers.values.sum()
}

fun PuzzleInput.part2(): Any {
    val grid = charGrid
    val numbers = mutableMapOf<Vec2i, Int>()
    return grid.where { it == '*' }.map { findNumbers(grid, it, numbers) }.filter { it.size == 2 }.sumOf { it.reduce(Int::times) }
}

@file:PuzzleName("Ceres Search")

package de.skyrising.aoc2024.day4

import de.skyrising.aoc.*

val test = TestInput("""
MMMSXXMASM
MSAMXMSMSA
AMXSXMAAMM
MSAMASMSMX
XMASAMXAMM
XXAMMXXAMA
SMSMSASXSS
SAXAMASAAA
MAMMMXMMMM
MXMXAXMASX
""")

fun PuzzleInput.part1(): Any {
    val grid = charGrid
    val search = "XMAS"
    return grid.where { it == search[0] }.sumOf { start ->
        listOf(Vec2i.N, Vec2i.NE, Vec2i.E, Vec2i.SE, Vec2i.S, Vec2i.SW, Vec2i.W, Vec2i.NW).count { dir ->
            start.ray(dir, search.length).allIndexed { index, pos -> pos in grid && grid[pos] == search[index] }
        }
    }
}

fun PuzzleInput.part2(): Any {
    val grid = charGrid
    return grid.where { it == 'A' }.count {
        it.northWest in grid && it.southEast in grid &&
        (grid[it.northWest] == 'M' && grid[it.southEast] == 'S' || grid[it.northWest] == 'S' && grid[it.southEast] == 'M') &&
        (grid[it.southWest] == 'M' && grid[it.northEast] == 'S' || grid[it.southWest] == 'S' && grid[it.northEast] == 'M')
    }
}

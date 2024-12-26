@file:PuzzleName("Ceres Search")

package de.skyrising.aoc2024.day4

import de.skyrising.aoc.PuzzleInput
import de.skyrising.aoc.PuzzleName
import de.skyrising.aoc.TestInput

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
        val startX = start.x
        val startY = start.y
        var count = 0
        for (yOff in -1..1) {
            val yS = startY + yOff * 3
            for (xOff in -1..1) {
                if (xOff == 0 && yOff == 0) continue
                val xS = startX + xOff * 3
                if (!grid.contains(xS, yS)) continue
                if (grid[xS, yS] != 'S') continue
                if (grid[startX + xOff * 2, startY + yOff * 2] != 'A') continue
                if (grid[startX + xOff, startY + yOff] != 'M') continue
                count++
            }
        }
        count
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

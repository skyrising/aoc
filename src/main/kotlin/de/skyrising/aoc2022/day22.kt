package de.skyrising.aoc2022

import de.skyrising.aoc.CharGrid
import de.skyrising.aoc.PuzzleInput
import de.skyrising.aoc.TestInput
import de.skyrising.aoc.Vec2i

class BenchmarkDay22 : BenchmarkDayV1(22)

private fun parseInput(input: PuzzleInput): Pair<CharGrid, List<String>> {
    val gridLines = input.lines.subList(0, input.lines.size - 2)
    val width = gridLines.maxOf { it.length }
    val height = gridLines.size
    val grid = CharGrid(width, height, CharArray(width * height))
    for (row in 0 until height) {
        val line = gridLines[row]
        for (col in 0 until width) {
            grid[col, row] = if (col < line.length) line[col] else ' '
        }
    }
    val path = Regex("\\d+|R|L").findAll(input.lines.last()).map { it.value }.toList()
    return grid to path
}

private fun nextTile(grid: CharGrid, pos: Vec2i, dir: Vec2i): Vec2i {
    var p = pos
    do {
        p = (p + dir + grid.size) % grid.size
    } while (grid[p] == ' ')
    return if (grid[p] == '.') p else pos
}

private fun move(grid: CharGrid, pos: Vec2i, facing: Int, step: String) = when(step) {
    "R" -> pos to ((facing + 1) and 3)
    "L" -> pos to ((facing + 3) and 3)
    else -> {
        val steps = step.toInt()
        val dir = when (facing) {
            0 -> Vec2i(1, 0)
            1 -> Vec2i(0, 1)
            2 -> Vec2i(-1, 0)
            3 -> Vec2i(0, -1)
            else -> error("Invalid facing: $facing")
        }
        var pos = pos
        for (i in 0 until steps) {
            pos = nextTile(grid, pos, dir)
        }
        pos to facing
    }
}

fun registerDay22() {
    val test = TestInput("""
                ...#
                .#..
                #...
                ....
        ...#.......#
        ........#...
        ..#....#....
        ..........#.
                ...#....
                .....#..
                .#......
                ......#.
        
        10R5L5R10L4R5L5
    """)
    puzzle(22, "Monkey Map") {
        val (grid, path) = parseInput(this)
        var pos = grid.where { it == '.' }.first()
        var facing = 0
        for (step in path) {
            val (newPos, newFacing) = move(grid, pos, facing, step)
            pos = newPos
            facing = newFacing
        }
        pos.y * 1000 + pos.x * 4 + facing + 1004
    }
    puzzle(22, "Part Two") {
    }
}
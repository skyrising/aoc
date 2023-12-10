package de.skyrising.aoc2023

import de.skyrising.aoc.*

@Suppress("unused")
class BenchmarkDay10 : BenchmarkDayV1(10)

@Suppress("unused")
fun registerDay10() {
    val test = TestInput(
        """
        ..........
        .S------7.
        .|F----7|.
        .||OOOO||.
        .||OOOO||.
        .|L-7F-J|.
        .|II||II|.
        .L--JL--J.
        ..........
    """
    )

    fun dirs(c: Char) = when (c) {
        '|' -> listOf(Vec2i.N, Vec2i.S)
        '-' -> listOf(Vec2i.W, Vec2i.E)
        'L' -> listOf(Vec2i.N, Vec2i.E)
        'J' -> listOf(Vec2i.N, Vec2i.W)
        '7' -> listOf(Vec2i.S, Vec2i.W)
        'F' -> listOf(Vec2i.S, Vec2i.E)
        'S' -> listOf(Vec2i.N, Vec2i.S, Vec2i.W, Vec2i.E)
        else -> emptyList()
    }

    fun goAlong(c: Char, move: Vec2i) = when (c) {
        '|' -> if (move.y > 0) Vec2i.S else Vec2i.N
        '-' -> if (move.x > 0) Vec2i.E else Vec2i.W
        'L' -> if (move.y == 0) Vec2i.N else Vec2i.E
        'J' -> if (move.y == 0) Vec2i.N else Vec2i.W
        '7' -> if (move.y == 0) Vec2i.S else Vec2i.W
        'F' -> if (move.y == 0) Vec2i.S else Vec2i.E
        else -> throw IllegalStateException()
    }

    fun walk(input: PuzzleInput): List<Vec2i> {
        val grid = input.charGrid
        val start = grid.where { it == 'S' }.first()
        val loops = mutableListOf<List<Vec2i>>()
        for (n in start.fourNeighbors()) {
            if (n !in grid || grid[n] == '.') continue
            val loop = mutableListOf(start)
            var pos = n
            var move = n - start
            while (pos != start) {
                loop.add(pos)
                move = goAlong(grid[pos], move)
                pos += move
            }
            loops.add(loop)
        }
        return loops.maxByOrNull { it.size } ?: throw IllegalStateException()
    }
    part1("Pipe Maze") {
        val loop = walk(this)
        loop.size / 2
    }
    part2 {
        val origGrid = charGrid
        val loop = walk(this)
        val bb = loop.boundingBox().expand(Vec2i.ZERO).expand(1)
        val scaledBB = BoundingBox2i(bb.min * 3 - 1, bb.max * 3 + 1)
        val grid = scaledBB.charGrid { ' ' }
        for (point in loop) {
            val offset = point * 3 + 1
            grid[offset] = '#'
            for (dir in dirs(origGrid[point])) grid[offset + dir] = '#'
        }
        grid.floodFill(grid.offset, '.')
        origGrid.positions.count { grid[it * 3 + 1] == ' ' }
    }
}
package de.skyrising.aoc2023

import de.skyrising.aoc.*
import it.unimi.dsi.fastutil.chars.CharArrayList
import kotlin.math.absoluteValue

@Suppress("unused")
class BenchmarkDay10 : BenchmarkDayV1(10)

private inline fun Vec2i.scaled() = this * 3 + 1

private inline fun goAlong(c: Char, move: Vec2i) = when (c) {
    '|' -> if (move.y > 0) Vec2i.S else Vec2i.N
    '-' -> if (move.x > 0) Vec2i.E else Vec2i.W
    'L' -> if (move.y == 0) Vec2i.N else Vec2i.E
    'J' -> if (move.y == 0) Vec2i.N else Vec2i.W
    '7' -> if (move.y == 0) Vec2i.S else Vec2i.W
    'F' -> if (move.y == 0) Vec2i.S else Vec2i.E
    else -> throw IllegalStateException()
}

private fun startDirection(grid: CharGrid, pos: Vec2i): Vec2i {
    if (pos.y > 0) {
        val c = grid[pos.x, pos.y - 1]
        if (c == '|' || c == '7' || c == 'F') return Vec2i.N
    }
    if (pos.y < grid.height - 1) {
        val c = grid[pos.x, pos.y + 1]
        if (c == '|' || c == 'L' || c == 'J') return Vec2i.S
    }
    if (pos.x > 0) {
        val c = grid[pos.x - 1, pos.y]
        if (c == '-' || c == 'F' || c == 'L') return Vec2i.W
    }
    if (pos.x < grid.width - 1) {
        val c = grid[pos.x + 1, pos.y]
        if (c == '-' || c == '7' || c == 'J') return Vec2i.E
    }
    throw IllegalStateException()
}

private inline fun walk(input: PuzzleInput, consumer: (Vec2i, Char) -> Unit) {
    val grid = input.charGrid
    val start = grid.indexOfFirst { it == 'S' }
    consumer(start, 'S')
    var move = startDirection(grid, start)
    var pos = start + move
    while (pos != start) {
        consumer(pos, grid[pos])
        move = goAlong(grid[pos], move)
        pos += move
    }
}

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

    part1("Pipe Maze") {
        var length = 0
        walk(this) { _, _ ->
            length++
        }
        length / 2
    }

    part2 {
        val input = this
        val loop = mutableListOf<Vec2i>()
        val chars = CharArrayList()
        walk(input) { p, c ->
            loop.add(p)
            chars.add(c)
        }
        val bb = loop.boundingBox().expand(1)
        val scaledBB = BoundingBox2i(bb.min.scaled(), bb.max.scaled())
        val grid = scaledBB.charGrid { ' ' }
        for (i in 0 until loop.size) {
            val offset = loop[i].scaled()
            grid[offset] = '#'
            val c = chars.getChar(i)
            if (c == 'S' || c == '|' || c == 'L' || c == 'J') grid[offset.x, offset.y - 1] = '#'
            if (c == 'S' || c == '|' || c == '7' || c == 'F') grid[offset.x, offset.y + 1] = '#'
            if (c == 'S' || c == '-' || c == '7' || c == 'J') grid[offset.x - 1, offset.y] = '#'
            if (c == 'S' || c == '-' || c == 'F' || c == 'L') grid[offset.x + 1, offset.y] = '#'
        }
        grid.floodFill(grid.offset, '.')
        input.charGrid.positions.map { it.scaled() }.count { it in grid && grid[it] == ' ' }
    }

    part2 {
        var firstCorner: Vec2i? = null
        var a: Vec2i?
        var b: Vec2i? = null
        var area = 0
        var perimeter = 0
        walk(this) { p, c ->
            perimeter++
            if (c in "7JLF") {
                a = b
                b = p
                if (firstCorner == null) firstCorner = p
                if (a != null) {
                    area += a!!.x * b!!.y - a!!.y * b!!.x
                }
            }
        }
        if (b != null) {
            area += b!!.x * firstCorner!!.y - b!!.y * firstCorner!!.x
        }
        area = area.absoluteValue / 2
        area - perimeter / 2 + 1
    }
}
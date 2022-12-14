package de.skyrising.aoc2022

import de.skyrising.aoc.CharGrid
import de.skyrising.aoc.Vec2i
import de.skyrising.aoc.boundingBox
import de.skyrising.aoc.ints

class BenchmarkDay14 : BenchmarkDayV1(14)

private fun parseInput(input: List<String>, floor: Boolean = false): Pair<Vec2i, CharGrid> {
    val lines = mutableListOf<Pair<Vec2i, Vec2i>>()
    val points = mutableSetOf(Vec2i(500, 0))
    for (line in input) {
        val parts = line.split(" -> ")
        val (fromX, fromY) = parts[0].ints()
        var from = Vec2i(fromX, fromY)
        points.add(from)
        for (i in 1 until parts.size) {
            val (toX, toY) = parts[i].ints()
            val to = Vec2i(toX, toY)
            points.add(to)
            lines += from to to
            from = to
        }
    }
    var (min, max) = points.boundingBox()
    if (floor) {
        min = min.copy(x = minOf(min.x, 500 - max.y - 3))
        max = max.copy(x = maxOf(max.x, 500 + max.y + 3), y = max.y + 2)
    }
    val size = max - min + Vec2i(1, 1)
    val data = CharArray(size.x * size.y) { '.' }
    val grid = CharGrid(size.x, size.y, data)
    for (line in lines) {
        val (from, to) = line
        val dir = when {
            to.x > from.x -> Vec2i(1, 0)
            to.x < from.x -> Vec2i(-1, 0)
            to.y > from.y -> Vec2i(0, 1)
            to.y < from.y -> Vec2i(0, -1)
            else -> error("Invalid line $line")
        }
        var pos = from
        while (pos != to) {
            grid[pos - min] = '#'
            pos += dir
        }
        grid[pos - min] = '#'
    }
    if (floor) {
        for (x in 0 until size.x) {
            grid[x, size.y - 1] = '#'
        }
    }
    return min to grid
}

private fun CharGrid.dropSand(pos: Vec2i): Vec2i {
    var curPos = pos
    outer@while (true) {
        for (below in arrayOf(Vec2i(curPos.x, curPos.y + 1), Vec2i(curPos.x - 1, curPos.y + 1), Vec2i(curPos.x + 1, curPos.y + 1))) {
            if (below.y >= height) return Vec2i(curPos.x, Int.MAX_VALUE)
            if (this[below] == '.') {
                curPos = below
                continue@outer
            }
        }
        return curPos
    }
}

fun registerDay14() {
    val test = """
        498,4 -> 498,6 -> 496,6
        503,4 -> 502,4 -> 502,9 -> 494,9
    """.trimIndent().lines().filter(String::isNotBlank)
    puzzleLS(14, "Regolith Reservoir") {
        val (origin, grid) = parseInput(it)
        var dropped = 0
        while (true) {
            val pos = grid.dropSand(Vec2i(500, 0) - origin)
            if (pos.y >= grid.height) break
            grid[pos] = (dropped % 10 + '0'.code).toChar()
            dropped++
        }
        dropped
    }
    puzzleLS(14, "Part Two") {
        val (origin, grid) = parseInput(it, true)
        var dropped = 0
        while (true) {
            dropped++
            val pos = grid.dropSand(Vec2i(500, 0) - origin)
            grid[pos] = (dropped % 10 + '0'.code).toChar()
            if (pos.y == 0) break
        }
        dropped
    }
}
package de.skyrising.aoc2022

import de.skyrising.aoc.Graph
import de.skyrising.aoc.IntGrid
import de.skyrising.aoc.Vec2i

class BenchmarkDay12 : BenchmarkDayV1(12)

private fun parseInput(input: List<String>): Triple<IntGrid, Vec2i, Vec2i> {
    val height = input.size
    val width = input[0].length
    val data = IntArray(width * height)
    val grid = IntGrid(width, height, data)
    var start: Vec2i? = null
    var end: Vec2i? = null
    for (y in 0 until height) {
        val line = input[y]
        for (x in 0 until width) {
            grid[x, y] = when (line[x]) {
                'S' -> {
                    start = Vec2i(x, y)
                    0
                }
                'E' -> {
                    end = Vec2i(x, y)
                    25
                }
                else -> line[x].code - 'a'.code
            }
        }
    }
    return Triple(grid, start!!, end!!)
}

fun registerDay12() {
    puzzleLS(12, "Hill Climbing Algorithm") {
        val g = Graph<Vec2i, Nothing>()
        val (grid, start, end) = parseInput(it)
        grid.forEach { x, y, i ->
            for (n in Vec2i(x, y).fourNeighbors()) {
                if (grid.contains(n) && grid[n] <= i + 1) {
                    g.edge(Vec2i(x, y), n, 1)
                }
            }
        }
        val path = g.dijkstra(g.vertex(start), g.vertex(end)) ?: return@puzzleLS -1
        path.size
    }
    puzzleLS(12, "Part Two") {
        val g = Graph<Vec2i, Nothing>()
        val (grid, _, end) = parseInput(it)
        grid.forEach { x, y, i ->
            for (n in Vec2i(x, y).fourNeighbors()) {
                if (grid.contains(n) && grid[n] <= i + 1) {
                    g.edge(Vec2i(x, y), n, 1)
                }
            }
        }
        var lowest = Int.MAX_VALUE
        for (v in g.getVertexes()) {
            if (grid[v.value] != 0) continue
            val path = g.dijkstra(g.vertex(v), g.vertex(end))
            if (path != null) {
                lowest = minOf(lowest, path.size)
            }
        }
        lowest
    }
}
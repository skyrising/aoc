package de.skyrising.aoc2022.day12

import de.skyrising.aoc.*

@Suppress("unused")
class BenchmarkDay : BenchmarkBaseV1(2022, 12)

private fun parseInput(input: PuzzleInput): Triple<IntGrid, Vec2i, Vec2i> {
    val height = input.byteLines.size
    val width = input.byteLines[0].remaining()
    val data = IntArray(width * height)
    val grid = IntGrid(width, height, data)
    var start: Vec2i? = null
    var end: Vec2i? = null
    for (y in 0 until height) {
        val line = input.byteLines[y]
        for (x in 0 until width) {
            grid[x, y] = when (line[x].toInt().toChar()) {
                'S' -> {
                    start = Vec2i(x, y)
                    0
                }
                'E' -> {
                    end = Vec2i(x, y)
                    25
                }
                else -> line[x] - 'a'.code
            }
        }
    }
    return Triple(grid, start!!, end!!)
}

@Suppress("unused")
fun register() {
    part1("Hill Climbing Algorithm") {
        val g = Graph<Vec2i, Nothing>()
        val (grid, start, end) = parseInput(this)
        grid.forEach { x, y, i ->
            for (n in Vec2i(x, y).fourNeighbors()) {
                if (grid.contains(n) && grid[n] <= i + 1) {
                    g.edge(Vec2i(x, y), n, 1)
                }
            }
        }
        val path = g.dijkstra(start, end) ?: return@part1 -1
        path.size
    }
    part2 {
        val g = Graph<Vec2i, Nothing>()
        val (grid, _, end) = parseInput(this)
        grid.forEach { x, y, i ->
            for (n in Vec2i(x, y).fourNeighbors()) {
                if (grid.contains(n) && grid[n] <= i + 1) {
                    g.edge(n, Vec2i(x, y), 1)
                }
            }
        }
        val ends = g.getVertexes().filterTo(mutableSetOf()) { v -> grid[v] == 0 }
        val path = g.dijkstra(end, ends::contains) ?: return@part2 -1
        path.size
    }
}
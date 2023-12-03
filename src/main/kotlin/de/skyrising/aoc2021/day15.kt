package de.skyrising.aoc2021

import de.skyrising.aoc.Graph
import de.skyrising.aoc.TestInput
import de.skyrising.aoc.part1
import de.skyrising.aoc.part2

@Suppress("unused")
class BenchmarkDay15 : BenchmarkDayV1(15)

@Suppress("unused")
fun registerDay15() {
    val test = TestInput("""
        1163751742
        1381373672
        2136511328
        3694931569
        7463417111
        1319128137
        1359912421
        3125421639
        1293138521
        2311944581
    """)
    part1("Chiton") {
        val input = lines
        val g = Graph.build<Pair<Int, Int>, Int> {
            for (y in input.indices) {
                val line = input[y]
                for (x in line.indices) {
                    val value = line[x].digitToInt()
                    if (y > 0) edge(Pair(x, y - 1), Pair(x, y), value)
                    if (x > 0) edge(Pair(x - 1, y), Pair(x, y), value)
                }
            }
        }
        val path = g.dijkstra(g[Pair(0, 0)]!!, g[Pair(input.lastIndex, input.last().lastIndex)]!!)
        path?.sumOf { e -> e.weight }
    }
    part2 {
        val input = lines
        val width = input.last().length
        val height = input.size
        val map = Array(height * 5) { IntArray(width * 5) }
        for (y in input.indices) {
            val line = input[y]
            for (x in line.indices) {
                val value = line[x].digitToInt()
                for (i in 0..4) {
                    for (j in 0..4) {
                        val y1 = i * height + y
                        val x1 = j * width + x
                        val offset = i + j
                        val v1 = 1 + ((value - 1) + offset) % 9
                        map[y1][x1] = v1
                    }
                }
            }
        }
        val dist = Array(height * 5) { IntArray(width * 5) }
        val unvisited = ArrayDeque<Pair<Int, Int>>()
        unvisited.add(Pair(0, 1))
        unvisited.add(Pair(1, 0))
        while (unvisited.isNotEmpty()) {
            val (x, y) = unvisited.removeFirst()
            var top = if (y > 0) dist[y - 1][x] else 0
            var left = if (x > 0) dist[y][x - 1] else 0
            var bottom = if (y < dist.size - 1) dist[y + 1][x] else 0
            var right = if (x < dist.size - 1) dist[y][x + 1] else 0
            if (top == 0 && (y != 0 || x != 1)) top = Int.MAX_VALUE
            if (left == 0 && (x != 0 || y != 1)) left = Int.MAX_VALUE
            if (bottom == 0) bottom = Int.MAX_VALUE
            if (right == 0) right = Int.MAX_VALUE
            val newValue = minOf(minOf(top, bottom), minOf(left, right)) + map[y][x]
            if (dist[y][x] == 0 || newValue < dist[y][x]) {
                dist[y][x] = newValue
                if (y > 0) unvisited.add(Pair(x, y - 1))
                if (x > 0) unvisited.add(Pair(x - 1, y))
                if (y < dist.size - 1) unvisited.add(Pair(x, y + 1))
                if (x < dist.size - 1) unvisited.add(Pair(x + 1, y))
            }
        }
        dist.last().last()
    }
}
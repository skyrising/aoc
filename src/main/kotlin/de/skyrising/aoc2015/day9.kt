package de.skyrising.aoc2015

import de.skyrising.aoc.Graph

class BenchmarkDay9 : BenchmarkDayV1(9)

fun registerDay9() {
    puzzleLS(9, "All in a Single Night") {
        val test = listOf("London to Dublin = 464", "London to Belfast = 518", "Dublin to Belfast = 141")
        val g = Graph<String, Nothing>()
        for (line in it) {
            val (from, _, to, _, dist) = line.split(' ')
            g.edge(from, to, dist.toInt())
            g.edge(to, from, dist.toInt())
        }
        val cities = g.getVertexes()
        val dummy = g.vertex("__DUMMY__")
        for (c in cities) {
            g.edge(c, dummy, 0)
            g.edge(dummy, c, 0)
        }
        g.tsp()?.sumOf { e -> e.weight }
    }
    puzzleLS(9, "Part Two") {
        val g = Graph<String, Nothing>()
        for (line in it) {
            val (from, _, to, _, dist) = line.split(' ')
            g.edge(from, to, -dist.toInt())
            g.edge(to, from, -dist.toInt())
        }
        val cities = g.getVertexes()
        val dummy = g.vertex("__DUMMY__")
        for (c in cities) {
            g.edge(c, dummy, 0)
            g.edge(dummy, c, 0)
        }
        g.tsp()?.sumOf { e -> -e.weight }
    }
}
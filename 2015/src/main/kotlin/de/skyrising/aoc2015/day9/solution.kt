package de.skyrising.aoc2015.day9

import de.skyrising.aoc.*

@Suppress("unused")
class BenchmarkDay : BenchmarkBaseV1(2015, 9)

@Suppress("unused")
fun register() {
    val test = TestInput("""
        London to Dublin = 464
        London to Belfast = 518
        Dublin to Belfast = 141
    """)
    part1("All in a Single Night") {
        val g = Graph<String, Nothing>()
        for (line in lines) {
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
    part2 {
        val g = Graph<String, Nothing>()
        for (line in lines) {
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
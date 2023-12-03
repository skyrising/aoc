package de.skyrising.aoc2015

import de.skyrising.aoc.Graph
import de.skyrising.aoc.TestInput
import de.skyrising.aoc.part1
import de.skyrising.aoc.part2

@Suppress("unused")
class BenchmarkDay9 : BenchmarkDayV1(9)

@Suppress("unused")
fun registerDay9() {
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
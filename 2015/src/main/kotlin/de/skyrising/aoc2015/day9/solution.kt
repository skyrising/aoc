package de.skyrising.aoc2015.day9

import de.skyrising.aoc.*

val test = TestInput("""
    London to Dublin = 464
    London to Belfast = 518
    Dublin to Belfast = 141
""")

@PuzzleName("All in a Single Night")
fun PuzzleInput.part1(): Any? {
    val g = Graph<String, Nothing>()
    for (line in lines) {
        val (from, _, to, _, dist) = line.split(' ')
        g.edge(from, to, dist.toInt())
        g.edge(to, from, dist.toInt())
    }
    val cities = g.getVertexes()
    for (c in cities) {
        g.edge(c, "__DUMMY__", 0)
        g.edge("__DUMMY__", c, 0)
    }
    return g.tsp()?.sumOf { e -> e.weight }
}

fun PuzzleInput.part2(): Any? {
    val g = Graph<String, Nothing>()
    for (line in lines) {
        val (from, _, to, _, dist) = line.split(' ')
        g.edge(from, to, -dist.toInt())
        g.edge(to, from, -dist.toInt())
    }
    val cities = g.getVertexes()
    for (c in cities) {
        g.edge(c, "__DUMMY__", 0)
        g.edge("__DUMMY__", c, 0)
    }
    return g.tsp()?.sumOf { e -> -e.weight }
}
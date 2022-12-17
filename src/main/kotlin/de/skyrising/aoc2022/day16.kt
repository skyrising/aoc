package de.skyrising.aoc2022

import de.skyrising.aoc.*
import de.skyrising.aoc2021.pairs
import it.unimi.dsi.fastutil.objects.*

class BenchmarkDay16 : BenchmarkDayV1(16)

private fun parseInput(input: PuzzleInput): Triple<Object2IntMap<String>, Object2IntMap<Pair<String, String>>, Set<String>> {
    val graph = Graph<String, Nothing>()
    val flows = Object2IntOpenHashMap<String>()
    for (line in input.lines) {
        val (_, name, rate, next) = Regex("Valve (..) has flow rate=(\\d+); tunnels? leads? to valves? (.*)").matchEntire(line)!!.groupValues
        flows[name] = rate.toInt()
        for (nextName in next.split(", ")) {
            graph.edge(name, nextName, 1)
        }
    }
    val paths = Object2IntOpenHashMap<Pair<String, String>>()
    for ((a, b) in graph.getVertexes().toList().pairs()) {
        paths[a.value to b.value] = (graph.dijkstra(a, b)?:continue).size
    }
    val closed = graph.getVertexes().filter { flows.getInt(it.value) != 0 }.map(Vertex<String>::value).toSet()
    return Triple(flows, paths, closed)
}

private fun findBestValve(paths: Object2IntMap<Pair<String, String>>, flows: Object2IntMap<String>, currentPos: String, currentTime: Int, closed: Set<String>, totalTime: Int): Int {
    return closed.maxOfOrNull {
        val timeThere = currentTime + paths.getInt(currentPos to it) + 1
        if (timeThere >= totalTime) {
            0
        } else {
            val flow = (totalTime - timeThere) * flows.getInt(it)
            flow + findBestValve(paths, flows, it, timeThere, closed - it, totalTime)
        }
    } ?: 0
}

fun registerDay16() {
    val test = TestInput("""
        Valve AA has flow rate=0; tunnels lead to valves DD, II, BB
        Valve BB has flow rate=13; tunnels lead to valves CC, AA
        Valve CC has flow rate=2; tunnels lead to valves DD, BB
        Valve DD has flow rate=20; tunnels lead to valves CC, AA, EE
        Valve EE has flow rate=3; tunnels lead to valves FF, DD
        Valve FF has flow rate=0; tunnels lead to valves EE, GG
        Valve GG has flow rate=0; tunnels lead to valves FF, HH
        Valve HH has flow rate=22; tunnel leads to valve GG
        Valve II has flow rate=0; tunnels lead to valves AA, JJ
        Valve JJ has flow rate=21; tunnel leads to valve II
    """)
    puzzle(16, "Probosciedea Volcanium") {
        val (flows, paths, closed) = parseInput(this)
        findBestValve(paths, flows, "AA", 0, closed, 30)
    }
    puzzle(16, "Part Two") {
        val (flows, paths, closed) = parseInput(this)
        closed.subsets().maxOf { subset ->
            val myResult = findBestValve(paths, flows, "AA", 0, subset, 26)
            val elephantResult = findBestValve(paths, flows, "AA", 0, closed - subset, 26)
            myResult + elephantResult
        }
    }
}
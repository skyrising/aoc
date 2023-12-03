package de.skyrising.aoc2020

import de.skyrising.aoc.Graph
import de.skyrising.aoc.Vertex
import de.skyrising.aoc.part1
import de.skyrising.aoc.part2

@Suppress("unused")
class BenchmarkDay7 : BenchmarkDay(7)

private fun readMap(lines: List<String>): Map<String, Set<Pair<String, Int>>> {
    val map = mutableMapOf<String, MutableSet<Pair<String, Int>>>()
    for (line in lines) {
        val (bags, contain) = line.split(" bags contain ")
        if (contain.startsWith("no other")) continue
        val containList = contain.split(", ")
        val set = map.computeIfAbsent(bags) { mutableSetOf() }
        for (containItem in containList) {
            val space = containItem.indexOf(' ')
            val num = containItem.substring(0, space)
            val type = containItem.substring(space + 1, containItem.indexOf(" bag"))
            set.add(Pair(type, num.toInt()))
        }
    }
    return map
}

private fun readGraph(lines: List<String>) = Graph.build<String, Nothing?> {
    for (line in lines) {
        val (bags, contain) = line.split(" bags contain ")
        if (contain.startsWith("no other")) continue
        val containList = contain.split(", ")
        for (containItem in containList) {
            val space = containItem.indexOf(' ')
            val num = containItem.substring(0, space)
            val type = containItem.substring(space + 1, containItem.indexOf(" bag"))
            edge(bags, type, num.toInt(), null)
        }
    }
}

@Suppress("unused")
fun registerDay7() {
    part1("Handy Haversacks") {
        val set = mutableSetOf<String>()
        var count = 0
        var lastCount = -1
        while (count > lastCount) {
            for (line in lines) {
                val (bags, contain) = line.split(" bags contain ")
                if (contain.startsWith("no other")) continue
                val containList = contain.split(", ")
                for (containItem in containList) {
                    val space = containItem.indexOf(' ')
                    //val num = containItem.substring(0, space)
                    val type = containItem.substring(space + 1, containItem.indexOf(" bag"))
                    if (type == "shiny gold" || set.contains(type)) {
                        set.add(bags)
                    }
                }
            }
            lastCount = count
            count = set.size
        }
        count
    }
    part1("Handy Haversacks") {
        val graph = readGraph(lines)
        val set = mutableSetOf("shiny gold")
        var count = 0
        while (set.size > count) {
            count = set.size
            val newSet = mutableSetOf<String>()
            for (v in set) {
                for ((from, _, weight, _) in graph.getIncoming(v)) {
                    newSet.add(from.value)
                }
            }
            set.addAll(newSet)
        }
        count - 1
    }
    part2 {
        val map = readMap(lines)
        fun getContained(type: String): Int {
            val set = map[type] ?: return 1
            var sum = 1
            for ((contained, num) in set) {
                sum += num * getContained(contained)
            }
            return sum
        }
        getContained("shiny gold") - 1
    }
    part2 {
        val graph = readGraph(lines)
        fun getContained(type: Vertex<String>): Int {
            var sum = 1
            for ((_, contained, num, _) in graph.getOutgoing(type)) {
                sum += num * getContained(contained)
            }
            return sum
        }
        getContained(Vertex("shiny gold")) - 1
    }
}
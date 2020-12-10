package de.skyrising.aoc2020

import java.util.*
import kotlin.collections.HashSet

class Graph<V, E> {
    private val vertexes = mutableMapOf<V, Vertex<V>>()
    private val outgoing = mutableMapOf<Vertex<V>, MutableSet<Edge<V, E?>>>()
    private val incoming = mutableMapOf<Vertex<V>, MutableSet<Edge<V, E?>>>()

    val size: Int get() = vertexes.size

    fun vertex(value: V) = vertex(Vertex(value))
    fun vertex(v: Vertex<V>): Vertex<V> {
        vertexes[v.value] = v
        return v
    }

    fun edge(from: V, to: V, weight: Int, value: E? = null) = edge(
        vertexes.computeIfAbsent(from, ::Vertex),
        vertexes.computeIfAbsent(to, ::Vertex),
        weight,
        value
    )
    fun edge(from: Vertex<V>, to: Vertex<V>, weight: Int, value: E? = null) = edge(Edge(from, to, weight, value))
    fun edge(e: Edge<V, E?>): Edge<V, E?> {
        vertex(e.from)
        vertex(e.to)
        outgoing.computeIfAbsent(e.from) { mutableSetOf() }.add(e)
        incoming.computeIfAbsent(e.to) { mutableSetOf() }.add(e)
        return e
    }

    operator fun get(v: V) = vertexes[v]

    fun getOutgoing(v: V) = getOutgoing(vertexes[v] ?: Vertex(v))
    fun getOutgoing(v: Vertex<V>): Set<Edge<V, E?>> = outgoing[v] ?: emptySet()

    fun getIncoming(v: V) = getIncoming(vertexes[v] ?: Vertex(v))
    fun getIncoming(v: Vertex<V>): Set<Edge<V, E?>> = incoming[v] ?: emptySet()

    fun dijkstra(from: Vertex<V>, to: Vertex<V>): List<Edge<V, E?>>? {
        val unvisited = HashSet(vertexes.values)
        val inc = mutableMapOf<Vertex<V>, Edge<V, E?>>()
        val dist = mutableMapOf<Vertex<V>, Int>()
        dist[from] = 0
        var steps = 0
        while (unvisited.isNotEmpty()) {
            steps++
            val current = lowest(unvisited, dist)!!
            if (current == to || current !in unvisited) break
            val curDist = dist[current]!!
            unvisited.remove(current)
            for (e in getOutgoing(current)) {
                val v = e.to
                if (!unvisited.contains(v)) continue
                val alt = curDist + e.weight
                if (alt < dist[v] ?: Integer.MAX_VALUE) {
                    dist[v] = alt
                    inc[v] = e
                }
            }
        }
        // println("$steps steps")
        return buildPath(from, to, inc)
    }

    fun countPaths(from: Vertex<V>, to: Vertex<V>) = countPaths(from, to, mutableMapOf())

    private fun countPaths(from: Vertex<V>, to: Vertex<V>, cache: MutableMap<Vertex<V>, Long>): Long {
        if (from == to) return 1
        val known = cache[to]
        if (known != null) return known
        var count = 0L
        for ((n, _, _, _) in getIncoming(to)) {
            val x = countPaths(from, n, cache)
            count += x
            // println("${from.value} to ${to.value} via ${n.value}: $x")
        }
        cache[to] = count
        // println("${from.value} to ${to.value}: $count")
        return count
    }

    override fun toString(): String {
        val sb = StringBuilder()
        for (v in vertexes.values) {
            val out: Set<Edge<V, E?>> = outgoing[v] ?: emptySet()
            if (out.isEmpty()) {
                sb.append(v.value).append('\n')
            } else {
                for (e in out) {
                    sb.append(e).append('\n')
                }
            }
        }
        return sb.toString()
    }

    companion object {
        inline fun <V, E> build(init: Graph<V, E>.() -> Unit): Graph<V, E> {
            val graph = Graph<V, E>()
            init(graph)
            return graph
        }
    }
}

private fun <V> lowest(unvisited: Collection<Vertex<V>>, map: Map<Vertex<V>, Int>): Vertex<V>? {
    var lowest: Vertex<V>? = null
    for (v in unvisited) {
        if (lowest == null || map[v] ?: Integer.MAX_VALUE < map[lowest] ?: Integer.MAX_VALUE) {
            lowest = v
        }
    }
    return lowest
}

private fun <V, E> buildPath(from: Vertex<V>, to: Vertex<V>, inc: Map<Vertex<V>, Edge<V, E>>): List<Edge<V, E>>? {
    val first = inc[to] ?: return null
    val path = LinkedList<Edge<V, E>>()
    path.add(first)
    var edge: Edge<V, E>? = first
    while (true) {
        val eFrom = edge!!.from
        if (eFrom == from) return path
        edge = inc[eFrom]
        if (edge == null) return null
        path.addFirst(edge)
    }
}

data class Vertex<V>(val value: V)
data class Edge<V, E>(val from: Vertex<V>, val to: Vertex<V>, val weight: Int, val value: E) {
    override fun toString() = "${from.value} ==${if (value != null) "$value/" else ""}$weight=> ${to.value}"
}

fun main() {
    val graph = Graph.build<String, Nothing?> {
        edge("A", "B", 7); edge("B", "A", 7)
        edge("A", "C", 9); edge("C", "A", 9)
        edge("A", "F", 14); edge("F", "A", 14)
        edge("B", "C", 10); edge("C", "B", 10)
        edge("B", "D", 15); edge("D", "B", 15)
        edge("C", "D", 11); edge("D", "C", 11)
        edge("C", "F", 2); edge("F", "C", 2)
        edge("D", "E", 6); edge("E", "D", 6)
        edge("E", "F", 9); edge("F", "E", 9)
    }
    println(graph)
    println(graph.dijkstra(graph["A"]!!, graph["E"]!!))
}
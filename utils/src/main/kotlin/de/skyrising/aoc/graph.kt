package de.skyrising.aoc

import java.util.*

class Graph<V, E> {
    private val vertexes = mutableMapOf<V, Vertex<V>>()
    private val outgoing = mutableMapOf<Vertex<V>, MutableSet<Edge<V, E?>>>()
    private val incoming = mutableMapOf<Vertex<V>, MutableSet<Edge<V, E?>>>()

    val size: Int get() = vertexes.size

    fun getVertexes(): Set<Vertex<V>> = LinkedHashSet(vertexes.values)

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

    fun dijkstra(from: Vertex<V>, to: Vertex<V>) = dijkstra(from) {
        it == to
    }
    fun dijkstra(from: Vertex<V>, to: (Vertex<V>) -> Boolean): Path<V, E>? {
        val unvisited = mutableSetOf(from)
        val visited = mutableSetOf<Vertex<V>>()
        val inc = mutableMapOf<Vertex<V>, Edge<V, E?>>()
        val dist = mutableMapOf<Vertex<V>, Int>()
        dist[from] = 0
        while (unvisited.isNotEmpty()) {
            val current = lowest(unvisited, dist)!!
            if (to(current) || current !in unvisited) {
                return buildPath(from, current, inc)
            }
            val curDist = dist[current] ?: return null
            unvisited.remove(current)
            visited.add(current)
            for (e in getOutgoing(current)) {
                val v = e.to
                if (v in visited) continue
                val alt = curDist + e.weight
                if (alt < (dist[v] ?: Integer.MAX_VALUE)) {
                    dist[v] = alt
                    inc[v] = e
                    unvisited.add(v)
                }
            }
        }
        return null
    }

    fun astar(from: Vertex<V>, to: Vertex<V>, h: (Vertex<V>) -> Int) = astar(from, h) {
        it == to
    }
    fun astar(from: Vertex<V>, h: (Vertex<V>) -> Int, to: (Vertex<V>) -> Boolean): Path<V, E>? {
        val unvisited = mutableSetOf(from)
        val visited = mutableSetOf<Vertex<V>>()
        val inc = mutableMapOf<Vertex<V>, Edge<V, E?>>()
        val distG = mutableMapOf<Vertex<V>, Int>()
        val distF = mutableMapOf<Vertex<V>, Int>()
        distG[from] = 0
        distF[from] = h(from)
        while (unvisited.isNotEmpty()) {
            val current = lowest(unvisited, distF)!!
            if (to(current) || current !in unvisited) {
                return buildPath(from, current, inc)
            }
            val curDist = distG[current] ?: return null
            unvisited.remove(current)
            visited.add(current)
            for (e in getOutgoing(current)) {
                val v = e.to
                if (visited.contains(v)) continue
                val alt = curDist + e.weight
                if (alt < (distG[v] ?: Integer.MAX_VALUE)) {
                    distG[v] = alt
                    distF[v] = alt + h(v)
                    inc[v] = e
                    unvisited.add(v)
                }
            }
        }
        return null
    }

    fun tsp(): List<Edge<V, E?>>? {
        val vertexList = vertexes.values.toList()
        return tspBruteForce(vertexList[0], setOf(vertexList[0]), vertexList[0])?.first
    }

    private fun tspBruteForce(from: Vertex<V>, invalid: Set<Vertex<V>>, first: Vertex<V>): Pair<List<Edge<V, E?>>, Int>? {
        if (invalid.size == vertexes.size) {
            val e = outgoing[from]?.find { it.to == first }
            return e?.let { listOf(it) to it.weight }
        }
        var length: Int? = null
        var path: List<Edge<V, E?>>? = null
        for (edge in outgoing[from] ?: listOf()) {
            if (edge.to in invalid) continue
            val newInvalid = HashSet(invalid)
            newInvalid.add(edge.to)
            val (newPath, newLength) = tspBruteForce(edge.to, newInvalid, first) ?: continue
            if (length == null || newLength + edge.weight < length) {
                length = newLength + edge.weight
                path = mutableListOf()
                path.add(edge)
                path.addAll(newPath)
            }
        }
        return if (length != null) path!! to length else null
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

    fun getPathsV1(from: Vertex<V>, to: Vertex<V>, predicate: (Path<V, E>) -> Boolean) = getPathsV1(from, to, predicate, Path(emptyList()))

    private fun getPathsV1(from: Vertex<V>, to: Vertex<V>, predicate: (Path<V, E>) -> Boolean, via: Path<V, E>): Set<Path<V, E>> {
        if (from == to) return setOf(via)
        val paths = mutableSetOf<Path<V, E>>()
        for (e in getOutgoing(from)) {
            val newVia = via + e
            if (!predicate(newVia)) continue
            paths.addAll(getPathsV1(e.to, to, predicate, newVia))
        }
        return paths
    }

    inline fun getPaths(from: Vertex<V>, to: Vertex<V>, predicate: (Path<V, E>) -> Boolean): Set<Path<V, E>> {
        val paths = mutableSetOf<Path<V, E>>()
        val todo = ArrayDeque<Path<V, E>>()
        todo.add(Path(emptyList()))
        while (todo.isNotEmpty()) {
            val via = todo.poll()
            val v = if (via.isEmpty()) from else via.last().to
            for (e in getOutgoing(v)) {
                val newVia = via + e
                if (!predicate(newVia)) continue
                if (e.to == to) {
                    paths.add(newVia)
                } else {
                    todo.add(newVia)
                }
            }
        }
        return paths
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
        if (lowest == null || (map[v] ?: Integer.MAX_VALUE) < (map[lowest] ?: Integer.MAX_VALUE)) {
            lowest = v
        }
    }
    return lowest
}

private fun <V, E> buildPath(from: Vertex<V>, to: Vertex<V>, inc: Map<Vertex<V>, Edge<V, E?>>): Path<V, E>? {
    val first = inc[to] ?: return null
    val path = LinkedList<Edge<V, E?>>()
    path.add(first)
    var edge: Edge<V, E?>? = first
    while (true) {
        val eFrom = edge!!.from
        if (eFrom == from) return Path(path)
        edge = inc[eFrom]
        if (edge == null) return null
        path.addFirst(edge)
    }
}

data class Vertex<V>(val value: V)
data class Edge<V, E>(val from: Vertex<V>, val to: Vertex<V>, val weight: Int, val value: E) {
    override fun toString() = "${from.value} ==${if (value != null) "$value/" else ""}$weight=> ${to.value}"
}
data class Path<V, E>(private val edges: List<Edge<V, E?>>) : List<Edge<V, E?>> by edges {
    fun getVertexes(): List<Vertex<V>> {
        if (edges.isEmpty()) return emptyList()
        val vertexes = mutableListOf(edges[0].from)
        for (e in edges) vertexes.add(e.to)
        return vertexes
    }

    inline fun forEachVertex(callback: (Vertex<V>) -> Unit) {
        if (this.isEmpty()) return
        callback(this[0].from)
        for (e in this) callback(e.to)
    }

    override fun toString() = getVertexes().joinToString(separator = ",") { it.value.toString() }
}

operator fun <V, E> Edge<V, E?>.plus(path: Path<V, E>): Path<V, E> {
    if (path.isEmpty()) return Path(listOf(this))
    if (path[0].from != this.to) throw IllegalArgumentException("Cannot connect $this to ${path[0].from}")
    val newEdges = ArrayList<Edge<V, E?>>(path.size + 1)
    newEdges.add(this)
    newEdges.addAll(path)
    return Path(newEdges)
}

operator fun <V, E> Path<V, E>.plus(edge: Edge<V, E?>): Path<V, E> {
    if (this.isEmpty()) return Path(listOf(edge))
    if (this.last().to != edge.from) throw IllegalArgumentException("Cannot connect ${this.last().to} to $edge")
    val newEdges = ArrayList<Edge<V, E?>>(this.size + 1)
    newEdges.addAll(this)
    newEdges.add(edge)
    return Path(newEdges)
}

inline fun <T> bfsPath(start: T, end: (T) -> Boolean, step: (T) -> Iterable<T>): List<T>? {
    val visited = mutableSetOf(start)
    var queue = mutableListOf(start)
    val prev = mutableMapOf<T, T>()
    while (queue.isNotEmpty()) {
        val next = mutableListOf<T>()
        for (v in queue) {
            if (end(v)) {
                val path = mutableListOf(v)
                var p = v
                while (true) {
                    p = prev[p] ?: break
                    path.add(p)
                }
                return path.reversed()
            }
            for (n in step(v)) {
                if (visited.add(n)) {
                    next.add(n)
                    prev[n] = v
                }
            }
        }
        queue = next
    }
    return null
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
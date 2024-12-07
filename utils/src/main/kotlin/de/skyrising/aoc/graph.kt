package de.skyrising.aoc

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
import java.util.*

abstract class BaseGraph<V, E> {
    @get:JvmName("getInternalVertexes")
    protected val vertexes = mutableSetOf<V>()
    protected val outgoing = mutableMapOf<V, MutableSet<Edge<V, E?>>>()

    val size: Int get() = vertexes.size

    val edges get() = outgoing.values.flatMapTo(mutableSetOf()) { it }

    fun getOutgoing(v: V): Set<Edge<V, E?>> = outgoing[v] ?: emptySet()

    abstract fun getIncoming(v: V): Set<Edge<V, E?>>

    fun getVertexes(): Set<V> = vertexes

    fun vertex(value: V) {
        vertexes.add(value)
    }

    abstract fun edge(from: V, to: V, weight: Int, value: E? = null)

    inline fun forEachSimplePath(from: V, to: V, cb: (Path<V, E>)->Unit) = forEachSimplePath(from, { it == to }, cb)

    inline fun forEachSimplePath(from: V, to: (V)->Boolean, cb: (Path<V, E>)->Unit) {
        val queue = ArrayDeque<Pair<V, Path<V, E>>>()
        queue.add(from to Path(emptyList()))
        while (queue.isNotEmpty()) {
            val (v, path) = queue.removeLast()
            if (to(v)) {
                cb(path)
                continue
            }
            for (e in getOutgoing(v)) {
                if (e.to in path) continue
                queue.add(e.to to path + e)
            }
        }
    }

    fun dijkstra(from: V, to: V) = dijkstra(from) {
        it == to
    }
    inline fun dijkstra(from: V, to: (V) -> Boolean) = dijkstra(from, to, this::getOutgoing)

    inline fun astar(from: V, to: V, h: (V) -> Int) = astar(from, h) {
        it == to
    }
    inline fun astar(from: V, h: (V) -> Int, to: (V) -> Boolean) = astar(from, h, to, this::getOutgoing)

    fun tsp(): List<Edge<V, E?>>? {
        val vertexList = vertexes.toList()
        return tspBruteForce(vertexList[0], setOf(vertexList[0]), vertexList[0])?.first
    }

    private fun tspBruteForce(from: V, invalid: Set<V>, first: V): Pair<List<Edge<V, E?>>, Int>? {
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

    fun countPaths(from: V, to: V) = countPaths(from, to, mutableMapOf())

    private fun countPaths(from: V, to: V, cache: MutableMap<V, Long>): Long {
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

    fun getPathsV1(from: V, to: V, predicate: (Path<V, E>) -> Boolean) = getPathsV1(from, to, predicate, Path(emptyList()))

    private fun getPathsV1(from: V, to: V, predicate: (Path<V, E>) -> Boolean, via: Path<V, E>): Set<Path<V, E>> {
        if (from == to) return setOf(via)
        val paths = mutableSetOf<Path<V, E>>()
        for (e in getOutgoing(from)) {
            val newVia = via + e
            if (!predicate(newVia)) continue
            paths.addAll(getPathsV1(e.to, to, predicate, newVia))
        }
        return paths
    }

    inline fun getPaths(from: V, to: V, predicate: (Path<V, E>) -> Boolean): Set<Path<V, E>> {
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

    fun getConnected(start: V): Set<V> {
        val connected = mutableSetOf<V>()
        val queue = ArrayDeque<V>()
        queue.add(start)
        while (queue.isNotEmpty()) {
            val v = queue.removeFirst()
            if (!connected.add(v)) continue
            for (e in getOutgoing(v)) {
                queue.add(e.to)
            }
        }
        return connected
    }

    override fun toString(): String {
        val sb = StringBuilder()
        for (v in vertexes) {
            val out: Set<Edge<V, E?>> = outgoing[v] ?: emptySet()
            if (out.isEmpty()) {
                sb.append(v).append('\n')
            } else {
                for (e in out) {
                    sb.append(e).append('\n')
                }
            }
        }
        return sb.toString()
    }
}

class UndirectedGraph<V, E> : BaseGraph<V, E>() {
    override fun getIncoming(v: V): Set<Edge<V, E?>> = getOutgoing(v).mapTo(mutableSetOf()) { Edge(it.to, it.from, it.weight, it.value) }

    override fun edge(from: V, to: V, weight: Int, value: E?) {
        vertex(from)
        vertex(to)
        outgoing.computeIfAbsent(from) { mutableSetOf() }.add(Edge(from, to, weight, value))
        outgoing.computeIfAbsent(to) { mutableSetOf() }.add(Edge(to, from, weight, value))
    }

    fun simplify(keep: Set<V> = emptySet()) {
        val queue = ArrayDeque(vertexes)
        while (queue.isNotEmpty()) {
            val v = queue.poll()
            if (v in keep) continue
            val outEdges = outgoing[v] ?: continue
            if (outEdges.size != 2) continue
            val (e1, e2) = outEdges.toTypedArray()
            edge(e1.to, e2.to, e1.weight + e2.weight, null as E?)
            outgoing.remove(v)
            vertexes.remove(v)
            queue.add(e1.to)
            queue.add(e2.to)
        }
    }

    companion object {
        inline fun <V, E> build(init: UndirectedGraph<V, E>.() -> Unit): UndirectedGraph<V, E> {
            val graph = UndirectedGraph<V, E>()
            init(graph)
            return graph
        }
    }
}

class Graph<V, E> : BaseGraph<V, E>() {
    private val incoming = mutableMapOf<V, MutableSet<Edge<V, E?>>>()

    override fun edge(from: V, to: V, weight: Int, value: E?) {
        edge(Edge(from, to, weight, value))
    }

    fun edge(e: Edge<V, E?>): Edge<V, E?> {
        vertex(e.from)
        vertex(e.to)
        outgoing.computeIfAbsent(e.from) { mutableSetOf() }.add(e)
        incoming.computeIfAbsent(e.to) { mutableSetOf() }.add(e)
        return e
    }

    override fun getIncoming(v: V): Set<Edge<V, E?>> = incoming[v] ?: emptySet()

    fun simplify(keep: Set<V> = emptySet()) {
        val queue = ArrayDeque(vertexes)
        while (queue.isNotEmpty()) {
            val v = queue.poll()
            if (v in keep) continue
            val out = outgoing[v]?.singleOrNull() ?: continue
            val inc = incoming[v]?.singleOrNull() ?: continue
            val e = Edge(inc.from, out.to, inc.weight + out.weight, null as E?)
            outgoing[inc.from]!!.remove(inc)
            incoming[out.to]!!.remove(out)
            outgoing[inc.from]!!.add(e)
            incoming[out.to]!!.add(e)
            incoming.remove(v)
            outgoing.remove(v)
            vertexes.remove(v)
            queue.add(e.from)
            queue.add(e.to)
        }
    }

    companion object {
        inline fun <V, E> build(init: Graph<V, E>.() -> Unit): Graph<V, E> {
            val graph = Graph<V, E>()
            init(graph)
            return graph
        }
    }
}

fun <V, E> buildPath(from: V, to: V, inc: Map<V, Edge<V, E?>>): Path<V, E>? {
    val first = inc[to] ?: return null
    val path = mutableListOf<Edge<V, E?>>()
    path.add(first)
    var edge: Edge<V, E?>? = first
    while (true) {
        val eFrom = edge!!.from
        if (eFrom == from) {
            path.reverse()
            return Path(path)
        }
        edge = inc[eFrom]
        if (edge == null) return null
        path.add(edge)
    }
}

data class Edge<V, E>(val from: V, val to: V, val weight: Int, val value: E) {
    override fun toString() = "$from ==${if (value != null) "$value/" else ""}$weight=> $to"
}
data class Path<V, E>(internal val edges: List<Edge<V, E?>>) : List<Edge<V, E?>> by edges {
    val vertexes: List<V> get() {
        if (edges.isEmpty()) return emptyList()
        val vertexes = Array<Any?>(edges.size + 1) { null }
        vertexes[0] = edges[0].from
        for (i in edges.indices) vertexes[i + 1] = edges[i].to
        return vertexes.asList() as List<V>
    }
    val weight get() = edges.sumOf { it.weight }

    @JvmName("containsVertex")
    operator fun contains(v: V): Boolean {
        if (edges.isEmpty()) return false
        if (edges[0].from == v) return true
        for (e in edges) if (e.to == v) return true
        return false
    }

    inline fun forEachVertex(callback: (V) -> Unit) {
        if (this.isEmpty()) return
        callback(this[0].from)
        for (e in this) callback(e.to)
    }

    override fun toString() = vertexes.joinToString(separator = ",") { it.toString() }
}

operator fun <V, E> Edge<V, E?>.plus(path: Path<V, E>): Path<V, E> {
    if (path.isEmpty()) return Path(listOf(this))
    if (path[0].from != this.to) throw IllegalArgumentException("Cannot connect $this to ${path[0].from}")
    val newEdges = ArrayList<Edge<V, E?>>(path.size + 1)
    newEdges.add(this)
    newEdges.addAll(path.edges)
    return Path(newEdges)
}

operator fun <V, E> Path<V, E>.plus(edge: Edge<V, E?>): Path<V, E> {
    if (this.isEmpty()) return Path(listOf(edge))
    if (this.last().to != edge.from) throw IllegalArgumentException("Cannot connect ${this.last().to} to $edge")
    val newEdges = ArrayList<Edge<V, E?>>(this.size + 1)
    newEdges.addAll(edges)
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

data class VertexWithDistance<V>(val vertex: V, val dist: Int): Comparable<VertexWithDistance<V>> {
    override fun compareTo(other: VertexWithDistance<V>) = dist.compareTo(other.dist)
}

inline fun <V, E> dijkstra(from: V, to: (V) -> Boolean, getOutgoing: (V)->Collection<Edge<V, E?>>): Path<V, E>? {
    val unvisited = PriorityQueue<VertexWithDistance<V>>()
    unvisited.add(VertexWithDistance(from, 0))
    val inc = mutableMapOf<V, Edge<V, E?>>()
    val dist = Object2IntOpenHashMap<V>()
    dist.put(from, 0)
    while (unvisited.isNotEmpty()) {
        val (current, curDist) = unvisited.poll()
        if (curDist != dist.getOrDefault(current as Any, -1)) continue
        if (to(current)) return buildPath(from, current, inc)
        for (e in getOutgoing(current)) {
            val v = e.to
            val alt = curDist + e.weight
            if (alt < dist.getOrDefault(v as Any, Int.MAX_VALUE)) {
                dist.put(v, alt)
                inc[v] = e
                unvisited.add(VertexWithDistance(v, alt))
            }
        }
    }
    return null
}

inline fun <V, E> astar(from: V, h: (V) -> Int, to: (V) -> Boolean, getOutgoing: (V)->Collection<Edge<V, E?>>): Path<V, E>? {
    val unvisited = PriorityQueue<VertexWithDistance<V>>()
    unvisited.add(VertexWithDistance(from, h(from)))
    val inc = mutableMapOf<V, Edge<V, E?>>()
    val distG = Object2IntOpenHashMap<V>()
    val distF = Object2IntOpenHashMap<V>()
    distG[from] = 0
    distF[from] = h(from)
    while (unvisited.isNotEmpty()) {
        val (current, curDistF) = unvisited.poll()
        if (curDistF != distF.getOrDefault(current as Any, -1)) continue
        if (to(current)) return buildPath(from, current, inc)
        val curDist = distG.getInt(current)
        for (e in getOutgoing(current)) {
            val v = e.to
            val alt = curDist + e.weight
            if (alt < distG.getOrDefault(v as Any, Int.MAX_VALUE)) {
                distG[v] = alt
                val fDist = alt + h(v)
                distF[v] = fDist
                inc[v] = e
                unvisited.add(VertexWithDistance(v, fDist))
            }
        }
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
    println(graph.dijkstra("A", "E"))
}
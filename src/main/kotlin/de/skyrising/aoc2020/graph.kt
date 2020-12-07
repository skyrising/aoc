package de.skyrising.aoc2020

class Graph<V, E> {
    private val vertexes = mutableMapOf<V, Vertex<V>>()
    private val outgoing = mutableMapOf<Vertex<V>, MutableSet<Edge<V, E>>>()
    private val incoming = mutableMapOf<Vertex<V>, MutableSet<Edge<V, E>>>()

    fun addVertex(value: V) = addVertex(Vertex(value))
    fun addVertex(v: Vertex<V>): Vertex<V> {
        vertexes[v.value] = v
        return v
    }

    fun addEdge(from: V, to: V, value: E) = addEdge(vertexes.computeIfAbsent(from, ::Vertex), vertexes.computeIfAbsent(to, ::Vertex), value)
    fun addEdge(from: Vertex<V>, to: Vertex<V>, value: E) = addEdge(Edge(from, to, value))
    fun addEdge(e: Edge<V, E>): Edge<V, E> {
        addVertex(e.from)
        addVertex(e.to)
        outgoing.computeIfAbsent(e.from) { mutableSetOf() }.add(e)
        incoming.computeIfAbsent(e.to) { mutableSetOf() }.add(e)
        return e
    }

    operator fun get(v: V) = vertexes[v]

    fun getOutgoing(v: V) = getOutgoing(vertexes[v] ?: Vertex(v))
    fun getOutgoing(v: Vertex<V>): Set<Edge<V, E>> = outgoing.getOrElse(v) { emptySet() }

    fun getIncoming(v: V) = getIncoming(vertexes[v] ?: Vertex(v))
    fun getIncoming(v: Vertex<V>): Set<Edge<V, E>> = incoming.getOrElse(v) { emptySet() }

    override fun toString(): String {
        val sb = StringBuilder()
        for (v in vertexes.values) {
            val out: Set<Edge<V, E>> = outgoing[v] ?: emptySet()
            if (out.isEmpty()) {
                sb.append(v.value).append('\n')
            } else {
                for (e in out) {
                    sb.append(e.from.value).append(" -").append(e.value).append("> ").append(e.to.value).append('\n')
                }
            }
        }
        return sb.toString()
    }
}

data class Vertex<V>(val value: V)
data class Edge<V, E>(val from: Vertex<V>, val to: Vertex<V>, val value: E)
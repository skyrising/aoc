package de.skyrising.aoc2021

import de.skyrising.aoc.*

@Suppress("unused")
class BenchmarkDay12 : BenchmarkDay(12)

@Suppress("unused")
fun registerDay12() {
    val test = TestInput("""
        start-A
        start-b
        A-c
        A-b
        b-d
        A-end
        b-end
    """)
    val test2 = TestInput("""
        dc-end
        HN-start
        start-kj
        dc-start
        dc-HN
        LN-dc
        HN-end
        kj-sa
        kj-HN
        kj-dc
    """)
    val test3 = TestInput("""
        fs-end
        he-DX
        fs-he
        start-DX
        pj-DX
        end-zg
        zg-sl
        zg-pj
        pj-he
        RW-he
        fs-DX
        pj-RW
        zg-RW
        start-pj
        he-WI
        zg-he
        pj-fs
        start-RW
    """)
    part1("Passage Pathing") {
        val g = readInput(this) { s -> s }
        g.getPathsV1(g["start"]!!, g["end"]!!) { p ->
            val caves = p.getVertexes().map(Vertex<String>::value)
            val small = mutableSetOf<String>()
            for (cave in caves) {
                if (cave.lowercase() == cave) {
                    if (!small.add(cave)) return@getPathsV1 false
                }
            }
            true
        }.size
    }
    part1("Passage Pathing") {
        val caves = mutableMapOf<String, Cave>()
        val g = readInput(this) { s -> caves.computeIfAbsent(s, ::Cave) }
        g.getPaths(g[caves["start"]!!]!!, g[caves["end"]!!]!!) { p ->
            val small = HashSet<Cave>(p.size)
            p.forEachVertex { v ->
                val cave = v.value
                if (cave.isSmall && !small.add(cave)) return@getPaths false
            }
            true
        }.size
    }
    part2 {
        val g = readInput(this) { s -> s }
        g.getPathsV1(g["start"]!!, g["end"]!!) { p ->
            val caves = p.getVertexes().map(Vertex<String>::value)
            val small = mutableSetOf<String>()
            var smallDouble = false
            for (cave in caves) {
                if (cave.lowercase() == cave) {
                    if (!small.add(cave)) {
                        if (!smallDouble && cave != "start") {
                            smallDouble = true
                        } else {
                            return@getPathsV1 false
                        }
                    }
                }
            }
            true
        }.size
    }
    part2 {
        val caves = mutableMapOf<String, Cave>()
        val g = readInput(this) { s -> caves.computeIfAbsent(s, ::Cave) }
        g.getPaths(g[caves["start"]!!]!!, g[caves["end"]!!]!!) { p ->
            val small = HashSet<Cave>(p.size)
            var smallDouble = false
            p.forEachVertex { v ->
                val cave = v.value
                if (cave.isSmall && !small.add(cave)) {
                    if (!smallDouble && cave.name != "start") {
                        smallDouble = true
                    } else {
                        return@getPaths false
                    }
                }
            }
            true
        }.size
    }
}

private fun <V> readInput(input: PuzzleInput, vertexLabel: (String) -> V): Graph<V, Nothing> = Graph.build {
    for (line in input.lines) {
        val (from, to) = line.split('-')
        val fromCave = vertexLabel(from)
        val toCave = vertexLabel(to)
        edge(fromCave, toCave, 1)
        edge(toCave, fromCave, 1)
    }
}

data class Cave(val name: String) {
    val isSmall = name.lowercase() == name
    private val hashCode = name.hashCode()
    override fun toString() = name
    override fun hashCode() = hashCode
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        return other is Cave && name == other.name
    }
}
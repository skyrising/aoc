package de.skyrising.aoc2021.day12

import de.skyrising.aoc.Graph
import de.skyrising.aoc.PuzzleInput
import de.skyrising.aoc.PuzzleName
import de.skyrising.aoc.TestInput

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

@PuzzleName("Passage Pathing")
fun PuzzleInput.part1v0(): Any {
    val g = readInput(this) { s -> s }
    return g.getPathsV1("start", "end") { p ->
        val caves = p.vertexes
        val small = mutableSetOf<String>()
        for (cave in caves) {
            if (cave.lowercase() == cave) {
                if (!small.add(cave)) return@getPathsV1 false
            }
        }
        true
    }.size
}

@PuzzleName("Passage Pathing")
fun PuzzleInput.part1v1(): Any {
    val caves = mutableMapOf<String, Cave>()
    val g = readInput(this) { s -> caves.computeIfAbsent(s, ::Cave) }
    return g.getPaths(caves["start"]!!, caves["end"]!!) { p ->
        val small = HashSet<Cave>(p.size)
        p.forEachVertex { cave ->
            if (cave.isSmall && !small.add(cave)) return@getPaths false
        }
        true
    }.size
}

fun PuzzleInput.part2v0(): Any {
    val g = readInput(this) { s -> s }
    return g.getPathsV1("start", "end") { p ->
        val caves = p.vertexes
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

fun PuzzleInput.part2v1(): Any {
    val caves = mutableMapOf<String, Cave>()
    val g = readInput(this) { s -> caves.computeIfAbsent(s, ::Cave) }
    return g.getPaths(caves["start"]!!, caves["end"]!!) { p ->
        val small = HashSet<Cave>(p.size)
        var smallDouble = false
        p.forEachVertex { cave ->
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
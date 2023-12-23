package de.skyrising.aoc2023.day23

import de.skyrising.aoc.*

val test = TestInput("""
    #.#####################
    #.......#########...###
    #######.#########.#.###
    ###.....#.>.>.###.#.###
    ###v#####.#v#.###.#.###
    ###.>...#.#.#.....#...#
    ###v###.#.#.#########.#
    ###...#.#.#.......#...#
    #####.#.#.#######.#.###
    #.....#.#.#.......#...#
    #.#####.#.#.#########v#
    #.#...#...#...###...>.#
    #.#.#v#######v###.###v#
    #...#.>.#...>.>.#.###.#
    #####v#.#.###v#.#.###.#
    #.....#...#...#.#.#...#
    #.#########.###.#.#.###
    #...###...#...#...#.###
    ###.###.#.###v#####v###
    #...#...#.#.>.>.#.>.###
    #.###.###.#.###.#.#v###
    #.....###...###...#...#
    #####################.#
""")

private fun dirOfSlope(slope: Char) = when (slope) {
    '>' -> Direction.E
    '<' -> Direction.W
    '^' -> Direction.N
    'v' -> Direction.S
    else -> throw IllegalArgumentException()
}

data class Segment(val start: Vec2i, val end: Vec2i, val length: Int, val slopes: Set<Vec2i>)

private fun parse(input: PuzzleInput): Pair<Map<Vec2i, Segment>, CharGrid> {
    val cg = input.charGrid
    val queue = ArrayDeque<Pair<Vec2i, Direction>>()
    queue.add(Vec2i(1, 0) to Direction.S)
    val segments = mutableMapOf<Vec2i, Segment>()
    while (queue.isNotEmpty()) {
        val (segStart, dir) = queue.removeFirst()
        if (segStart in segments) continue
        var pos = segStart + dir
        val segment = mutableSetOf(segStart, pos)
        while (true) {
            val next = pos.fourNeighbors().singleOrNull { it in cg && it !in segment && cg[it] == '.'}
            if (next != null) {
                segment.add(next)
                pos = next
                continue
            }
            val slopes = pos.fourNeighbors().filterTo(mutableSetOf()) { it in cg && it !in segment && cg[it] == "^>v<"[(it - pos).dir.ordinal] }
            segments[segStart] = Segment(segStart, pos, segment.size - 1, slopes)
            for (slope in slopes) {
                queue.add(slope to dirOfSlope(cg[slope]))
            }
            break
        }
    }
    return segments to cg
}

@PuzzleName("A Long Walk")
fun PuzzleInput.part1(): Any {
    val (segments, cg) = parse(this)
    val g = Graph.build<Vec2i, Any> {
        for ((start, end, length, slopes) in segments.values) {
            for (slope in slopes) edge(start, slope, length + 1)
            if (slopes.isEmpty()) edge(start, end, length)
        }
    }
    val start = Vec2i(cg.row(0).indexOf('.'), 0)
    val end = Vec2i(cg.row(cg.height - 1).indexOf('.'), cg.height - 1)
    g.simplify(setOf(start, end))
    // for (edge in g.edges) {
    //     println("\"${edge.from.x},${edge.from.y}\" -> \"${edge.to.x},${edge.to.y}\" [label=${edge.weight}]")
    // }
    fun findLongest(to: Vec2i): Int {
        if (to == start) return 0
        return g.getIncoming(to).maxOf { findLongest(it.from) + it.weight }
    }
    return findLongest(end)
}

fun PuzzleInput.part2(): Any {
    val (segments, cg) = parse(this)
    val g = Graph.build<Vec2i, Any> {
        for ((start, end, length, slopes) in segments.values) {
            edge(start, end, length)
            for (slope in slopes) {
                edge(end, slope, 1)
            }
        }
    }
    val start = Vec2i(1, 0)
    val end = Vec2i(cg.width - 2, cg.height - 1)
    g.simplify(setOf(start, end))
    // for (edge in g.edges) {
    //     println("\"${edge.from.x},${edge.from.y}\" -- \"${edge.to.x},${edge.to.y}\" [label=${edge.weight}]")
    // }
    for (edge in g.edges) {
        g.edge(edge.to, edge.from, edge.weight)
    }
    var maxWeight = 0
    g.forEachSimplePath(start, end) {
        maxWeight = maxOf(maxWeight, it.weight)
    }
    return maxWeight
}
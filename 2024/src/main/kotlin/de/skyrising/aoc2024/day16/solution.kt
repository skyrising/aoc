package de.skyrising.aoc2024.day16

import de.skyrising.aoc.*
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
import java.util.*

val test = TestInput("""
###############
#.......#....E#
#.#.###.#.###.#
#.....#.#...#.#
#.###.#####.#.#
#.#.#.......#.#
#.#.#####.###.#
#...........#.#
###.#.#####.#.#
#...#.....#.#.#
#.#.#.###.#.#.#
#.....#...#.#.#
#.###.#.#.#.#.#
#S..#.....#...#
###############
""")

val test2 = TestInput("""
########
###...E#
#...#.##
#.#.#.##
#.....##
#S######
########
""")

fun buildGraph(grid: CharGrid): Triple<Graph<Pair<Vec2i, Direction>, Nothing>, Pair<Vec2i, Direction>, Vec2i> {
    val start = grid.where { it == 'S' }.first() to Direction.E
    val end = grid.find { it.charValue == 'E' }!!.key
    return Triple(Graph.build {
        val queue = ArrayDeque<Pair<Vec2i, Direction>>()
        val visited = mutableSetOf<Pair<Vec2i, Direction>>()
        queue.add(start)
        while (queue.isNotEmpty()) {
            val v = queue.removeFirst()
            if (!visited.add(v)) continue
            val (fromPos, dir) = v
            for (d in listOf(dir.rotateCW(), dir.rotateCCW())) {
                if (grid[fromPos + d] == '#') continue
                edge(fromPos to dir, fromPos to d, 1000)
                queue.add(fromPos to d)
            }
            val toPos = fromPos + dir
            if (grid[toPos] != '#') {
                edge(fromPos to dir, toPos to dir, 1)
                queue.add(toPos to dir)
            }
        }
        simplify()
    }, start, end)
}

@PuzzleName("Reindeer Maze")
fun PuzzleInput.part1(): Any {
    val (g, start, end) = buildGraph(charGrid)
    return g.dijkstra(start) { (pos, _) -> pos == end }!!.sumOf { it.weight }
}

fun PuzzleInput.part2(): Any {
    val grid = charGrid
    val (g, start, end) = buildGraph(grid)

    val unvisited = PriorityQueue<VertexWithDistance<Pair<Vec2i, Direction>>>()
    unvisited.add(VertexWithDistance(start, 0))
    val dist = Object2IntOpenHashMap<Pair<Vec2i, Direction>>()
    dist.put(start, 0)
    while (unvisited.isNotEmpty()) {
        val (current, curDist) = unvisited.poll()
        if (curDist != dist.getOrDefault(current as Any, -1)) continue
        for (e in g.getOutgoing(current)) {
            val v = e.to
            val alt = curDist + e.weight
            if (alt < dist.getOrDefault(v as Any, Int.MAX_VALUE)) {
                dist[v] = alt
                unvisited.add(VertexWithDistance(v, alt))
            }
        }
    }

    val queue = ArrayDeque<Pair<Vec2i, Direction>>()
    val visited = mutableSetOf<Pair<Vec2i, Direction>>()
    for (dir in Direction.values) {
        queue.add(end to dir)
    }
    while (queue.isNotEmpty()) {
        val v = queue.removeLast()
        if (!visited.add(v)) continue
        val costTo = dist.getInt(v)
        for (e in g.getIncoming(v)) {
            val costFrom = dist.getInt(e.from)
            val onBestPath = costFrom + e.weight <= costTo
            if (!onBestPath) continue
            grid[e.from.first lineTo e.to.first] = 'O'
            queue.add(e.from)
        }
    }
    return grid.count { it == 'O' }
}

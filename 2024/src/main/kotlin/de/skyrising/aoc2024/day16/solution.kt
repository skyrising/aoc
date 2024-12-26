@file:PuzzleName("Reindeer Maze")

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

class VecDirPair(private val longValue: Long) {
    constructor(pos: Vec2i, dir: Direction): this((pos.longValue shl 2) or dir.ordinal.toLong())
    val pos get() = Vec2i(longValue shr 2)
    val dir get() = Direction((longValue and 3).toInt())
    operator fun component1() = pos
    operator fun component2() = dir

    override fun hashCode(): Int = longValue.toInt() xor (longValue shr 15).toInt()
    override fun equals(other: Any?): Boolean {
        return this === other || other is VecDirPair && other.longValue == longValue
    }

    override fun toString() = "($pos, $dir)"
}

inline fun forEachOutgoing(grid: CharGrid, fromPos: Vec2i, dir: Direction, cb: (Vec2i, Direction, Int) -> Unit) {
    val dLeft = dir.rotateCCW()
    if (grid[fromPos + dLeft] != '#') {
        cb(fromPos, dLeft, 1000)
    }
    val dRight = dir.rotateCW()
    if (grid[fromPos + dRight] != '#') {
        cb(fromPos, dRight, 1000)
    }
    val toPos = fromPos + dir
    if (grid[toPos] != '#') {
        cb(toPos, dir, 1)
    }
}

inline fun forEachIncoming(grid: CharGrid, toPos: Vec2i, dir: Direction, cb: (Vec2i, Direction, Int) -> Unit) {
    val dLeft = dir.rotateCCW()
    if (grid[toPos - dLeft] != '#') {
        cb(toPos, dLeft, 1000)
    }
    val dRight = dir.rotateCW()
    if (grid[toPos - dRight] != '#') {
        cb(toPos, dRight, 1000)
    }
    val fromPos = toPos - dir
    if (grid[fromPos] != '#') {
        cb(fromPos, dir, 1)
    }
}

fun PuzzleInput.prepare(): Pair<Int, Int> {
    val grid = charGrid
    val start = VecDirPair(grid.where { it == 'S' }.first(), Direction.E)
    val end = grid.find { it.charValue == 'E' }!!.key

    val unvisited = PriorityQueue<VertexWithDistance<VecDirPair>>()
    unvisited.add(VertexWithDistance(start, 0))
    val dist = Object2IntOpenHashMap<VecDirPair>()
    dist.put(start, 0)
    while (unvisited.isNotEmpty()) {
        val (current, curDist) = unvisited.poll()
        if (curDist != dist.getOrDefault(current as Any, -1)) continue
        forEachOutgoing(grid, current.pos, current.dir) { pos, dir, weight ->
            val v = VecDirPair(pos, dir)
            val alt = curDist + weight
            if (alt < dist.getOrDefault(v as Any, Int.MAX_VALUE)) {
                dist[v] = alt
                unvisited.add(VertexWithDistance(v, alt))
            }
        }
    }

    val queue = ArrayDeque<VecDirPair>()
    val visited = mutableSetOf<VecDirPair>()
    for (dir in Direction.values) {
        queue.add(VecDirPair(end, dir))
    }
    while (queue.isNotEmpty()) {
        val v = queue.removeLast()
        if (!visited.add(v)) continue
        val costTo = dist.getInt(v)
        forEachIncoming(grid, v.pos, v.dir) { fromPos, fromDir, weight ->
            val vFrom = VecDirPair(fromPos, fromDir)
            val costFrom = dist.getInt(vFrom)
            val onBestPath = costFrom + weight <= costTo
            if (onBestPath) {
                grid[v.pos] = 'O'
                queue.add(vFrom)
            }
        }
    }
    grid[start.pos] = 'O'
    return Direction.values.minOf { dist.getOrDefault(VecDirPair(end, it) as Any, Int.MAX_VALUE) } to  grid.count { it == 'O' }
}

fun Pair<Int, Int>.part1() = first

fun Pair<Int, Int>.part2() = second

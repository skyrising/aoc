package de.skyrising.aoc2024.day18

import de.skyrising.aoc.*
import java.util.*

val test = TestInput("""
5,4
4,2
4,5
3,0
2,1
6,3
2,4
1,5
0,6
3,3
2,6
5,1
1,2
5,5
2,5
6,5
1,4
0,4
6,4
1,1
6,1
1,0
0,5
1,6
2,0
""")

fun parse(lines: List<String>) = lines.map { it.split2(',')!!.let { (x, y) -> Vec2i(x.toInt(), y.toInt()) } }

fun blockedGrid(end: Vec2i, positions: List<Vec2i>): IntGrid {
    val width = end.x + 1
    val height = end.y + 1
    return IntGrid(width, height, IntArray(width * height) { Int.MAX_VALUE }).apply {
        for ((i, pos) in positions.withIndex()) this[pos] = i
    }
}

fun IntGrid.pathLength(start: Vec2i, end: Vec2i, limit: Int): Int {
    val visited = BitSet(data.size)
    var queue = mutableListOf(start)
    var steps = 0
    while (queue.isNotEmpty()) {
        val next = ArrayList<Vec2i>(queue.size * 3)
        for (v in queue) {
            if (v == end) return steps
            if (v.x > 0) {
                val x = v.x - 1
                val y = v.y
                val idx = localIndex(x, y)
                if (!visited[idx] && data[idx] > limit) {
                    visited.set(idx)
                    next.add(Vec2i(x, y))
                }
            }
            if (v.y > 0) {
                val x = v.x
                val y = v.y - 1
                val idx = localIndex(x, y)
                if (!visited[idx] && data[idx] > limit) {
                    visited.set(idx)
                    next.add(Vec2i(x, y))
                }
            }
            if (v.x < end.x) {
                val x = v.x + 1
                val y = v.y
                val idx = localIndex(x, y)
                if (!visited[idx] && data[idx] > limit) {
                    visited.set(idx)
                    next.add(Vec2i(x, y))
                }
            }
            if (v.y < end.y) {
                val x = v.x
                val y = v.y + 1
                val idx = localIndex(x, y)
                if (!visited[idx] && data[idx] > limit) {
                    visited.set(idx)
                    next.add(Vec2i(x, y))
                }
            }
        }
        queue = next
        steps++
    }
    return -1
}

@PuzzleName("RAM Run")
fun PuzzleInput.part1(): Int {
    val doTest = false
    val start = Vec2i(0, 0)
    val end = if (doTest) Vec2i(6, 6) else Vec2i(70, 70)
    val positions = parse(if (doTest) test.lines else lines)
    val blockedAt = blockedGrid(end, positions)
    return blockedAt.pathLength(start, end, if (doTest) 12 else 1024)
}

fun PuzzleInput.part2(): String {
    val doTest = false
    val positions = parse(if (doTest) test.lines else lines)
    val start = Vec2i(0, 0)
    val end = if (doTest) Vec2i(6, 6) else Vec2i(70, 70)
    val blockedAt = blockedGrid(end, positions)
    val i = -binarySearch(if (doTest) 12 else 1024, positions.size) {
        if (blockedAt.pathLength(start, end, it) < 0) 1 else -1
    } - 1
    return positions[i].let { "${it.x},${it.y}" }
}

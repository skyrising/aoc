@file:PuzzleName("Movie Theater")

package de.skyrising.aoc2025.day9

import de.skyrising.aoc.*
import kotlin.math.absoluteValue

val test = TestInput("""
7,1
11,1
11,7
9,7
9,5
2,5
2,3
7,3
""")

fun PuzzleInput.part1(): Any {
    val ps = lines.map { Vec2i.parse(it) }
    var maxA = 0L
    for (p in ps.unorderedPairs()) {
        val (x, y) = (p.second - p.first)
        val a = (x.absoluteValue + 1).toLong() * (y.absoluteValue + 1)
        maxA = maxOf(maxA, a)
    }
    return maxA
}

fun Line2i.intersects(x1: Int, y1: Int, x2: Int, y2: Int): Boolean {
    return if (from.x == to.x) {
        val minY = from.y
        val maxY = to.y
        (from.x in x1+1..<x2) && maxOf(minY, y1) < minOf(maxY, y2)
    } else {
        val minX = from.x
        val maxX = to.x
        (from.y in y1+1..<y2) && maxOf(minX, x1) < minOf(maxX, x2)
    }
}

fun PuzzleInput.part2(): Any {
    val ps = lines.mapTo(ArrayList(lines.size + 1)) { Vec2i.parse(it) }
    ps.add(ps[0])
    val edges = mutableListOf<Line2i>()
    for (i in 1 ..< ps.size) {
        val p0 = ps[i - 1]
        val p1 = ps[i]
        if (p0.x == p1.x) {
            val x = p0.x
            val minY = minOf(p0.y, p1.y)
            val maxY = maxOf(p1.y, p0.y)
            edges.add(Line2i(Vec2i(x, minY), Vec2i(x, maxY)))
        } else {
            val y = p0.y
            val minX = minOf(p0.x, p1.x)
            val maxX = maxOf(p1.x, p0.x)
            edges.add(Line2i(Vec2i(minX, y), Vec2i(maxX, y)))
        }
    }
    ps.removeLast()
    var maxA = 0L
    outer@for ((a, b) in ps.unorderedPairs()) {
        val (x1, y1) = a
        val (x2, y2) = b
        val minX = minOf(x1, x2)
        val maxX = maxOf(x1, x2)
        val minY = minOf(y1, y2)
        val maxY = maxOf(y1, y2)
        for (e in edges) {
            if (e.intersects(minX, minY, maxX, maxY)) continue@outer
        }
        val width = maxX - minX
        val height = maxY - minY
        val area = (width + 1).toLong() * (height + 1)
        if (area > maxA) {
            maxA = area
        }
    }
    return maxA
}

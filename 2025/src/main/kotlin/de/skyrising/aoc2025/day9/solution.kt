@file:PuzzleName("Movie Theater")
@file:Suppress("NOTHING_TO_INLINE")

package de.skyrising.aoc2025.day9

import de.skyrising.aoc.*
import it.unimi.dsi.fastutil.ints.IntArrayList
import it.unimi.dsi.fastutil.ints.IntArraySet
import it.unimi.dsi.fastutil.ints.IntList
import it.unimi.dsi.fastutil.longs.LongArrays
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

data class Prepared(val part1: Long, val part2: Long)

fun IntArray.binarySearchFirstGreater(from: Int, to: Int, key: Int): Int {
    var from = from
    var to = to
    while (from != to) {
        val mid = (from + to) ushr 1
        if (this[mid] <= key) {
            from = mid + 1
        } else {
            to = mid
        }
    }
    return from
}

fun Line2i.intersects(x1: Int, y1: Int, x2: Int, y2: Int): Boolean {
    return if (from.x == to.x) {
        val minY = from.y
        val maxY = to.y
        (from.x in x1 + 1..<x2) && maxOf(minY, y1) < minOf(maxY, y2)
    } else {
        val minX = from.x
        val maxX = to.x
        (from.y in y1 + 1..<y2) && maxOf(minX, x1) < minOf(maxX, x2)
    }
}

class Points(
    val ints: IntList,
    val n: Int = ints.size / 2
) {
    init {
        ints.add(ints.getInt(0))
        ints.add(ints.getInt(1))
    }

    inline fun x(i: Int) = ints.getInt(i * 2)
    inline fun y(i: Int) = ints.getInt(i * 2 + 1)

    fun swap(i: Int, j: Int) {
        val x = x(i)
        val y = y(i)
        ints[i * 2] = x(j)
        ints[i * 2 + 1] = y(j)
        ints[j * 2] = x
        ints[j * 2 + 1] = y
    }
}

@Suppress("ArrayInDataClass")
data class Edges(
    val mainCoord: IntArray,
    val spanFirst: IntArray,
    val spanSecond: IntArray,
    val mid: Int,
    val permPoints: IntList,
    val longEdges: List<Line2i>,
) {
    inline fun intersectVertical(minX: Int, minY: Int, maxX: Int, maxY: Int): Boolean {
        var k = mainCoord.binarySearchFirstGreater(0, mid, minY)
        while (k < mid) {
            val y = mainCoord[k]
            if (y >= maxY) break
            if (maxOf(spanFirst[k], minX) < minOf(spanSecond[k], maxX)) return true
            k++
        }
        return false
    }

    inline fun intersectHorizontal(minX: Int, minY: Int, maxX: Int, maxY: Int): Boolean {
        val n = mainCoord.size
        var k = mainCoord.binarySearchFirstGreater(mid, n, minX)
        while (k < n) {
            val x = mainCoord[k]
            if (x >= maxX) break
            if (maxOf(spanFirst[k], minY) < minOf(spanSecond[k], maxY)) return true
            k++
        }
        return false
    }
}

fun Points.prepareEdges(): Edges {
    val n = n
    // vertical then horizontal edges
    val mainCoordLong = LongArray(n) // y...y|x...x
    val spanCoords = LongArray(n) // packed min and max: x...x|y...y
    var v = 0
    var h = n
    val permPoints = IntArraySet(4)
    val longEdges = ArrayList<Line2i>(2)
    for (i in 0 ..< n) {
        val p0x = x(i)
        val p0y = y(i)
        val p1x = x(i + 1)
        val p1y = y(i + 1)
        val isLong = (p1x-p0x).absoluteValue + (p1y-p0y).absoluteValue > 10000
        if (isLong) {
            permPoints.add(i + 1)
            permPoints.add(i)
        }
        if (p0x == p1x) {
            h--
            mainCoordLong[h] = p0x.toLong()
            spanCoords[h] = packToLong(minOf(p0y, p1y), maxOf(p1y, p0y))
            if (isLong) longEdges.add(Vec2i(p0x, minOf(p0y, p1y)) lineTo Vec2i(p0x, maxOf(p1y, p0y)))
        } else {
            mainCoordLong[v] = p0y.toLong()
            spanCoords[v] = packToLong(minOf(p0x, p1x), maxOf(p1x, p0x))
            if (isLong) longEdges.add(Vec2i(minOf(p0x, p1x), p0y) lineTo Vec2i(maxOf(p1x, p0x), p0y))
            v++
        }
    }
    if (h != v) throw IllegalStateException()
    val mid = h
    LongArrays.radixSort(mainCoordLong, spanCoords, 0, mid)
    LongArrays.radixSort(mainCoordLong, spanCoords, mid, mainCoordLong.size)
    return Edges(
        IntArray(mainCoordLong.size) { mainCoordLong[it].toInt() },
        IntArray(spanCoords.size) { unpackFirstInt(spanCoords[it]) },
        IntArray(spanCoords.size) { unpackSecondInt(spanCoords[it]) },
        mid,
        IntArrayList(permPoints),
        longEdges,
    )
}

fun PuzzleInput.prepare(): Prepared {
    val p = Points(chars.ints())
    val edges = p.prepareEdges()
    val permPoints = edges.permPoints
    val longEdges = edges.longEdges
    val le1 = if (longEdges.isNotEmpty()) longEdges[0] else null
    val le2 = if (longEdges.size > 1) longEdges[1] else null
    if (permPoints.isNotEmpty()) {
        var swapIdx = 0
        var minSwap = Int.MAX_VALUE
        var maxSwap = Int.MIN_VALUE
        for (i in permPoints.indices) {
            val swap = permPoints.getInt(i)
            p.swap(swap, swapIdx++)
            minSwap = minOf(minSwap, swap)
            maxSwap = maxOf(maxSwap, swap)
        }
        for (i in maxOf(0, minSwap - 40)..<minSwap) p.swap(i, swapIdx++)
        for (i in maxSwap + 1..<minOf(p.n, maxSwap + 40)) p.swap(i, swapIdx++)
    }
    val n = p.n
    var part1 = 0L
    var part2 = 0L
    for (i in 0 ..< n) {
        val x1 = p.x(i)
        val y1 = p.y(i)
        outer@for (j in i + 1 ..< n) {
            val x2 = p.x(j)
            val y2 = p.y(j)
            val minX = minOf(x1, x2)
            val maxX = maxOf(x1, x2)
            val minY = minOf(y1, y2)
            val maxY = maxOf(y1, y2)
            val area = (maxX - minX + 1).toLong() * (maxY - minY + 1)
            if (area > part1) part1 = area
            if (area <= part2) continue
            if (le1 != null && le1.intersects(minX, minY, maxX, maxY)) continue
            if (le2 != null && le2.intersects(minX, minY, maxX, maxY)) continue
            if (edges.intersectVertical(minX, minY, maxX, maxY)) continue
            if (edges.intersectHorizontal(minX, minY, maxX, maxY)) continue
            part2 = area
        }
    }
    return Prepared(part1, part2)
}

fun Prepared.part1() = part1
fun Prepared.part2() = part2

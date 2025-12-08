@file:PuzzleName("Playground")

package de.skyrising.aoc2025.day8

import de.skyrising.aoc.*
import it.unimi.dsi.fastutil.longs.LongArrays

val test = TestInput("""
162,817,812
57,618,57
906,360,560
592,479,940
352,342,300
466,668,158
542,29,236
431,825,988
739,650,466
52,470,668
216,146,977
819,987,18
117,168,530
805,96,715
346,949,466
970,615,88
941,993,340
862,61,35
984,92,344
425,690,689
""")

class Node(var circuit: Int)

fun Array<MutableList<Node>>.connect(a: Node, b: Node): Int {
    if (a.circuit == b.circuit) return 0
    val aId = a.circuit
    val bId = b.circuit
    val min = minOf(aId, bId)
    val max = maxOf(aId, bId)
    val source = this[max]
    for (c in source) c.circuit = min
    val dest = this[min]
    dest.addAll(source)
    source.clear()
    return dest.size
}

data class Prepared(val part1: Int, val part2: Long)

fun PuzzleInput.prepare(): Prepared {
    val longs = chars.longs()
    val nodes = Array(longs.size / 3) { Node(-1) }
    val pairCount = nodes.size * (nodes.size - 1) / 2
    val dists = LongArray(pairCount)
    val pairIdx = LongArray(pairCount)
    var part1 = 0
    var part2 = 0L
    var factor = 5
    while ((1 shl factor) > 1 + nodes.size / 20) factor--
    while (part2 == 0L && factor >= 0) {
        val nodesByCircuit = Array<MutableList<Node>>(nodes.size) { i -> ArrayList<Node>(1).apply { add(nodes[i].also { it.circuit = i }) } }
        var k = 0
        var maxMinDist = 0L
        for (i in nodes.indices) {
            val x1 = longs.getLong(i * 3)
            val y1 = longs.getLong(i * 3 + 1)
            val z1 = longs.getLong(i * 3 + 2)
            val kStart = k
            for (j in i + 1 until nodes.size) {
                val dx = x1 - longs.getLong(j * 3)
                val dy = y1 - longs.getLong(j * 3 + 1)
                val dz = z1 - longs.getLong(j * 3 + 2)
                val dist = dx * dx + dy * dy + dz * dz
                dists[k] = dist
                pairIdx[k] = packToLong(i, j)
                k++
            }
            var aMinDist = Long.MAX_VALUE
            for (i in k-1 downTo kStart) aMinDist = minOf(aMinDist, dists[i])
            if (aMinDist < Long.MAX_VALUE) maxMinDist = maxOf(maxMinDist, aMinDist)
        }
        if (factor > 0) {
            k = 0
            for (i in dists.indices) {
                val dist = dists[i]
                if (dist <= maxMinDist shr factor) {
                    dists[k] = dist
                    pairIdx[k] = pairIdx[i]
                    k++
                }
            }
        }
        LongArrays.radixSort(dists, pairIdx, 0, k)
        var todo = if (nodes.size == 20) 10 else 1000
        for (i in 0..< k) {
            val idx = pairIdx[i]
            val a = nodes[unpackFirstInt(idx)]
            val b = nodes[unpackSecondInt(idx)]
            if (todo-- == 0) {
                part1 = nodesByCircuit.asList().topK(3) { it.size }.fold(1, Int::times)
            }
            if (nodesByCircuit.connect(a, b) == nodes.size) {
                part2 = longs.getLong(unpackFirstInt(idx) * 3) * longs.getLong(unpackSecondInt(idx) * 3)
                break
            }
        }
        factor--
    }
    return Prepared(part1, part2)
}

fun Prepared.part1() = part1
fun Prepared.part2() = part2


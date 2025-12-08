@file:PuzzleName("Playground")

package de.skyrising.aoc2025.day8

import de.skyrising.aoc.*

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

class Node(val pos: Vec3l, var circuit: Int) {
    override fun toString() = "$pos@$circuit"
    override fun hashCode() = pos.hashCode()
    override fun equals(other: Any?) = other is Node && pos == other.pos

    infix fun distTo(other: Node): Long {
        val x = pos.x - other.pos.x
        val y = pos.y - other.pos.y
        val z = pos.z - other.pos.z
        return x*x + y*y + z*z
    }
}

fun Array<MutableSet<Node>>.connect(a: Node, b: Node): Boolean {
    if (a.circuit == b.circuit) return false
    val aId = a.circuit
    val bId = b.circuit
    val min = minOf(aId, bId)
    val max = maxOf(aId, bId)
    val source = this[max]
    for (c in source) c.circuit = min
    this[min].addAll(source)
    source.clear()
    return true
}

data class Prepared(val part1: Int, val part2: Long)

fun PuzzleInput.prepare(): Prepared {
    val nodes = lines.map { Node(Vec3l.parse(it), -1) }
    val nodesByCircuit = Array(nodes.size) { i -> mutableSetOf(nodes[i].also { it.circuit = i }) }
    val pairs = nodes.unorderedPairs().sortedBy { (a, b) -> a distTo b }
    var todo = if (nodes.size == 20) 10 else 1000
    var part1 = 0
    var part2 = 0L
    for ((a, b) in pairs) {
        if (--todo == 0) {
            val (s0, s1, s2) = nodesByCircuit.map { it.size }.sortedDescending()
            part1 = s0 * s1 * s2
        }
        if (nodesByCircuit.connect(a, b)) part2 = a.pos.x * b.pos.x
    }
    return Prepared(part1, part2)
}

fun Prepared.part1() = part1
fun Prepared.part2() = part2


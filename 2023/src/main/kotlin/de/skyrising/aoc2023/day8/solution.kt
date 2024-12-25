@file:PuzzleName("")

package de.skyrising.aoc2023.day8

import de.skyrising.aoc.*
import it.unimi.dsi.fastutil.ints.Int2LongMap
import it.unimi.dsi.fastutil.ints.Int2LongOpenHashMap

private inline fun steps(start: Int, lr: BooleanArray, map: Int2LongMap, end: (Int)->Boolean) =
    start.stepsUntil(end) { PackedIntPair(map[this])[lr[it % lr.size]] }

private const val AAA = 13330
private const val ZZZ = 46655
private const val Z = 35

val test = TestInput("""
    RL

    AAA = (BBB, CCC)
    BBB = (DDD, EEE)
    CCC = (ZZZ, GGG)
    DDD = (DDD, DDD)
    EEE = (EEE, EEE)
    GGG = (GGG, GGG)
    ZZZ = (ZZZ, ZZZ)
""")

val test2 = TestInput("""
    LR

    11A = (11B, XXX)
    11B = (XXX, 11Z)
    11Z = (11B, XXX)
    22A = (22B, XXX)
    22B = (22C, 22C)
    22C = (22Z, 22Z)
    22Z = (22B, 22B)
    XXX = (XXX, XXX)
""")

fun parse(input: PuzzleInput) = Pair(
    input.lines[0].map { it == 'L' }.toBooleanArray(),
    input.lines.asSequence().drop(2).associateTo(Int2LongOpenHashMap(input.lines.size - 2)) {
        val (k, v) = it.split(" = ")
        val (l, r) = v.substring(1, v.length - 1).split(", ")
        k.toInt(36) to PackedIntPair(l.toInt(36), r.toInt(36)).longValue
    })

fun PuzzleInput.part1(): Any {
    val (lr, map) = parse(this)
    return steps(AAA, lr, map) { it == ZZZ }
}

fun PuzzleInput.part2(): Any {
    val (lr, map) = parse(this)
    var result = 1L
    val iter = map.keys.intIterator()
    while (iter.hasNext()) {
        val k = iter.nextInt()
        if (k % 36 != 0xA) continue
        result = result lcm steps(k, lr, map) { it % 36 == Z }.toLong()
    }
    return result
}

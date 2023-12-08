package de.skyrising.aoc2023

import de.skyrising.aoc.*

@Suppress("unused")
class BenchmarkDay8 : BenchmarkDayV1(8)

private inline fun steps(start: String, lr: String, map: Map<String, Pair<String, String>>, end: (String)->Boolean): Int {
    var pos = start
    var steps = 0
    while (!end(pos)) {
        val dir = lr[steps++ % lr.length]
        val (l, r) = map[pos]!!
        pos = if (dir == 'L') l else r
    }
    return steps
}

@Suppress("unused")
fun registerDay8() {
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
    fun parse(input: PuzzleInput) = input.lines[0] to mapOf(*input.lines.drop(2).map {
        val (k, v) = it.split(" = ")
        val (l, r) = v.substring(1, v.length - 1).split(", ")
        k to (l to r)
    }.toTypedArray())
    part1("") {
        val (lr, map) = parse(this)
        steps("AAA", lr, map) { it == "ZZZ" }
    }
    part2 {
        val (lr, map) = parse(this)
        map.keys.filter { it[2] == 'A' }.map { steps(it, lr, map) { it.endsWith("Z")}.toLong() }.reduce(Long::lcm)
    }
}
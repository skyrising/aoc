package de.skyrising.aoc2019

import de.skyrising.aoc.*

@Suppress("unused")
class BenchmarkDay3 : BenchmarkDayV1(3)

@Suppress("unused")
fun registerDay3() {
    val test = TestInput("""
        R8,U5,L5,D3
        U7,R6,D4,L4
    """)
    val test2 = TestInput("""
        R75,D30,R83,U83,L12,D49,R71,U7,L72
        U62,R66,U55,R34,D71,R55,D58,R83
    """)
    val test3 = TestInput("""
        R98,U47,R26,D63,R33,U87,L62,D20,R33,U53,R51
        U98,R91,D20,R16,D67,R40,U7,R15,U6,R7
    """)
    fun walk(wire: String, at: (Vec2i)->Unit) {
        var last = Vec2i.ZERO
        wire.split(',').forEach {
            val next = last + Vec2i.KNOWN[it.substring(0, 1)]!! * it.substring(1).toInt()
            for (p in Line2i(last, next)) {
                if (p == last) continue
                at(p)
            }
            last = next
        }
    }
    part1("Crossed Wires") {
        val points = mutableSetOf<Vec2i>()
        walk(lines[0]) {
            points += it
        }
        var minDist = Int.MAX_VALUE
        walk(lines[1]) {
            if (it in points) minDist = minOf(minDist, it.manhattanDistance(Vec2i.ZERO))
        }
        minDist
    }
    part2 {
        val steps1 = mutableMapOf<Vec2i, Int>()
        var step = 0
        walk(lines[0]) {
            steps1.putIfAbsent(it, ++step)
        }
        step = 0
        var minSteps = Int.MAX_VALUE
        walk(lines[1]) {
            step++
            if (it in steps1) minSteps = minOf(minSteps, step + steps1[it]!!)
        }
        minSteps
    }
}
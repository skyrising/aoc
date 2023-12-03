package de.skyrising.aoc2022

import de.skyrising.aoc.PuzzleInput
import de.skyrising.aoc.Vec2i
import de.skyrising.aoc.part1
import de.skyrising.aoc.part2
import kotlin.math.absoluteValue
import kotlin.math.sign

@Suppress("unused")
class BenchmarkDay9 : BenchmarkDayV1(9)

private fun simulateRope(input: PuzzleInput, knots: Int): Int {
    val path = mutableSetOf<Vec2i>()
    val pos = Array(knots) { Vec2i.ZERO }
    for (line in input.lines) {
        val (dir, steps) = line.split(" ")
        val headMoveVec = Vec2i.KNOWN[dir]!!
        repeat(steps.toInt()) {
            pos[0] += headMoveVec
            for (i in 1 until pos.size) {
                val diff = pos[i - 1] - pos[i]
                if (diff.x.absoluteValue >= 2 || diff.y.absoluteValue >= 2) {
                    pos[i] += Vec2i(diff.x.sign, diff.y.sign)
                }
            }
            path.add(pos.last())
        }
    }
    return path.size
}

@Suppress("unused")
fun registerDay9() {
    part1("Rope Bridge") {
        simulateRope(this, 2)
    }
    part2 {
        simulateRope(this, 10)
    }
}
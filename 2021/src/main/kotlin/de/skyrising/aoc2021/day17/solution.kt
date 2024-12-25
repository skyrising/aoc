@file:PuzzleName("Trick Shot")

package de.skyrising.aoc2021.day17

import de.skyrising.aoc.PuzzleInput
import de.skyrising.aoc.PuzzleName
import de.skyrising.aoc.TestInput

val test = TestInput("target area: x=20..30, y=-10..-5")

fun PuzzleInput.part1v0(): Any {
    val target = parseInput(this)
    val x2 = target.first.last
    val y1 = target.second.first
    var maxY = 0
    for (x in 1..x2) {
        for (y in 1..-y1) {
            val result = ProbeState(0, 0, x, y).simulate(target)
            if (result.hit) {
                maxY = maxOf(maxY, result.maxY)
                //println("$x,$y ${result.maxY}")
            }
        }
    }
    return maxY
}

fun PuzzleInput.part1v1(): Any {
    val target = parseInput(this)
    val vy = -target.second.first - 1
    return vy * (vy + 1) / 2
}

fun PuzzleInput.part2(): Any {
    val target = parseInput(this)
    val x2 = target.first.last
    val y1 = target.second.first
    var hits = 0
    for (x in 1..x2) {
        for (y in y1..-y1) {
            val result = ProbeState(0, 0, x, y).simulate(target)
            if (result.hit) {
                hits++
                //println("$x,$y ${result.maxY}")
            }
        }
    }
    return hits
}

private fun parseInput(input: PuzzleInput): Pair<IntRange, IntRange> {
    val comma = input.chars.indexOf(',')
    val (x1, x2) = input.chars.substring(15, comma).split("..").map(String::toInt)
    val (y1, y2) = input.chars.substring(comma + 4).trimEnd().split("..").map(String::toInt)
    return Pair(x1..x2, y1..y2)
}

data class ProbeState(val x: Int, val y: Int, val vx: Int, val vy: Int) {
    fun step(): ProbeState {
        val x = this.x + vx
        val y = this.y + vy
        val vx = if (this.vx > 0) this.vx - 1 else 0
        val vy = this.vy - 1
        return ProbeState(x, y, vx, vy)
    }

    fun simulate(target: Pair<IntRange, IntRange>): ProbeResult {
        var state = this
        var steps = 0
        var maxY = state.y
        //println(target)
        while (true) {
            //println(state)
            if (state.x in target.first && state.y in target.second) return ProbeResult(true, state.x, state.y, maxY)
            if (state.x > target.first.last || state.y < target.second.first) return ProbeResult(false, state.x, state.y, maxY)
            state = state.step()
            maxY = maxOf(maxY, state.y)
            steps++
        }
    }
}

data class ProbeResult(val hit: Boolean, val x: Int, val y: Int, val maxY: Int)

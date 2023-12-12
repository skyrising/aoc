package de.skyrising.aoc2015.day2

import de.skyrising.aoc.BenchmarkBaseV1
import de.skyrising.aoc.TestInput
import de.skyrising.aoc.part1
import de.skyrising.aoc.part2

@Suppress("unused")
class BenchmarkDay : BenchmarkBaseV1(2015, 2)

@Suppress("unused")
fun register() {
    val test = TestInput("""
        2x3x4
        1x1x10
    """)
    part1("I Was Told There Would Be No Math") {
        var total = 0
        for (line in lines) {
            val (ls, ws, hs) = line.split('x', limit = 3)
            val l = ls.toInt()
            val w = ws.toInt()
            val h = hs.toInt()
            val side1 = l * w
            val side2 = w * h
            val side3 = h * l
            total += side1 * 2 + side2 * 2 + side3 * 2 + minOf(side1, side2, side3)
        }
        total
    }
    part2 {
        var total = 0
        for (line in lines) {
            val (ls, ws, hs) = line.split('x', limit = 3)
            val l = ls.toInt()
            val w = ws.toInt()
            val h = hs.toInt()
            val v = l * w * h
            val face1 = l + l + w + w
            val face2 = w + w + h + h
            val face3 = h + h + l + l
            total += minOf(face1, face2, face3) + v
        }
        total
    }
}
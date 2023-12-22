package de.skyrising.aoc2015.day2

import de.skyrising.aoc.*

val test = TestInput("""
    2x3x4
    1x1x10
""")

@PuzzleName("I Was Told There Would Be No Math")
fun PuzzleInput.part1(): Any {
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
    return total
}

fun PuzzleInput.part2(): Any {
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
    return total
}
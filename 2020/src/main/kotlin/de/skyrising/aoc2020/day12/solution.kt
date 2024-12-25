@file:PuzzleName("Rain Risk")

package de.skyrising.aoc2020.day12

import de.skyrising.aoc.PuzzleInput
import de.skyrising.aoc.PuzzleName
import de.skyrising.aoc.TestInput
import kotlin.math.*

val test = TestInput("""
    F10
    N3
    F7
    R90
    F11
""")

fun PuzzleInput.part1(): Any {
    var x = 0
    var y = 0
    var angle = 0
    for (line in lines) {
        val command = line[0]
        val amount = line.substring(1).toInt()
        when (command) {
            'N' -> y += amount
            'S' -> y -= amount
            'E' -> x += amount
            'W' -> x -= amount
            'L' -> angle += amount
            'R' -> angle -= amount
            'F' -> {
                val rad = Math.toRadians(angle.toDouble())
                x += round(cos(rad) * amount).toInt()
                y += round(sin(rad) * amount).toInt()
            }
        }
    }
    return abs(x) + abs(y)
}

fun PuzzleInput.part2(): Any {
    var x = 0
    var y = 0
    var wx = 10
    var wy = 1
    for (line in lines) {
        val command = line[0]
        val amount = line.substring(1).toInt()
        when (command) {
            'N' -> wy += amount
            'S' -> wy -= amount
            'E' -> wx += amount
            'W' -> wx -= amount
            'L' -> {
                val dist = sqrt((wx * wx + wy * wy).toDouble())
                val r = atan2(wy.toDouble(), wx.toDouble()) + Math.toRadians(amount.toDouble())
                wx = round(cos(r) * dist).toInt()
                wy = round(sin(r) * dist).toInt()
            }
            'R' -> {
                val dist = sqrt((wx * wx + wy * wy).toDouble())
                val r = atan2(wy.toDouble(), wx.toDouble()) - Math.toRadians(amount.toDouble())
                wx = round(cos(r) * dist).toInt()
                wy = round(sin(r) * dist).toInt()
            }
            'F' -> {
                x += wx * amount
                y += wy * amount
            }
        }
    }
    return abs(x) + abs(y)
}

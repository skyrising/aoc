package de.skyrising.aoc2020

import kotlin.math.*

class BenchmarkDay12 : BenchmarkDayV1(12)

fun registerDay12() {
    val test = """
        F10
        N3
        F7
        R90
        F11
        """.trimIndent().split("\n")
    puzzleLS(12, "Rain Risk v1") {
        var x = 0
        var y = 0
        var angle = 0
        for (line in it) {
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
        abs(x) + abs(y)
    }
    puzzleLS(12, "Part 2 v1") {
        var x = 0
        var y = 0
        var wx = 10
        var wy = 1
        for (line in it) {
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
        abs(x) + abs(y)
    }
}
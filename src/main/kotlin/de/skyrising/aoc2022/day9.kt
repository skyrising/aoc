package de.skyrising.aoc2022

import de.skyrising.aoc.Vec2i
import kotlin.math.absoluteValue
import kotlin.math.sign

class BenchmarkDay9 : BenchmarkDayV1(9)

val test = """
    R 4
    U 4
    L 3
    D 1
    R 4
    D 1
    L 5
    R 2
""".trimIndent().split('\n').filter(String::isNotBlank)

val test2 = """
    R 5
    U 8
    L 8
    D 3
    R 17
    D 10
    L 25
    U 20
""".trimIndent().split('\n').filter(String::isNotBlank)

fun registerDay9() {
    puzzleLS(9, "") {
        val path = mutableSetOf<Vec2i>()
        var headPos = Vec2i(0, 0)
        var tailPos = Vec2i(0, 0)
        for (line in it) {
            val (dir, steps) = line.split(" ")
            val headMoveVec = when (dir) {
                "U" -> Vec2i(0, -1)
                "D" -> Vec2i(0, 1)
                "L" -> Vec2i(-1, 0)
                "R" -> Vec2i(1, 0)
                else -> error("Invalid direction: $dir")
            }
            repeat(steps.toInt()) {
                headPos += headMoveVec
                val diff = headPos - tailPos
                val tailMoveVec = when {
                    diff.x.absoluteValue >= 2 || diff.y.absoluteValue >= 2 -> Vec2i(diff.x.sign, diff.y.sign)
                    else -> Vec2i(0, 0)
                }
                tailPos += tailMoveVec
                path.add(tailPos)
            }
        }
        path.size
    }
    puzzleLS(9, "Part Two") {
        val path = mutableSetOf<Vec2i>()
        var pos = Array(10) { Vec2i(0, 0) }
        for (line in it) {
            val (dir, steps) = line.split(" ")
            val headMoveVec = when (dir) {
                "U" -> Vec2i(0, -1)
                "D" -> Vec2i(0, 1)
                "L" -> Vec2i(-1, 0)
                "R" -> Vec2i(1, 0)
                else -> error("Invalid direction: $dir")
            }
            repeat(steps.toInt()) {
                pos[0] += headMoveVec
                for (i in 1 until pos.size) {
                    val diff = pos[i - 1] - pos[i]
                    val tailMoveVec = when {
                        diff.x.absoluteValue >= 2 || diff.y.absoluteValue >= 2 -> Vec2i(diff.x.sign, diff.y.sign)
                        else -> Vec2i(0, 0)
                    }
                    pos[i] += tailMoveVec
                }
                path.add(pos.last())
            }
        }
        path.size
    }
}
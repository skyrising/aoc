package de.skyrising.aoc2024.day13

import de.skyrising.aoc.*
import kotlin.math.roundToLong

val test = TestInput("""
Button A: X+94, Y+34
Button B: X+22, Y+67
Prize: X=8400, Y=5400

Button A: X+26, Y+66
Button B: X+67, Y+21
Prize: X=12748, Y=12176

Button A: X+17, Y+86
Button B: X+84, Y+37
Prize: X=7870, Y=6450

Button A: X+69, Y+23
Button B: X+27, Y+71
Prize: X=18641, Y=10279
""")

fun solveGame(a: Vec2l, b: Vec2l, prize: Vec2l): Vec2l? {
    val invDet = 1.0 / (a.x * b.y - b.x * a.y)
    val numA = ((b.y * prize.x - b.x * prize.y) * invDet).roundToLong()
    val numB = ((-a.y * prize.x + a.x * prize.y) * invDet).roundToLong()
    return if (numA * a + numB * b == prize) Vec2l(numA, numB) else null
}

@PuzzleName("Claw Contraption")
fun PuzzleInput.part1(): Any {
    val games = lines.splitOnEmpty().map { it.map { val (x, y) = it.longs(); Vec2l(x, y) } }
    return games.sumOf {(a, b, prize) ->
        (solveGame(a, b, prize) ?: Vec2l.ZERO).dot(Vec2l(3, 1))
    }
}

fun PuzzleInput.part2(): Any {
    val games = lines.splitOnEmpty().map { it.map { val (x, y) = it.longs(); Vec2l(x, y) } }
    return games.sumOf {(a, b, prize) ->
        (solveGame(a, b, prize + 10000000000000L) ?: Vec2l.ZERO).dot(Vec2l(3, 1))
    }
}

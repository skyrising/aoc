package de.skyrising.aoc2024.day13

import de.skyrising.aoc.*
import java.nio.ByteBuffer

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

inline fun List<ByteBuffer>.sumGames(block: (Vec2l, Vec2l, Vec2l) -> Long): Long {
    var sum = 0L
    for (index in 0 until (size + 1) / 4) {
        val j = index * 4
        val aLine = get(j)
        val a = Vec2l(aLine.toLong(12, 14), aLine.toLong(18, 20))
        val bLine = get(j + 1)
        val b = Vec2l(bLine.toLong(12, 14), bLine.toLong(18, 20))
        val prizeLine = get(j + 2)
        val pxStart = 9
        val pxEnd = prizeLine.indexOf(','.code.toByte(), pxStart)
        val pyStart = pxEnd + 4
        val pyEnd = prizeLine.remaining()
        val prize = Vec2l(prizeLine.toLong(pxStart, pxEnd), prizeLine.toLong(pyStart, pyEnd))
        sum += block(a, b, prize)
    }
    return sum
}

fun solveGame(a: Vec2l, b: Vec2l, prize: Vec2l): Long {
    val det = a.x * b.y - b.x * a.y
    // (numA, numB) = adj(M) * prize / det(M), rounded to nearest integer
    val numA = ((b.y * prize.x - b.x * prize.y) + det / 2) / det
    val numB = ((-a.y * prize.x + a.x * prize.y) + det / 2) / det
    return if (numA * a + numB * b == prize) numA * 3 + numB else 0
}

@PuzzleName("Claw Contraption")
fun PuzzleInput.part1() = byteLines.sumGames { a, b, prize ->
    solveGame(a, b, prize)
}

fun PuzzleInput.part2() = byteLines.sumGames { a, b, prize ->
    solveGame(a, b, prize + 10000000000000L)
}

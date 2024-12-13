package de.skyrising.aoc2024.day13

import com.microsoft.z3.ArithExpr
import com.microsoft.z3.Context
import com.microsoft.z3.IntSort
import com.microsoft.z3.Status
import de.skyrising.aoc.*

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

@PuzzleName("Claw Contraption")
fun PuzzleInput.part1(): Any {
    val games = lines.splitOnEmpty().map { it.map { val (x, y) = it.ints(); Vec2i(x, y) } }
    return games.sumOf {(a, b, prize) ->
        val maxA = minOf(prize.x / a.x, prize.y / a.y)
        for (numA in maxA downTo 0) {
            val numB = (prize.x - numA * a.x) / b.x
            if (numA * a + numB * b == prize) return@sumOf numA * 3 + numB
        }
        0
    }
}

fun PuzzleInput.part2(): Any {
    val games = lines.splitOnEmpty().map { it.map { val (x, y) = it.ints(); Vec2i(x, y) } }
    val offset = 10000000000000L
    Context().run {
        val (numA, numB) = mkIntConst("numA", "numB")
        return games.sumOf { (a, b, prize) ->
            val opt = mkOptimize()
            val realPrizeX = offset + prize.x
            val realPrizeY = offset + prize.y
            opt += numA * a.x + numB * b.x eq realPrizeX
            opt += numA * a.y + numB * b.y eq realPrizeY
            val handle = opt.MkMinimize((numA * 3) as ArithExpr<IntSort> + numB)
            if (opt.Check() != Status.SATISFIABLE) 0 else handle.value.toLong()
        }
    }
}

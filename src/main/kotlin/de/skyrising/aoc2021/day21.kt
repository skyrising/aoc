package de.skyrising.aoc2021

import it.unimi.dsi.fastutil.objects.Object2LongLinkedOpenHashMap

class BenchmarkDay21 : BenchmarkDayV1(21)

fun registerDay21() {
    val test = listOf(
        "Player 1 starting position: 4",
        "Player 2 starting position: 8"
    )
    puzzleLS(21, "Dirac Dice") {
        var (p1, p2) = parseInput21(it)
        val dice = Dice()
        var score1 = 0
        var score2 = 0
        var rolls = 0
        while (score2 < 1000) {
            p1 = move(p1, dice.next() + dice.next() + dice.next())
            rolls += 3
            score1 += p1
            if (score1 >= 1000) break
            p2 = move(p2, dice.next() + dice.next() + dice.next())
            rolls += 3
            score2 += p2
        }
        minOf(score1, score2) * rolls
    }
    puzzleLS(21, "Part Two") {
        val (i1, i2) = parseInput21(it)
        var wins1 = 0L
        var wins2 = 0L
        val universes = Object2LongLinkedOpenHashMap<Universe>()
        universes[Universe(i1, i2, 0, 0)] = 1L
        while (universes.isNotEmpty()) {
            val (universe, count) = universes.object2LongEntrySet().first()
            universes.removeLong(universe)
            val (p1, p2, score1, score2) = universe
            for (d11 in 1..3) for (d12 in 1..3) for (d13 in 1..3) {
                val p12 = move(p1, d11 + d12 + d13)
                val score12 = score1 + p12
                if (score12 >= 21) {
                    wins1 += count
                    continue
                }
                for (d21 in 1..3) for (d22 in 1..3) for (d23 in 1..3) {
                    val p22 = move(p2, d21 + d22 + d23)
                    val score22 = score2 + p22
                    if (score22 >= 21) {
                        wins2 += count
                        continue
                    }
                    val newUniverse = Universe(p12, p22, score12, score22)
                    universes[newUniverse] = universes.getLong(newUniverse) + count
                }
            }
        }
        maxOf(wins1, wins2)
    }
}

private fun move(p: Int, amount: Int) = (p + amount - 1) % 10 + 1

data class Universe(val p1: Int, val p2: Int, val score1: Int, val score2: Int)

data class Dice(var next: Int = 1) {
    fun next(): Int {
        val r = next
        next++
        if (next > 1000) next = 1
        return r
    }
}

private fun parseInput21(input: List<String>) = Pair(input[0][28].digitToInt(), input[1][28].digitToInt())
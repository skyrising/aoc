package de.skyrising.aoc2022

import de.skyrising.aoc.ints
import java.math.BigInteger

class BenchmarkDay11 : BenchmarkDayV1(11)

private val MOD = (2 * 3 * 5 * 7 * 11 * 13 * 17 * 19).toBigInteger()

data class Monkey(val items: MutableList<BigInteger>, val op: (BigInteger) -> BigInteger, val divisor: BigInteger, val trueMonkey: Int, val falseMonkey: Int) {
    var inspected = 0L

    fun step(monkeys: List<Monkey>, divideByThree: Boolean) {
        for (item in items) {
            var newLevel = op(item)
            if (divideByThree) {
                newLevel /= 3.toBigInteger()
            } else {
                newLevel %= MOD
            }
            val throwTo = if (newLevel % divisor == BigInteger.ZERO) trueMonkey else falseMonkey
            monkeys[throwTo].items.add(newLevel)
            inspected++
        }
        items.clear()
    }
}

private fun parseInput(input: List<String>): List<Monkey> {
    return input.chunked(7) {
        val startingItems = it[1].ints()
        val opText = it[2].substringAfter("= ")
        val op: (BigInteger) -> BigInteger = when {
            opText == "old * old" -> { x -> x * x }
            opText[4] == '*' -> { x -> x * opText.ints().getInt(0).toBigInteger() }
            opText[4] == '+' -> { x -> x + opText.ints().getInt(0).toBigInteger() }
            else -> error("Unknown op: $opText")
        }
        val divisor = it[3].ints().getInt(0)
        val trueMonkey = it[4].ints().getInt(0)
        val falseMonkey = it[5].ints().getInt(0)
        Monkey(startingItems.mapTo(mutableListOf(), Int::toBigInteger), op, BigInteger.valueOf(divisor.toLong()), trueMonkey, falseMonkey)
    }
}

fun registerDay11() {
    puzzleLS(11, "Monkey in the Middle") {
        val monkeys = parseInput(it)
        repeat(20) {
            for (monkey in monkeys) {
                monkey.step(monkeys, true)
            }
        }
        val (a, b) = monkeys.sortedByDescending(Monkey::inspected).take(2)
        a.inspected * b.inspected
    }
    puzzleLS(11, "Part Two") {
        val monkeys = parseInput(it)
        repeat(10000) {
            for (monkey in monkeys) {
                monkey.step(monkeys, false)
            }
        }
        val (a, b) = monkeys.sortedByDescending(Monkey::inspected).take(2)
        a.inspected * b.inspected
    }
}
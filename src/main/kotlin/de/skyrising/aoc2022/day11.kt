package de.skyrising.aoc2022

import de.skyrising.aoc.PuzzleInput
import de.skyrising.aoc.ints
import it.unimi.dsi.fastutil.longs.LongArrayList
import it.unimi.dsi.fastutil.longs.LongList
import java.util.function.LongConsumer

class BenchmarkDay11 : BenchmarkDayV1(11)

data class Monkey(val items: LongList, val op: (Long) -> Long, val divisor: Int, val trueMonkey: Int, val falseMonkey: Int) {
    var inspected = 0L

    fun step(monkeys: List<Monkey>, divideByThree: Boolean) {
        items.forEach(LongConsumer { item ->
            var newLevel = op(item)
            if (divideByThree) {
                newLevel /= 3
            }
            newLevel %= 2 * 3 * 5 * 7 * 11 * 13 * 17 * 19
            val throwTo = if (newLevel % divisor == 0L) trueMonkey else falseMonkey
            monkeys[throwTo].items.add(newLevel)
            inspected++
        })
        items.clear()
    }
}

private fun monkeyBusiness(input: PuzzleInput, steps: Int, divideByThree: Boolean): Long {
    val monkeys = parseInput(input)
    repeat(steps) {
        for (monkey in monkeys) {
            monkey.step(monkeys, divideByThree)
        }
    }
    val (a, b) = monkeys.sortedByDescending(Monkey::inspected).take(2)
    return a.inspected * b.inspected
}

private fun parseInput(input: PuzzleInput): List<Monkey> {
    return input.lines.chunked(7) {
        val startingItems = it[1].ints()
        val opText = it[2].substringAfter("= ")
        val split = opText.split(' ')
        val op: (Long) -> Long = when {
            split[2] == "old" -> { x -> x * x }
            split[1] == "*" -> {
                val factor = split[2].toInt();
                { x -> x * factor }
            }
            split[1] == "+" -> {
                val addend = split[2].toInt();
                { x -> x + addend }
            }
            else -> error("Unknown op: $opText")
        }
        val divisor = it[3].ints().getInt(0)
        val trueMonkey = it[4].ints().getInt(0)
        val falseMonkey = it[5].ints().getInt(0)
        Monkey(startingItems.mapTo(LongArrayList(), Int::toLong), op, divisor, trueMonkey, falseMonkey)
    }
}

fun registerDay11() {
    puzzle(11, "Monkey in the Middle") {
        monkeyBusiness(this, 20, true)
    }
    puzzle(11, "Part Two") {
        monkeyBusiness(this, 10000, false)
    }
}
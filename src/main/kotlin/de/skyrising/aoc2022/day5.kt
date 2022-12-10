package de.skyrising.aoc2022

import de.skyrising.aoc.ints
import it.unimi.dsi.fastutil.chars.CharArrayList

class BenchmarkDay5 : BenchmarkDayV1(5)

private fun parseInput(it: List<String>): Pair<List<String>, Array<CharArrayList>> {
    val splitPoint = it.indexOf("")
    val setup = it.subList(0, splitPoint - 1)
    val moves = it.subList(splitPoint + 1, it.size)
    val width = (setup[0].length + 1) / 4
    val stacks = Array(width) { CharArrayList() }
    for (depth in setup.size - 1 downTo 0) {
        val line = setup[depth]
        for (i in 0 until width) {
            val char = line[i * 4 + 1]
            if (char != ' ') stacks[i].add(char)
        }
    }
    return moves to stacks
}

private fun Array<CharArrayList>.toResult(): String {
    val result = StringBuilder()
    for (element in this) {
        result.append(element.getChar(element.lastIndex))
    }
    return result.toString()
}

fun registerDay5() {
    puzzleLS(5, "Supply Stacks") {
        val (moves, stacks) = parseInput(it)
        for (move in moves) {
            val (count, from, to) = move.ints()
            repeat(count) {
                stacks[to - 1].add(stacks[from - 1].removeChar(stacks[from - 1].lastIndex))
            }
        }
        stacks.toResult()
    }

    puzzleLS(5, "Part Two") {
        val (moves, stacks) = parseInput(it)
        for (move in moves) {
            val (count, from, to) = move.ints()
            val indexFrom = stacks[from - 1].size - count
            val indexTo = stacks[from - 1].size
            stacks[to - 1].addAll(stacks[from - 1].subList(indexFrom, indexTo))
            stacks[from - 1].removeElements(indexFrom, indexTo)
        }
        stacks.toResult()
    }
}
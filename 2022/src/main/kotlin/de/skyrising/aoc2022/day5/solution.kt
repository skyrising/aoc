package de.skyrising.aoc2022.day5

import de.skyrising.aoc.*
import it.unimi.dsi.fastutil.chars.CharArrayList

@Suppress("unused")
class BenchmarkDay : BenchmarkBaseV1(2022, 5)

private fun parseInput(input: PuzzleInput): Pair<List<String>, Array<CharArrayList>> {
    val splitPoint = input.lines.indexOf("")
    val setup = input.lines.subList(0, splitPoint - 1)
    val moves = input.lines.subList(splitPoint + 1, input.lines.size)
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

@Suppress("unused")
fun register() {
    part1("Supply Stacks") {
        val (moves, stacks) = parseInput(this)
        for (move in moves) {
            val (count, from, to) = move.ints()
            repeat(count) {
                stacks[to - 1].add(stacks[from - 1].removeChar(stacks[from - 1].lastIndex))
            }
        }
        stacks.toResult()
    }

    part2 {
        val (moves, stacks) = parseInput(this)
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
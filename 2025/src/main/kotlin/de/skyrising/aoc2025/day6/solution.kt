@file:PuzzleName("Trash Compactor")

package de.skyrising.aoc2025.day6

import de.skyrising.aoc.PuzzleInput
import de.skyrising.aoc.PuzzleName
import de.skyrising.aoc.TestInput
import de.skyrising.aoc.longs

val test = TestInput("""
123 328  51 64 
 45 64  387 23 
  6 98  215 314
*   +   *   +  
""")

fun PuzzleInput.part1(): Any {
    val lines = lines
    val numCount = lines.lastIndex
    val numbers = lines.subList(0, numCount).map(String::longs)
    var i = 0
    var sum = 0L
    for (c in lines.last()) {
        if (c == ' ') continue
        var n = numbers[0].getLong(i)
        for (j in 1 ..< numCount) {
            val m = numbers[j].getLong(i)
            when (c) {
                '*' -> n *= m
                '+' -> n += m
                else -> throw IllegalArgumentException()
            }
        }
        i++
        sum += n
    }
    return sum
}

fun PuzzleInput.part2(): Any {
    val lines = lines
    val numCount = lines.lastIndex
    val opLine = lines.last()
    val operatorIndexes = opLine.withIndex().filter { it.value != ' ' }.map { it.index }
    val maxLength = lines.maxOf { it.length }
    var sum = 0L
    for (i in operatorIndexes.indices) {
        val end = if (i == operatorIndexes.lastIndex) maxLength - 1 else operatorIndexes[i + 1] - 2
        val op = opLine[operatorIndexes[i]]
        var result = if (op == '*') 1L else 0L
        for (j in end downTo operatorIndexes[i]) {
            var n = 0L
            for (k in 0 ..< numCount) {
                val c = lines[k][j]
                if (c == ' ') continue
                n = n * 10 + c.digitToInt()
            }
            result = if (op == '*') result * n else result + n
        }
        sum += result
    }
    return sum
}

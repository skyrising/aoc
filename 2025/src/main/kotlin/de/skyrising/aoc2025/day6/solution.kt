@file:PuzzleName("Trash Compactor")

package de.skyrising.aoc2025.day6

import de.skyrising.aoc.PuzzleInput
import de.skyrising.aoc.PuzzleName
import de.skyrising.aoc.TestInput
import java.nio.ByteBuffer

val test = TestInput("""
123 328  51 64 
 45 64  387 23 
  6 98  215 314
*   +   *   +  
""")

data class Prepared(val lines: List<ByteBuffer>, val opLine: ByteBuffer, val maxLength: Int) {
    inline fun cephalopodMath(problem: Prepared.(Char,Int, Int) -> Long): Long {
        val opArr = opLine.array()
        val opOffset = opLine.arrayOffset()
        var end = maxLength - 1
        var start = minOf(end, opLine.limit() - 1)
        var sum = 0L
        while (end >= 0 && start >= 0) {
            when(val op = opArr[opOffset + start].toInt().toChar()) {
                ' ' -> start--
                else -> {
                    sum += problem(op, start, end)
                    end = start - 2
                    start = end
                }
            }
        }
        return sum
    }
}

fun PuzzleInput.prepare(): Prepared {
    val lines = byteLines
    return Prepared(lines.subList(0, lines.lastIndex), lines.last(), lines.maxOf { it.remaining() })
}

fun Prepared.part1() = cephalopodMath { op, start, end ->
    var result = if (op == '*') 1L else 0L
    for (k in lines.indices) {
        val line = lines[k]
        var n = 0L
        for (j in start..end) {
            val c = line[j].toInt()
            if (c == ' '.code) continue
            n = n * 10 + c - '0'.code
        }
        result = if (op == '*') result * n else result + n
    }
    result
}

fun Prepared.part2() = cephalopodMath { op, start, end ->
    var result = if (op == '*') 1L else 0L
    for (j in end downTo start) {
        var n = 0L
        for (k in lines.indices) {
            val c = lines[k][j].toInt()
            if (c == ' '.code) continue
            n = n * 10 + c - '0'.code
        }
        result = if (op == '*') result * n else result + n
    }
    result
}

package de.skyrising.aoc2022.day25

import de.skyrising.aoc.*
import it.unimi.dsi.fastutil.ints.IntArrayList

private fun snafuDecode(number: String): Long {
    var result = 0L
    for (c in number) {
        val value = when (c) {
            '0' -> 0
            '1' -> 1
            '2' -> 2
            '-' -> -1
            '=' -> -2
            else -> error("Invalid character $c")
        }
        result = result * 5 + value
    }
    return result
}

private fun snafuEncode(number: Long): String {
    val digits = IntArrayList()
    var n = number
    while (n != 0L) {
        digits.add((n % 5).toInt())
        n /= 5
    }
    digits.add(0)
    for (i in digits.indices) {
        val d = digits.getInt(i)
        if (d >= 3) {
            digits[i] = d - 5
            digits[i + 1] = digits.getInt(i + 1) + 1
        }
    }
    if (digits.last() == 0) digits.removeInt(digits.size - 1)
    val sb = StringBuilder()
    for (i in digits.size - 1 downTo 0) {
        val d = digits.getInt(i)
        sb.append(when (d) {
            0 -> '0'
            1 -> '1'
            2 -> '2'
            -1 -> '-'
            -2 -> '='
            else -> error("Invalid digit $d")
        })
    }
    return sb.toString()
}

val test = TestInput("""
    1=-0-2
    12111
    2=0=
    21
    2=01
    111
    20012
    112
    1=-1=
    1-12
    12
    1=
    122
""")

@PuzzleName("Full of Hot Air")
fun PuzzleInput.part1() = snafuEncode(lines.map(::snafuDecode).sum())
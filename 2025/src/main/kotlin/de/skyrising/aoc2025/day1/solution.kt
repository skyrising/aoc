@file:PuzzleName("Secret Entrance")

package de.skyrising.aoc2025.day1

import de.skyrising.aoc.*
import it.unimi.dsi.fastutil.ints.IntList
import kotlin.math.absoluteValue

val test = TestInput("""
L68
L30
R48
L5
R60
L55
L1
L99
R14
L82
""")

fun PuzzleInput.prepare() = byteLines.mapToInts { it[0].toSignum('L'.code.toByte()) * it.toInt(1) }

fun IntList.part1(): Int {
    var pos = 50
    return count {
        pos = (pos + it).mod(100)
        pos == 0
    }
}

fun IntList.part2(): Int {
    var pos = 50
    return sumOf {
        pos += it
        val s = pos ushr 31
        val d = (pos + s).absoluteValue / 100 + s
        val r = pos % 100
        pos = r + 100.ifBit(s and r.isZeroInt)
        d
    }
}
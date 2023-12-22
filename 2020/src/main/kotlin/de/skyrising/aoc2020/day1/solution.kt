package de.skyrising.aoc2020.day1

import de.skyrising.aoc.*
import it.unimi.dsi.fastutil.ints.IntOpenHashSet
import java.nio.ByteBuffer

private fun parseInt4(s: ByteBuffer) = when (s.remaining()) {
    1 -> s[0] - '0'.code.toByte()
    2 -> (s[1] - '0'.code.toByte()) + 10 * (s[0] - '0'.code.toByte())
    3 -> (s[2] - '0'.code.toByte()) + 10 * ((s[1] - '0'.code.toByte()) + 10 * (s[0] - '0'.code.toByte()))
    4 -> (s[3] - '0'.code.toByte()) + 10 * ((s[2] - '0'.code.toByte()) + 10 * ((s[1] - '0'.code.toByte()) + 10 * (s[0] - '0'.code.toByte())))
    else -> throw IllegalArgumentException()
}

@PuzzleName("Report Repair")
fun PuzzleInput.part1v0(): Any {
    val numbers = IntOpenHashSet()
    for (line in lines) {
        val num = line.toInt()
        val other = 2020 - num
        if (numbers.contains(other)) return num * other
        numbers.add(num)
    }
    return 0
}

@PuzzleName("Report Repair")
fun PuzzleInput.part1v1(): Any {
    val numbers = LongArray(2048 shr 6)
    for (line in byteLines) {
        val num = parseInt4(line)
        val other = 2020 - num
        if (isBitSet(numbers, other)) return num * other
        setBit(numbers, num)
    }
    return 0
}

fun PuzzleInput.part2v0(): Any {
    val numbers = IntOpenHashSet()
    for (line in lines) {
        val a = line.toInt()
        for (b in numbers.iterator()) {
            val c = 2020 - a - b
            if (numbers.contains(c)) return a * b * c
        }
        numbers.add(a)
    }
    return 0
}

fun PuzzleInput.part2v1(): Any {
    val numbers = LongArray(2048 shr 6)
    for (line in byteLines) {
        val a = parseInt4(line)
        for (b in 0 until 1010) {
            if (!isBitSet(numbers, b)) continue
            val c = 2020 - a - b
            if (c < 0) break
            if (isBitSet(numbers, c)) return a * b * c
        }
        setBit(numbers, a)
    }
    return 0
}
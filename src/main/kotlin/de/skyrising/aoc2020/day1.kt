package de.skyrising.aoc2020

import it.unimi.dsi.fastutil.ints.IntOpenHashSet
import java.nio.ByteBuffer

class BenchmarkDay1 : BenchmarkDay(1)

fun parseInt4(s: ByteBuffer) = when (s.remaining()) {
    1 -> s[0] - '0'.toByte()
    2 -> (s[1] - '0'.toByte()) + 10 * (s[0] - '0'.toByte())
    3 -> (s[2] - '0'.toByte()) + 10 * ((s[1] - '0'.toByte()) + 10 * (s[0] - '0'.toByte()))
    4 -> (s[3] - '0'.toByte()) + 10 * ((s[2] - '0'.toByte()) + 10 * ((s[1] - '0'.toByte()) + 10 * (s[0] - '0'.toByte())))
    else -> throw IllegalArgumentException()
}

fun registerDay1() {
    puzzleLS(1, "Report Repair v1") {
        val numbers = IntOpenHashSet()
        for (line in it) {
            val num = line.toInt()
            val other = 2020 - num
            if (numbers.contains(other)) return@puzzleLS num * other
            numbers.add(num)
        }
        0
    }
    puzzleLB(1, "Report Repair v2") {
        val numbers = LongArray(2048 shr 6)
        for (line in it) {
            val num = parseInt4(line)
            val other = 2020 - num
            if (isBitSet(numbers, other)) return@puzzleLB num * other
            setBit(numbers, num)
        }
        0
    }
    puzzleLS(1, "Part Two v1") {
        val numbers = IntOpenHashSet()
        for (line in it) {
            val a = line.toInt()
            for (b in numbers.iterator()) {
                val c = 2020 - a - b
                if (numbers.contains(c)) return@puzzleLS a * b * c
            }
            numbers.add(a)
        }
        0
    }
    puzzleLB(1, "Part Two v2") {
        val numbers = LongArray(2048 shr 6)
        for (line in it) {
            val a = parseInt4(line)
            for (b in 0 until 1010) {
                if (!isBitSet(numbers, b)) continue
                val c = 2020 - a - b
                if (c < 0) break
                if (isBitSet(numbers, c)) return@puzzleLB a * b * c
            }
            setBit(numbers, a)
        }
        0
    }
}
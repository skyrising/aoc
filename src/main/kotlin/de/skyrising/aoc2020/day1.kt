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

fun isBitSet(longs: LongArray, i: Int): Boolean {
    return (longs[i shr 6] shr (i and 0x3f)) and 1 != 0L
}

fun setBit(longs: LongArray, i: Int) {
    val idx = i shr 6
    longs[idx] = longs[idx] or (1L shl (i and 0x3f))
}

fun registerDay1() {
    puzzleS(1, "Report Repair v1") {
        val numbers = IntOpenHashSet()
        for (line in it) {
            val num = line.toInt()
            val other = 2020 - num
            if (numbers.contains(other)) return@puzzleS num * other
            numbers.add(num)
        }
        0
    }
    puzzleB(1, "Report Repair v2") {
        val numbers = LongArray(2048 shr 6)
        for (line in it) {
            val num = parseInt4(line)
            val other = 2020 - num
            if (isBitSet(numbers, other)) return@puzzleB num * other
            setBit(numbers, num)
        }
        0
    }
    puzzleS(1, "Part Two v1") {
        val numbers = IntOpenHashSet()
        for (line in it) {
            val a = line.toInt()
            for (b in numbers.iterator()) {
                val c = 2020 - a - b
                if (numbers.contains(c)) return@puzzleS a * b * c
            }
            numbers.add(a)
        }
        0
    }
    puzzleB(1, "Part Two v2") {
        val numbers = LongArray(2048 shr 6)
        for (line in it) {
            val a = parseInt4(line)
            for (b in 0 until 1010) {
                if (!isBitSet(numbers, b)) continue
                val c = 2020 - a - b
                if (c < 0) break
                if (isBitSet(numbers, c)) return@puzzleB a * b * c
            }
            setBit(numbers, a)
        }
        0
    }
}
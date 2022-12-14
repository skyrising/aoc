package de.skyrising.aoc2020

import de.skyrising.aoc.isBitSet
import de.skyrising.aoc.setBit
import it.unimi.dsi.fastutil.ints.IntOpenHashSet
import java.nio.ByteBuffer

class BenchmarkDay1 : BenchmarkDay(1)

private fun parseInt4(s: ByteBuffer) = when (s.remaining()) {
    1 -> s[0] - '0'.toByte()
    2 -> (s[1] - '0'.toByte()) + 10 * (s[0] - '0'.toByte())
    3 -> (s[2] - '0'.toByte()) + 10 * ((s[1] - '0'.toByte()) + 10 * (s[0] - '0'.toByte()))
    4 -> (s[3] - '0'.toByte()) + 10 * ((s[2] - '0'.toByte()) + 10 * ((s[1] - '0'.toByte()) + 10 * (s[0] - '0'.toByte())))
    else -> throw IllegalArgumentException()
}

fun registerDay1() {
    puzzle(1, "Report Repair v1") {
        val numbers = IntOpenHashSet()
        for (line in lines) {
            val num = line.toInt()
            val other = 2020 - num
            if (numbers.contains(other)) return@puzzle num * other
            numbers.add(num)
        }
        0
    }
    puzzle(1, "Report Repair v2") {
        val numbers = LongArray(2048 shr 6)
        for (line in byteLines) {
            val num = parseInt4(line)
            val other = 2020 - num
            if (isBitSet(numbers, other)) return@puzzle num * other
            setBit(numbers, num)
        }
        0
    }
    puzzle(1, "Part Two v1") {
        val numbers = IntOpenHashSet()
        for (line in lines) {
            val a = line.toInt()
            for (b in numbers.iterator()) {
                val c = 2020 - a - b
                if (numbers.contains(c)) return@puzzle a * b * c
            }
            numbers.add(a)
        }
        0
    }
    puzzle(1, "Part Two v2") {
        val numbers = LongArray(2048 shr 6)
        for (line in byteLines) {
            val a = parseInt4(line)
            for (b in 0 until 1010) {
                if (!isBitSet(numbers, b)) continue
                val c = 2020 - a - b
                if (c < 0) break
                if (isBitSet(numbers, c)) return@puzzle a * b * c
            }
            setBit(numbers, a)
        }
        0
    }
}
package de.skyrising.aoc2020.day1

import de.skyrising.aoc.*
import it.unimi.dsi.fastutil.ints.IntOpenHashSet
import java.nio.ByteBuffer

@Suppress("unused")
class BenchmarkDay : BenchmarkBase(2020, 1)

private fun parseInt4(s: ByteBuffer) = when (s.remaining()) {
    1 -> s[0] - '0'.code.toByte()
    2 -> (s[1] - '0'.code.toByte()) + 10 * (s[0] - '0'.code.toByte())
    3 -> (s[2] - '0'.code.toByte()) + 10 * ((s[1] - '0'.code.toByte()) + 10 * (s[0] - '0'.code.toByte()))
    4 -> (s[3] - '0'.code.toByte()) + 10 * ((s[2] - '0'.code.toByte()) + 10 * ((s[1] - '0'.code.toByte()) + 10 * (s[0] - '0'.code.toByte())))
    else -> throw IllegalArgumentException()
}

@Suppress("unused")
fun register() {
    part1("Report Repair") {
        val numbers = IntOpenHashSet()
        for (line in lines) {
            val num = line.toInt()
            val other = 2020 - num
            if (numbers.contains(other)) return@part1 num * other
            numbers.add(num)
        }
        0
    }
    part1("Report Repair") {
        val numbers = LongArray(2048 shr 6)
        for (line in byteLines) {
            val num = parseInt4(line)
            val other = 2020 - num
            if (isBitSet(numbers, other)) return@part1 num * other
            setBit(numbers, num)
        }
        0
    }
    part2 {
        val numbers = IntOpenHashSet()
        for (line in lines) {
            val a = line.toInt()
            for (b in numbers.iterator()) {
                val c = 2020 - a - b
                if (numbers.contains(c)) return@part2 a * b * c
            }
            numbers.add(a)
        }
        0
    }
    part2 {
        val numbers = LongArray(2048 shr 6)
        for (line in byteLines) {
            val a = parseInt4(line)
            for (b in 0 until 1010) {
                if (!isBitSet(numbers, b)) continue
                val c = 2020 - a - b
                if (c < 0) break
                if (isBitSet(numbers, c)) return@part2 a * b * c
            }
            setBit(numbers, a)
        }
        0
    }
}
package de.skyrising.aoc2020

import de.skyrising.aoc.TestInput
import de.skyrising.aoc.part1
import de.skyrising.aoc.part2
import it.unimi.dsi.fastutil.HashCommon
import it.unimi.dsi.fastutil.longs.LongOpenHashSet

@Suppress("unused")
class BenchmarkDay9 : BenchmarkDay(9)

@Suppress("unused")
fun registerDay9() {
    val test = TestInput("""
        35
        20
        15
        25
        47
        40
        62
        55
        65
        95
        102
        117
        150
        182
        127
        219
        299
        277
        309
        576
    """)
    fun longArrayOf(numbers: List<String>): LongArray {
        val arr = LongArray(numbers.size)
        for (i in arr.indices) {
            arr[i] = numbers[i].toLong()
        }
        return arr
    }
    fun findInvalid(numbers: List<Long>, preambleLen: Int = 25): Long? {
        for (i in preambleLen until numbers.size) {
            val n = numbers[i]
            val set = LongOpenHashSet(preambleLen)
            var valid = false
            for (j in i - preambleLen until i) {
                val m = numbers[j]
                if ((n - m) in set) {
                    valid = true
                    break
                }
                set.add(m)
            }
            if (!valid) return n
        }
        return null
    }
    fun findInvalid2(numbers: LongArray, preambleLen: Int = 25): Long? {
        for (i in preambleLen until numbers.size) {
            val n = numbers[i]
            var bloom = 0L
            var valid = false
            middle@ for (j in i - preambleLen until i) {
                val m = numbers[j]
                val diff = n - m
                val mixed = HashCommon.mix(diff)
                if ((bloom and mixed) == mixed) {
                    for (k in i - preambleLen until j) {
                        if (numbers[k] == diff) {
                            valid = true
                            break@middle
                        }
                    }
                }
                bloom = bloom or HashCommon.mix(m)
            }
            if (!valid) return n
        }
        return null
    }
    part1("Encoding Error") {
        val numbers = lines.map(String::toLong)
        findInvalid(numbers)
    }
    part1("Encoding Error") {
        val numbers = longArrayOf(lines)
        findInvalid2(numbers)
    }
    part2 {
        val numbers = lines.map(String::toLong)
        val invalid = findInvalid(numbers) ?: return@part2 null
        for (i in numbers.indices) {
            var sum = 0L
            for (j in i until numbers.size) {
                sum += numbers[j]
                if (sum == invalid) {
                    val sorted = numbers.subList(i, j + 1).sorted()
                    return@part2 sorted[0] + sorted[sorted.size - 1]
                }
                if (sum > invalid) break
            }
        }
        return@part2 null
    }
    part2 {
        val numbers = longArrayOf(lines)
        val invalid = findInvalid2(numbers) ?: return@part2 null
        for (i in numbers.lastIndex downTo 0) {
            var sum = numbers[i]
            var min = sum
            var max = sum
            for (j in i + 1 until numbers.size) {
                val n = numbers[j]
                min = minOf(min, n)
                max = maxOf(max, n)
                sum += n
                if (sum == invalid && max != min) return@part2 min + max
                if (sum > invalid) break
            }
        }
        return@part2 null
    }
}
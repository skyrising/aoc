package de.skyrising.aoc2020.day9

import de.skyrising.aoc.*
import it.unimi.dsi.fastutil.HashCommon
import it.unimi.dsi.fastutil.longs.LongOpenHashSet

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

@PuzzleName("Encoding Error")
fun PuzzleInput.part1v0() = findInvalid(lines.map(String::toLong))

@PuzzleName("Encoding Error")
fun PuzzleInput.part1v1() = findInvalid2(longArrayOf(lines))

fun PuzzleInput.part2v0(): Any? {
    val numbers = lines.map(String::toLong)
    val invalid = findInvalid(numbers) ?: return null
    for (i in numbers.indices) {
        var sum = 0L
        for (j in i until numbers.size) {
            sum += numbers[j]
            if (sum == invalid) {
                val sorted = numbers.subList(i, j + 1).sorted()
                return sorted[0] + sorted[sorted.size - 1]
            }
            if (sum > invalid) break
        }
    }
    return null
}

fun PuzzleInput.part2v1(): Any? {
    val numbers = longArrayOf(lines)
    val invalid = findInvalid2(numbers) ?: return null
    for (i in numbers.lastIndex downTo 0) {
        var sum = numbers[i]
        var min = sum
        var max = sum
        for (j in i + 1 until numbers.size) {
            val n = numbers[j]
            min = minOf(min, n)
            max = maxOf(max, n)
            sum += n
            if (sum == invalid && max != min) return min + max
            if (sum > invalid) break
        }
    }
    return null
}

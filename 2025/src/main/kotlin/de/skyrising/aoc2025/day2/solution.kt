@file:PuzzleName("Gift Shop")

package de.skyrising.aoc2025.day2

import de.skyrising.aoc.*

val test = TestInput("""
    11-22,95-115,998-1012,1188511880-1188511890,222220-222224,
    1698522-1698528,446443-446449,38593856-38593862,565653-565659,
    824824821-824824827,2121212118-2121212124
""".replace(Regex("\\s"), ""))

fun PuzzleInput.prepare() = string.trim().split(',').map { it.toLongRange() }

inline fun List<LongRange>.sumInvalid(crossinline predicate: (CharSequence) -> Boolean): Long {
    return mapParallel {
        var sum = 0L
        val bytes = ByteArray(19)
        val cs = ByteView(bytes, bytes.size, bytes.size)
        for (element in it) {
            cs.start = element.getChars(bytes, 19)
            if (predicate(cs)) sum += element
        }
        sum
    }.sum()
}

fun List<LongRange>.part1() = sumInvalid { s ->
    if (s.length % 2 != 0) return@sumInvalid false
    val l = s.length / 2
    for (i in 0 ..< l) if (s[i] != s[l + i]) return@sumInvalid false
    true
}

fun List<LongRange>.part2() = sumInvalid { s ->
    outer@for (start in 1 ..< s.length) {
        for (i in s.indices) {
            var j = start + i
            if (j >= s.length) j -= s.length
            if (s[i] != s[j]) continue@outer
        }
        return@sumInvalid true
    }
    return@sumInvalid false
}
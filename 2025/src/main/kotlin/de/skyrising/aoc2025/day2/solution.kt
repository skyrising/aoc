@file:PuzzleName("Gift Shop")

package de.skyrising.aoc2025.day2

import de.skyrising.aoc.*

val test = TestInput("""
    11-22,95-115,998-1012,1188511880-1188511890,222220-222224,
    1698522-1698528,446443-446449,38593856-38593862,565653-565659,
    824824821-824824827,2121212118-2121212124
""".replace(Regex("\\s"), ""))

fun PuzzleInput.prepare() = string.replace('-', ';').longs().chunked(2) { it.toPair().toRange() }

fun List<LongRange>.part1(): Long {
    return sumOf {
        it.sumOf { v ->
            val s = v.toString()
            val l = s.length / 2
            if (s.take(l) == s.substring(l)) v else 0
        }
    }
}

fun List<LongRange>.part2(): Long {
    return sumOf {
        it.sumOf { v ->
            val s = v.toString()
            (if ((1..s.length / 2).any { i ->
                s.length % i == 0 && s.take(i).repeat(s.length / i) == s
            }) v else 0)
        }
    }
}
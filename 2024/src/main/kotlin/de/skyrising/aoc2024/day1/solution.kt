@file:PuzzleName("Historian Hysteria")

package de.skyrising.aoc2024.day1

import de.skyrising.aoc.*
import kotlin.math.absoluteValue

val test = TestInput("""
3   4
4   3
2   5
1   3
3   9
3   3
""")

fun PuzzleInput.prepare() = lines.map { it.ints().toPair() }.pivot()

fun Pair<List<Int>, List<Int>>.part1() = map { it.sorted() }.pivot().sumOf { (it.second - it.first).absoluteValue }

fun Pair<List<Int>, List<Int>>.part2() = first.sumOf { a -> a * second.count { it == a } }

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

@PuzzleName("Historian Hysteria")
fun PuzzleInput.part1() =
    lines.map { it.ints().toPair() }.pivot().map { it.sorted() }.pivot().sumOf { (it.second - it.first).absoluteValue }

fun PuzzleInput.part2(): Any {
    val (firstList, secondList) = lines.map { it.ints().toPair() }.pivot()
    return firstList.sumOf { a -> a * secondList.count { it == a } }
}

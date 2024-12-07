package de.skyrising.aoc2024.day2

import de.skyrising.aoc.PuzzleInput
import de.skyrising.aoc.PuzzleName
import de.skyrising.aoc.TestInput
import de.skyrising.aoc.ints
import kotlin.math.absoluteValue

val test = TestInput("""
7 6 4 2 1
1 2 7 8 9
9 7 6 2 1
1 3 2 4 5
8 6 4 4 1
1 3 6 7 9
""")

fun safe(report: List<Int>) = report.windowed(2).map { (a, b) -> b - a }.let { diffs ->
    (diffs.all { it < 0 } || diffs.all { it > 0 }) && diffs.all { it.absoluteValue <= 3 }
}

fun List<Int>.missingOne() = indices.asSequence().map { subList(0, it) + subList(it + 1, size) }

@PuzzleName("Red-Nosed Reports")
fun PuzzleInput.part1() = lines.map { it.ints() }.count(::safe)

fun PuzzleInput.part2() = lines.map { it.ints() }.count { it.missingOne().any(::safe) }
